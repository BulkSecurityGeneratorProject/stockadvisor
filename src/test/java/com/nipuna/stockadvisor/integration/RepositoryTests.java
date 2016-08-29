package com.nipuna.stockadvisor.integration;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.nipuna.stockadvisor.StockadvisorApp;
import com.nipuna.stockadvisor.domain.AlertType;
import com.nipuna.stockadvisor.domain.Source;
import com.nipuna.stockadvisor.domain.Watchlist;
import com.nipuna.stockadvisor.repository.AlertHistoryRepository;
import com.nipuna.stockadvisor.repository.AlertTypeRepository;
import com.nipuna.stockadvisor.repository.JobLogRepository;
import com.nipuna.stockadvisor.repository.RepositoryConfig;
import com.nipuna.stockadvisor.repository.SourceRepository;
import com.nipuna.stockadvisor.repository.WatchlistRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StockadvisorApp.class)
@WebAppConfiguration
@IntegrationTest
public class RepositoryTests {
	
	@Autowired
	private WatchlistRepository watchlistRepository;
	
	@Autowired
	private AlertHistoryRepository alertHistoryRepository;
	
	@Autowired
	private JobLogRepository jobLogRepository;
	
	@Autowired
	private AlertTypeRepository alertTypeRepository;
	
	@Autowired
	private SourceRepository sourceRepository;
	private Watchlist newWatchlist;
	
	
	@Before
	public void setup(){
		
		Source source = sourceRepository.findOneByName("Mad Money");
		List<AlertType> alertTypes = alertTypeRepository.findAll();
		newWatchlist = new Watchlist();
		newWatchlist.setSymbol("Dummy");
		newWatchlist.setEntryDate(LocalDate.now());
		newWatchlist.getAlerts().addAll(alertTypes);
		newWatchlist.getSources().add(source);
	}
	
	@Test
	public void newWatchlist(){
		Watchlist saved = watchlistRepository.saveAndFlush(newWatchlist);
		
		assertNotNull(saved.getId());
		assertEquals("Dummy",saved.getSymbol());
	}
	

}
