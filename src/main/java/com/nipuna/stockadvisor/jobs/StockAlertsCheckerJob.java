package com.nipuna.stockadvisor.jobs;

import java.io.IOException;
import java.math.BigDecimal;
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

import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockStats;

@Component
@ConditionalOnProperty(prefix = "stockadvisor.jobs.stockalert", name = "schedule")
public class StockAlertsCheckerJob extends AbstractJob {

	private static Logger LOG = Logger.getLogger(StockAlertsCheckerJob.class.getName());

	@Autowired
	private WatchlistRepository watchListRepository;

	@Autowired
	private AlertHistoryRepository alertHistoryRepository;
	// @Autowired
	// private WatchlistCommentRepository watchlistCommentRepository;
	private static final Map<String, AlertChecker> CHECKER_MAP = new HashMap<>();

	@Scheduled(cron = "${stockadvisor.jobs.stockalert.schedule}", zone = "America/New_York")
	public void executeJob() throws IOException {

		try {
			List<Watchlist> items = watchListRepository.findAllWithEagerRelationships();

			if (items.isEmpty()) {
				LOG.info("~~~~ No WatchLists found ~~~~");
				ensureJobRunForMoreThanAMin();
				return;
			}

			Iterator<Watchlist> iter = items.iterator();
			List<String> symbols = new ArrayList<>();
			Map<String, Set<AlertType>> subscriptionMap = new HashMap<>();
			Map<String, Watchlist> watchListBySymbolMap = new HashMap<>();
			while (iter.hasNext()) {
				Watchlist watchlist = iter.next();
				Set<AlertType> subscriptions = watchlist.getAlerts();
				String symbol = watchlist.getSymbol();
				if (subscriptions == null || subscriptions.isEmpty()) {
					LOG.warn("No Alert subscriptions found for stock: " + symbol);
					continue;
				}
				symbols.add(symbol);
				subscriptionMap.put(symbol, subscriptions);
				watchListBySymbolMap.put(watchlist.getSymbol(), watchlist);
			}

			StringBuffer log = new StringBuffer();
			StringBuffer errorLog = new StringBuffer();

			log.append("Watchlist: " + symbols + "\n");
			errorLog.append("Watchlist: " + symbols + "\n");
			LOG.info("Watchlist: " + symbols + "\n");
			Map<String, Stock> stockMap = YahooFinance.get(symbols.toArray(new String[symbols.size()]));

			Set<Entry<String, Stock>> entries = stockMap.entrySet();
			for (Entry<String, Stock> entry : entries) {

				String lastAlert = "";
				int alertcount = 0;
				Stock stock = entry.getValue();
				String symbol = entry.getKey();
				StringBuffer alertDesc = new StringBuffer();
				StringBuffer alertNames = new StringBuffer();
				Set<AlertType> alerts = subscriptionMap.get(symbol);
				String stockInfo = getStockInfo(stock, watchListBySymbolMap.get(symbol));
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
						if (checker == null) {
							LOG.info("checker null for " + alertType.getFqdn() + " stock: " + stock.getSymbol());
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
							history.setDescription(StringEscapeUtils.escapeSql(checker.shortDesc()));
							history.setPriority(checker.getPriority());
							alertHistoryRepository.saveAndFlush(history);
							alertDesc.append("Multiple Alerts:\n" + checker.desc() + "\n\n");
							alertNames.append(alertType.getName() + " ");
							alertcount++;

							lastAlert = checker.desc();
						}
					}
				}

				if (alertcount > 0) {

					// TODO: eliminate stocks greater than $30 for now
					if (stock.getQuote().getPrice().doubleValue() <= 30) {
						// if only one alert, send that alert in email,
						// otherwise
						// group them as one.
						String subject = alertcount == 1 ? lastAlert
								: "Multiple alerts for " + stock.getSymbol() + " " + alertNames.toString();
						String body = alertDesc.toString() + "\n\n" + stockInfo;
						boolean debugMail = Boolean.valueOf(System.getenv("stockadvisor.mail.debug"));
						if (debugMail) {
							body += "LOG: " + log.toString() + " \n\n\n\n  ERROR LOG: " + errorLog.toString();
						}
						EmailSender.sendEmail(subject, body);
					}
				}
			}
			performAudit();
			LOG.info("Waitinig for NEXT RUN...");
		} catch (Exception e) {
			EmailSender.sendEmail("#####" + getJobId() + " EXCEPTION ##### ", "stacktrace:\n" + getStackTrace(e));
			ensureJobRunForMoreThanAMin();
		}
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

	private String getStockInfo(Stock stock, Watchlist watchlist) {

		StringBuffer sb = new StringBuffer();

		sb.append("Name:\n");
		sb.append("\t" + stock.getName() + "\n\n");

		sb.append("Price:\n");
		sb.append("\t" + stock.getQuote().getPrice() + "\n\n");

		sb.append("% Change in Price:\n");
		sb.append("\t" + stock.getQuote().getChangeInPercent() + "%\n\n");

		sb.append("Day Range:\n");
		sb.append("\t" + stock.getQuote().getDayLow() + "-" + stock.getQuote().getDayHigh() + "\n\n");

		// List<AlertHistory> historyItems =
		// alertHistoryRepository.findAlertHistoryByWatchListIdSorted();
		//
		// sb.append("Triggerd alerts:\n");
		// for (AlertHistory item : historyItems) {
		// sb.append("\t" + item.getTriggeredAt() +" - "+item.getDescription() +
		// "\n\n");
		// }

		// List<WatchlistComment> comments =
		// watchlistCommentRepository.findWatchListCommentsByWatchListId(watchlist.getId());
		//
		// sb.append("Comments:\n");
		// for (WatchlistComment watchlistComment : comments) {
		// sb.append("\t" + watchlistComment.getEntryDate().toString() +" -
		// "+watchlistComment.getComment() + "\n\n");
		// }

		// sb.append("10 Day Low:\n");
		// sb.append("20 Day Low:\n");
		// sb.append("50 Day Low:\n");
		// sb.append("100 Day Low:\n");

		sb.append("Year Range:\n");
		sb.append("\t" + stock.getQuote().getYearLow() + "-" + stock.getQuote().getYearHigh() + "\n\n");

		sb.append("Quote:\n");
		sb.append("\t" + stock.getQuote() + "\n\n");

		StockStats stats = stock.getStats();

		if (stats != null) {
			sb.append("Book Value per share:\n");
			sb.append("\t" + stats.getBookValuePerShare() + "\n\n");

			sb.append("1 Year price Target:\n");
			sb.append("\t" + stats.getOneYearTargetPrice() + "\n\n");

			sb.append("Market Cap:\n");
			BigDecimal marketCap = stats.getMarketCap();
			if (marketCap == null) {
				sb.append("\t NA \n\n");
			} else {
				sb.append("\t" + NumerToWordUtil.format(marketCap.longValue()) + "\n\n");
			}
		}

		sb.append("Volume:\n");
		sb.append("\t" + NumerToWordUtil.format(stock.getQuote().getVolume()) + "\n\n");

		sb.append("Avg Volume(3m):\n");
		sb.append("\t" + NumerToWordUtil.format(stock.getQuote().getAvgVolume()) + "\n\n");

		sb.append("50 DMA:\n");
		sb.append("\t" + stock.getQuote().getPriceAvg50() + "\n\n");

		sb.append("200 DMA:\n");
		sb.append("\t" + stock.getQuote().getPriceAvg200() + "\n\n");

		sb.append("Links:\n");
		sb.append("\thttp://seekingalpha.com/symbol/" + watchlist.getSymbol() + "\n\n");
		sb.append("\thttp://finviz.com/quote.ashx?t=" + watchlist.getSymbol() + "\n\n");
		sb.append("\thttp://stocktwits.com/symbol/" + watchlist.getSymbol() + "\n\n");

		sb.append("\n\n");
		return sb.toString();
	}
}
