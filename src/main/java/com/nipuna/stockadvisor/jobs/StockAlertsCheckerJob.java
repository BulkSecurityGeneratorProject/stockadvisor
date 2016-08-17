package com.nipuna.stockadvisor.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nipuna.stockadvisor.checkers.AlertChecker;
import com.nipuna.stockadvisor.domain.AlertType;
import com.nipuna.stockadvisor.domain.Watchlist;
import com.nipuna.stockadvisor.repository.AlertTypeRepository;
import com.nipuna.stockadvisor.repository.WatchlistRepository;
import com.nipuna.stockadvisor.util.EmailSender;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@Component
public class StockAlertsCheckerJob extends AbstractJob {

	private static Logger LOG = Logger.getLogger(StockAlertsCheckerJob.class.getName());

	@Autowired
	private WatchlistRepository watchListRepository;

	@Autowired
	private AlertTypeRepository alertTypeRepository;

	private static final Map<String, AlertChecker> CHECKER_MAP = new HashMap<>();

	StringBuffer sb = new StringBuffer();

	@Scheduled(fixedRate = 30000, initialDelay = 60000)
	// @Scheduled(cron = "* 0/15 9-17 * * MON-FRI",zone="America/New_York")
	// @Scheduled(cron = "* 0/17 7-17 * * MON-FRI")
	// At every 0, 15, 30 and 45th minute past the 9, 10, 11, 12, 13, 14, 15, 16
	// and 17th hour on Mon, Tue, Wed, Thu and Fri.
	// http://crontab.guru/#0/15_9-17_*_*_MON-FRI
	public void executeJob() throws IOException {

		List<Watchlist> items = watchListRepository.findAll();

		Iterator<Watchlist> iter = items.iterator();
		List<String> symbols = new ArrayList<>();
		Map<String, Set<AlertType>> subscriptionMap = new HashMap<>();
		while (iter.hasNext()) {
			Watchlist watchlist = iter.next();
			Set<AlertType> subscriptions = watchlist.getAlerts();
			String symbol = watchlist.getSymbol();
			symbols.add(symbol);
			subscriptionMap.put(symbol, subscriptions);
		}

		StringBuffer log = new StringBuffer();

		log.append("Watchlist: " + symbols + "\n");
		Map<String, Stock> stockMap = YahooFinance.get(symbols.toArray(new String[symbols.size()]));

		Set<Entry<String, Stock>> entries = stockMap.entrySet();
		for (Entry<String, Stock> entry : entries) {
			Stock stock = entry.getValue();
			String symbol = entry.getKey();

			Set<AlertType> alerts = subscriptionMap.get(symbol);
			for (AlertType alertType : alerts) {
				AlertChecker checker = ensureCheckerCached(alertType);
				checker.setParam(alertType.getParamType(), alertType.getParamValue());
				checker.setStock(stock);

				log.append("checking " + symbol + " for " + alertType + "\n");

				if (checker.check()) {
					sb.append(checker.desc() + "\n");
					EmailSender.sendEmail(checker.desc(),
							stock.getQuote().toString() + "\n" + sb.toString() + "\n\n\n\n" + log.toString());

					// TODO removal
					// subscriptions.get(symbol).remove(alertType);
				}
			}

		}

		LOG.info("Waitinig for NEXT RUN...");

	}

	private AlertChecker ensureCheckerCached(AlertType alertType) {

		String fqdn = "com.nipuna.stockadvisor.checkers" + alertType.getFqdn();
		AlertChecker temp = CHECKER_MAP.get(fqdn);
		if (temp == null) {
			try {
				CHECKER_MAP.put(fqdn, (AlertChecker) Class.forName(fqdn).newInstance());
			} catch (Exception e) {
				LOG.error("Could not instantiate checker: " + fqdn);
			}
		}

		return temp;
	}
}