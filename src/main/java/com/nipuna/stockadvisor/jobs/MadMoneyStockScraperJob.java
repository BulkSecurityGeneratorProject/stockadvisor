package com.nipuna.stockadvisor.jobs;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nipuna.stockadvisor.domain.Source;
import com.nipuna.stockadvisor.domain.Watchlist;
import com.nipuna.stockadvisor.repository.SourceRepository;
import com.nipuna.stockadvisor.repository.WatchlistRepository;
import com.nipuna.stockadvisor.util.EmailSender;

@Component
public class MadMoneyStockScraperJob extends AbstractJob {

	@Autowired
	private WatchlistRepository watchListRepository;
	@Autowired
	private SourceRepository sourceRepository;

	// @Scheduled(cron = "0 0 9 * * MON-FRI",zone="America/New_York")
	@Scheduled(fixedRate = 30000,initialDelay=60000)
	public void executeJob() throws IOException {
		String url = "http://madmoney.thestreet.com/screener/index.cfm?showview=stocks&showrows=500";
		Document doc = Jsoup.connect(url).get();
		// Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
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

				Watchlist item = new Watchlist();
				item.setSymbol(symbol);
				item.setEntryPrice(Double.parseDouble(price.replace('$', ' ').trim()));
				item.setEntryDate(LocalDate.now(ZONEID_EST));
				Source source = sourceRepository.findOne(1L);

				if (source == null) {
					source = new Source();
					source.setName("MADMONEY");
					sourceRepository.save(source);
				}

				item.getSources().add(source);
				watchListRepository.save(item);

				sb.append(symbol).append(" ").append(name).append(" ").append(date).append(" ").append(segment)
						.append(" ").append(call).append(" ").append(price);

				System.out.println(sb.toString());
				// sb.setLength(0);
				sb.append("\n");
			}
		}

		EmailSender.sendEmail("Mad Money Stocks for " + new Date().toString(), sb.toString());
	}
}