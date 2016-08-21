package com.nipuna.stockadvisor.jobs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nipuna.stockadvisor.domain.AlertType;
import com.nipuna.stockadvisor.domain.ManualEntryStock;
import com.nipuna.stockadvisor.domain.Source;
import com.nipuna.stockadvisor.domain.Watchlist;
import com.nipuna.stockadvisor.repository.AlertTypeRepository;
import com.nipuna.stockadvisor.repository.ManualEntryStockRepository;
import com.nipuna.stockadvisor.repository.SourceRepository;
import com.nipuna.stockadvisor.repository.WatchlistRepository;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

@Component
@ConditionalOnProperty(prefix = "stockadvisor.jobs.manualstockentry", name = "schedule")
public class ManualStockEntryCheckerJob extends AbstractJob {
	private static Logger LOG = Logger.getLogger(ManualStockEntryCheckerJob.class.getName());

	@Autowired
	private ManualEntryStockRepository manualEntryStockRepository;
	@Autowired
	private WatchlistRepository watchListRepository;
	@Autowired
	private SourceRepository sourceRepository;
	@Autowired
	private AlertTypeRepository alertTypeRepository;
	
	
	@Scheduled(cron = "${stockadvisor.jobs.manualstockentry.schedule}", zone = "America/New_York")
	public void executeJob() throws IOException {

		Source source = sourceRepository.findOneByName("UNKNOWN");
		List<AlertType> allAlertTypes = alertTypeRepository.findAll();
		List<ManualEntryStock> manualEntries = manualEntryStockRepository.findManualEntryStockByProcessingStatus("N");
		
		Set<String> symbolsToAdd = new HashSet<>();
		
		if(manualEntries.isEmpty()){
			LOG.info("~~~~~~~~~~ No Manual Entry stock data to process ~~~~~~~~~~");
			return;
		}
		for (ManualEntryStock manualEntryStock : manualEntries) {
			String text = manualEntryStock.getSymbols();
			String[] symbols = text.replaceAll("\\s", ",").split(",");
			symbolsToAdd.addAll(Arrays.asList(symbols));
		}
		
		LOG.info("SYMBOLS to ADD (RAW): "+symbolsToAdd);
		
		List<Watchlist> foundWatchLists = watchListRepository.findWatchListBySymbols(symbolsToAdd.toArray(new String[symbolsToAdd.size()]));
		Set<String> existingSymbols = new HashSet<String>();
		for (Watchlist each : foundWatchLists) {
			existingSymbols.add(each.getSymbol());
		}
		
		symbolsToAdd.removeAll(existingSymbols);
		LOG.info("SYMBOLS to ADD (AFTER DIFF): "+symbolsToAdd);
		
		Map<String, Stock> stockMap = YahooFinance.get(symbolsToAdd.toArray(new String[symbolsToAdd.size()]));
		
		Set<Entry<String, Stock>> entries = stockMap.entrySet();
		for (Entry<String, Stock> entry : entries) {
			Stock stock = entry.getValue();
			String symbol = entry.getKey();
			
			Watchlist item = new Watchlist();
			item.setSymbol(symbol);
			item.setEntryPrice(stock.getQuote().getPrice().doubleValue());
			item.setEntryDate(LocalDate.now());
			item.getSources().add(source);
			item.getAlerts().addAll(allAlertTypes);
			watchListRepository.save(item);
			
		}
		
		for (ManualEntryStock manualEntryStock : manualEntries) {
			manualEntryStock.setProcessed("Y");
		}
		
		manualEntryStockRepository.save(manualEntries);
		
		performAudit();
		LOG.info("Waitinig for NEXT RUN...");
	}

}