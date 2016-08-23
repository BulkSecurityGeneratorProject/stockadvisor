package com.nipuna.stockadvisor.jobs;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nipuna.stockadvisor.checkers.AlertChecker;
import com.nipuna.stockadvisor.domain.AlertHistory;
import com.nipuna.stockadvisor.domain.AlertType;
import com.nipuna.stockadvisor.domain.Watchlist;
import com.nipuna.stockadvisor.repository.AlertHistoryRepository;
import com.nipuna.stockadvisor.repository.WatchlistRepository;
import com.nipuna.stockadvisor.util.EmailSender;
import com.nipuna.stockadvisor.util.NumerToWordUtil;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@Component
@ConditionalOnProperty(prefix = "stockadvisor.jobs.stockalert", name = "schedule")
public class StockAlertsCheckerJob extends AbstractJob {

	private static Logger LOG = Logger.getLogger(StockAlertsCheckerJob.class.getName());

	@Autowired
	private WatchlistRepository watchListRepository;

	@Autowired
	private AlertHistoryRepository alertHistoryRepository;

	private static final Map<String, AlertChecker> CHECKER_MAP = new HashMap<>();

	@Scheduled(cron = "${stockadvisor.jobs.stockalert.schedule}", zone = "America/New_York")
	public void executeJob() throws IOException {
		List<Watchlist> items = watchListRepository.findAll();

		Iterator<Watchlist> iter = items.iterator();
		List<String> symbols = new ArrayList<>();
		Map<String, Set<AlertType>> subscriptionMap = new HashMap<>();
		Map<String, Watchlist> watchListBySymbolMap = new HashMap<>();
		while (iter.hasNext()) {
			Watchlist watchlist = iter.next();
			Set<AlertType> subscriptions = watchlist.getAlerts();
			String symbol = watchlist.getSymbol();
			symbols.add(symbol);
			subscriptionMap.put(symbol, subscriptions);

			watchListBySymbolMap.put(watchlist.getSymbol(), watchlist);
		}

		if (symbols.isEmpty()) {
			LOG.info("~~~~ No WatchLists found ~~~~");
			return;
		}

		StringBuffer log = new StringBuffer();
		StringBuffer errorLog = new StringBuffer();

		log.append("Watchlist: " + symbols + "\n");
		errorLog.append("Watchlist: " + symbols + "\n");
		LOG.info("Watchlist: " + symbols + "\n");
		Map<String, Stock> stockMap = YahooFinance.get(symbols.toArray(new String[symbols.size()]));

		Set<Entry<String, Stock>> entries = stockMap.entrySet();
		for (Entry<String, Stock> entry : entries) {
			Stock stock = entry.getValue();
			String symbol = entry.getKey();

			Set<AlertType> alerts = subscriptionMap.get(symbol);
			if (alerts != null) {
				for (AlertType alertType : alerts) {
					AlertChecker checker = null;
					try {
						checker = ensureCheckerCached(alertType);
					} catch (Exception e) {
						errorLog.append(e.getMessage() + "\n");
						LOG.error("Error while checking if checker cached ", e);
						continue;
					}
					checker.setParam(alertType.getParamType(), alertType.getParamValue());
					checker.setStock(stock);
					LOG.debug("checking " + symbol + " for " + alertType);
					log.append("checking " + symbol + " for " + alertType + "\n");

					if (checker.check()) {
						AlertHistory history = new AlertHistory();
						history.setTriggeredAt(ZonedDateTime.now());
						history.setWatchlist(watchListBySymbolMap.get(symbol));
						alertHistoryRepository.save(history);
						EmailSender.sendEmail(checker.desc(), getStockInfo(stock) + "\n\n\n\n  LOG: "
								+ log.toString() + " \n\n\n\n  ERROR LOG: " + errorLog.toString());

					}
				}
			}
		}
		performAudit();
		LOG.info("Waitinig for NEXT RUN...");
	}

	private AlertChecker ensureCheckerCached(AlertType alertType) throws Exception {

		String fqdn = "com.nipuna.stockadvisor.checkers." + alertType.getFqdn();
		AlertChecker temp = CHECKER_MAP.get(fqdn);
		if (temp == null) {
			try {
				CHECKER_MAP.put(fqdn, (AlertChecker) Class.forName(fqdn).newInstance());
			} catch (Exception e) {
				throw new Exception("Could not instantiate checker: " + fqdn, e);
			}
		}

		return temp;
	}

	private String getStockInfo(Stock stock) {

		StringBuffer sb = new StringBuffer();

		sb.append("Price:\n");
		sb.append("\t" + stock.getQuote().getPrice() + "\n\n");

		sb.append("Year Range:\n");
		sb.append("\t" + stock.getQuote().getYearLow() + "-" + stock.getQuote().getYearHigh() + "\n\n");

		sb.append("Quote:\n");
		sb.append("\t" + stock.getQuote() + "\n\n");

		sb.append("Book Value per share:\n");
		sb.append("\t" + stock.getStats().getBookValuePerShare().doubleValue() + "\n\n");

		sb.append("1 Year price Target:\n");
		sb.append("\t" + stock.getStats().getOneYearTargetPrice().doubleValue() + "\n\n");

		sb.append("Market Cap:\n");
		sb.append("\t" + NumerToWordUtil.format(stock.getStats().getMarketCap().longValue()) + "\n\n");

		sb.append("Volume:\n");
		sb.append("\t" + NumerToWordUtil.format(stock.getQuote().getVolume()) + "\n\n");

		sb.append("Avg Volume(3m):\n");
		sb.append("\t" + NumerToWordUtil.format(stock.getQuote().getAvgVolume()) + "\n\n");

		sb.append("50 DMA:\n");
		sb.append("\t" + stock.getQuote().getPriceAvg50() + "\n\n");

		sb.append("200 DMA:\n");
		sb.append("\t" + stock.getQuote().getPriceAvg200() + "\n\n");

		return sb.toString();
	}
}