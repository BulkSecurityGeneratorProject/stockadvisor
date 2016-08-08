package com.nipuna.stockadvisor.web.rest;

import com.nipuna.stockadvisor.StockadvisorApp;
import com.nipuna.stockadvisor.domain.Watchlist;
import com.nipuna.stockadvisor.repository.WatchlistRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the WatchlistResource REST controller.
 *
 * @see WatchlistResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StockadvisorApp.class)
@WebAppConfiguration
@IntegrationTest
public class WatchlistResourceIntTest {

    private static final String DEFAULT_SYMBOL = "AAAAA";
    private static final String UPDATED_SYMBOL = "BBBBB";

    private static final Double DEFAULT_ENTRY_PRICE = 1D;
    private static final Double UPDATED_ENTRY_PRICE = 2D;

    private static final LocalDate DEFAULT_ENTRY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ENTRY_DATE = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private WatchlistRepository watchlistRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restWatchlistMockMvc;

    private Watchlist watchlist;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WatchlistResource watchlistResource = new WatchlistResource();
        ReflectionTestUtils.setField(watchlistResource, "watchlistRepository", watchlistRepository);
        this.restWatchlistMockMvc = MockMvcBuilders.standaloneSetup(watchlistResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        watchlist = new Watchlist();
        watchlist.setSymbol(DEFAULT_SYMBOL);
        watchlist.setEntryPrice(DEFAULT_ENTRY_PRICE);
        watchlist.setEntryDate(DEFAULT_ENTRY_DATE);
    }

    @Test
    @Transactional
    public void createWatchlist() throws Exception {
        int databaseSizeBeforeCreate = watchlistRepository.findAll().size();

        // Create the Watchlist

        restWatchlistMockMvc.perform(post("/api/watchlists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(watchlist)))
                .andExpect(status().isCreated());

        // Validate the Watchlist in the database
        List<Watchlist> watchlists = watchlistRepository.findAll();
        assertThat(watchlists).hasSize(databaseSizeBeforeCreate + 1);
        Watchlist testWatchlist = watchlists.get(watchlists.size() - 1);
        assertThat(testWatchlist.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testWatchlist.getEntryPrice()).isEqualTo(DEFAULT_ENTRY_PRICE);
        assertThat(testWatchlist.getEntryDate()).isEqualTo(DEFAULT_ENTRY_DATE);
    }

    @Test
    @Transactional
    public void checkSymbolIsRequired() throws Exception {
        int databaseSizeBeforeTest = watchlistRepository.findAll().size();
        // set the field null
        watchlist.setSymbol(null);

        // Create the Watchlist, which fails.

        restWatchlistMockMvc.perform(post("/api/watchlists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(watchlist)))
                .andExpect(status().isBadRequest());

        List<Watchlist> watchlists = watchlistRepository.findAll();
        assertThat(watchlists).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEntryPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = watchlistRepository.findAll().size();
        // set the field null
        watchlist.setEntryPrice(null);

        // Create the Watchlist, which fails.

        restWatchlistMockMvc.perform(post("/api/watchlists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(watchlist)))
                .andExpect(status().isBadRequest());

        List<Watchlist> watchlists = watchlistRepository.findAll();
        assertThat(watchlists).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEntryDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = watchlistRepository.findAll().size();
        // set the field null
        watchlist.setEntryDate(null);

        // Create the Watchlist, which fails.

        restWatchlistMockMvc.perform(post("/api/watchlists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(watchlist)))
                .andExpect(status().isBadRequest());

        List<Watchlist> watchlists = watchlistRepository.findAll();
        assertThat(watchlists).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWatchlists() throws Exception {
        // Initialize the database
        watchlistRepository.saveAndFlush(watchlist);

        // Get all the watchlists
        restWatchlistMockMvc.perform(get("/api/watchlists?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(watchlist.getId().intValue())))
                .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL.toString())))
                .andExpect(jsonPath("$.[*].entryPrice").value(hasItem(DEFAULT_ENTRY_PRICE.doubleValue())))
                .andExpect(jsonPath("$.[*].entryDate").value(hasItem(DEFAULT_ENTRY_DATE.toString())));
    }

    @Test
    @Transactional
    public void getWatchlist() throws Exception {
        // Initialize the database
        watchlistRepository.saveAndFlush(watchlist);

        // Get the watchlist
        restWatchlistMockMvc.perform(get("/api/watchlists/{id}", watchlist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(watchlist.getId().intValue()))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL.toString()))
            .andExpect(jsonPath("$.entryPrice").value(DEFAULT_ENTRY_PRICE.doubleValue()))
            .andExpect(jsonPath("$.entryDate").value(DEFAULT_ENTRY_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWatchlist() throws Exception {
        // Get the watchlist
        restWatchlistMockMvc.perform(get("/api/watchlists/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWatchlist() throws Exception {
        // Initialize the database
        watchlistRepository.saveAndFlush(watchlist);
        int databaseSizeBeforeUpdate = watchlistRepository.findAll().size();

        // Update the watchlist
        Watchlist updatedWatchlist = new Watchlist();
        updatedWatchlist.setId(watchlist.getId());
        updatedWatchlist.setSymbol(UPDATED_SYMBOL);
        updatedWatchlist.setEntryPrice(UPDATED_ENTRY_PRICE);
        updatedWatchlist.setEntryDate(UPDATED_ENTRY_DATE);

        restWatchlistMockMvc.perform(put("/api/watchlists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedWatchlist)))
                .andExpect(status().isOk());

        // Validate the Watchlist in the database
        List<Watchlist> watchlists = watchlistRepository.findAll();
        assertThat(watchlists).hasSize(databaseSizeBeforeUpdate);
        Watchlist testWatchlist = watchlists.get(watchlists.size() - 1);
        assertThat(testWatchlist.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testWatchlist.getEntryPrice()).isEqualTo(UPDATED_ENTRY_PRICE);
        assertThat(testWatchlist.getEntryDate()).isEqualTo(UPDATED_ENTRY_DATE);
    }

    @Test
    @Transactional
    public void deleteWatchlist() throws Exception {
        // Initialize the database
        watchlistRepository.saveAndFlush(watchlist);
        int databaseSizeBeforeDelete = watchlistRepository.findAll().size();

        // Get the watchlist
        restWatchlistMockMvc.perform(delete("/api/watchlists/{id}", watchlist.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Watchlist> watchlists = watchlistRepository.findAll();
        assertThat(watchlists).hasSize(databaseSizeBeforeDelete - 1);
    }
}
