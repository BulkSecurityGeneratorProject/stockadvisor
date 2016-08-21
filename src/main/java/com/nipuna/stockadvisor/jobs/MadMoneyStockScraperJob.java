package com.nipuna.stockadvisor.jobs;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nipuna.stockadvisor.domain.AlertType;
import com.nipuna.stockadvisor.domain.Source;
import com.nipuna.stockadvisor.domain.Watchlist;
import com.nipuna.stockadvisor.repository.AlertTypeRepository;
import com.nipuna.stockadvisor.repository.SourceRepository;
import com.nipuna.stockadvisor.repository.WatchlistRepository;
import com.nipuna.stockadvisor.util.EmailSender;

@Component
@ConditionalOnProperty(prefix = "stockadvisor.jobs.madmoney", name = "schedule")
public class MadMoneyStockScraperJob extends AbstractJob {

	@Autowired
	private WatchlistRepository watchListRepository;
	@Autowired
	private SourceRepository sourceRepository;
	@Autowired
	private AlertTypeRepository alertTypeRepository;

	@Scheduled(cron = "${stockadvisor.jobs.madmoney.schedule}",zone="America/New_York")
	public void executeJob() throws IOException {

		Source source = sourceRepository.findOneByName("Mad Money");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		String url = "http://madmoney.thestreet.com/screener/index.cfm?showview=stocks&showrows=500";
		Document doc = Jsoup.connect(url).get();

		List<AlertType> allAlertTypes = alertTypeRepository.findAll();
		
		String callDate = doc.select("#leftPanel p").text().split(":")[1].trim();
		List<Watchlist> foundWatchLists = watchListRepository.findWatchListEnteredByDateAndSource(source.getName(),
				LocalDate.parse(callDate, formatter));
		Set<String> existingSymbols = new HashSet<String>();
		for (Watchlist watchlist : foundWatchLists) {
			existingSymbols.add(watchlist.getSymbol());
		}
		int count = 0;
		Elements rows = doc.select("#stockTable tr");
		StringBuilder sb = new StringBuilder();
		for (Element row : rows) {
			Elements tdList = row.select("td");
			if (tdList.size() == 6) {
				String stockText = tdList.get(0).text();
				String[] stockTextTokens = stockText.split("\\(");
				String name = stockTextTokens[0].trim();
				String symbol = stockTextTokens[1].replaceFirst("\\)", "").trim();
				String date = tdList.get(1).text();
				String segment = tdList.get(2).select("img").attr("alt");
				String call = tdList.get(3).select("img").attr("alt");
				String price = tdList.get(4).text();

				if (existingSymbols.contains(symbol)) {
					
					Watchlist item = new Watchlist();
					item.setSymbol(symbol);
					item.setEntryPrice(Double.parseDouble(price.replace('$', ' ').trim()));
					item.setEntryDate(LocalDate.parse(date + LocalDate.now().getYear(), formatter));// LocalDate.now(ZONEID_EST)
					item.getSources().add(source);
					item.getAlerts().addAll(allAlertTypes);
					watchListRepository.save(item);
					count++;
					sb.append(symbol).append(" ").append(name).append(" ").append(date).append(" ").append(segment)
							.append(" ").append(call).append(" ").append(price);

					// sb.setLength(0);
					sb.append("\n");

				} else {
					LOG.info("Mad Money stock " + symbol + " already present");
				}
			}
		}
		
		if(count > 0){
			EmailSender.sendEmail("Mad Money Stocks for " + callDate, sb.toString());
		}
		
		performAudit();
	}
}