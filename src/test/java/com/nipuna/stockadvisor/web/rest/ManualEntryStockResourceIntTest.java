package com.nipuna.stockadvisor.web.rest;

import com.nipuna.stockadvisor.StockadvisorApp;
import com.nipuna.stockadvisor.domain.ManualEntryStock;
import com.nipuna.stockadvisor.repository.ManualEntryStockRepository;

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
 * Test class for the ManualEntryStockResource REST controller.
 *
 * @see ManualEntryStockResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StockadvisorApp.class)
@WebAppConfiguration
@IntegrationTest
public class ManualEntryStockResourceIntTest {

    private static final String DEFAULT_SYMBOLS = "AAAAA";
    private static final String UPDATED_SYMBOLS = "BBBBB";

    private static final LocalDate DEFAULT_ENTRY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ENTRY_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final String DEFAULT_PROCESSED = "AAAAA";
    private static final String UPDATED_PROCESSED = "BBBBB";

    @Inject
    private ManualEntryStockRepository manualEntryStockRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restManualEntryStockMockMvc;

    private ManualEntryStock manualEntryStock;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ManualEntryStockResource manualEntryStockResource = new ManualEntryStockResource();
        ReflectionTestUtils.setField(manualEntryStockResource, "manualEntryStockRepository", manualEntryStockRepository);
        this.restManualEntryStockMockMvc = MockMvcBuilders.standaloneSetup(manualEntryStockResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        manualEntryStock = new ManualEntryStock();
        manualEntryStock.setSymbols(DEFAULT_SYMBOLS);
        manualEntryStock.setEntryDate(DEFAULT_ENTRY_DATE);
        manualEntryStock.setProcessed(DEFAULT_PROCESSED);
    }

    @Test
    @Transactional
    public void createManualEntryStock() throws Exception {
        int databaseSizeBeforeCreate = manualEntryStockRepository.findAll().size();

        // Create the ManualEntryStock

        restManualEntryStockMockMvc.perform(post("/api/manual-entry-stocks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(manualEntryStock)))
                .andExpect(status().isCreated());

        // Validate the ManualEntryStock in the database
        List<ManualEntryStock> manualEntryStocks = manualEntryStockRepository.findAll();
        assertThat(manualEntryStocks).hasSize(databaseSizeBeforeCreate + 1);
        ManualEntryStock testManualEntryStock = manualEntryStocks.get(manualEntryStocks.size() - 1);
        assertThat(testManualEntryStock.getSymbols()).isEqualTo(DEFAULT_SYMBOLS);
        assertThat(testManualEntryStock.getEntryDate()).isEqualTo(DEFAULT_ENTRY_DATE);
        assertThat(testManualEntryStock.getProcessed()).isEqualTo(DEFAULT_PROCESSED);
    }

    @Test
    @Transactional
    public void checkSymbolsIsRequired() throws Exception {
        int databaseSizeBeforeTest = manualEntryStockRepository.findAll().size();
        // set the field null
        manualEntryStock.setSymbols(null);

        // Create the ManualEntryStock, which fails.

        restManualEntryStockMockMvc.perform(post("/api/manual-entry-stocks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(manualEntryStock)))
                .andExpect(status().isBadRequest());

        List<ManualEntryStock> manualEntryStocks = manualEntryStockRepository.findAll();
        assertThat(manualEntryStocks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEntryDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = manualEntryStockRepository.findAll().size();
        // set the field null
        manualEntryStock.setEntryDate(null);

        // Create the ManualEntryStock, which fails.

        restManualEntryStockMockMvc.perform(post("/api/manual-entry-stocks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(manualEntryStock)))
                .andExpect(status().isBadRequest());

        List<ManualEntryStock> manualEntryStocks = manualEntryStockRepository.findAll();
        assertThat(manualEntryStocks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkProcessedIsRequired() throws Exception {
        int databaseSizeBeforeTest = manualEntryStockRepository.findAll().size();
        // set the field null
        manualEntryStock.setProcessed(null);

        // Create the ManualEntryStock, which fails.

        restManualEntryStockMockMvc.perform(post("/api/manual-entry-stocks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(manualEntryStock)))
                .andExpect(status().isBadRequest());

        List<ManualEntryStock> manualEntryStocks = manualEntryStockRepository.findAll();
        assertThat(manualEntryStocks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllManualEntryStocks() throws Exception {
        // Initialize the database
        manualEntryStockRepository.saveAndFlush(manualEntryStock);

        // Get all the manualEntryStocks
        restManualEntryStockMockMvc.perform(get("/api/manual-entry-stocks?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(manualEntryStock.getId().intValue())))
                .andExpect(jsonPath("$.[*].symbols").value(hasItem(DEFAULT_SYMBOLS.toString())))
                .andExpect(jsonPath("$.[*].entryDate").value(hasItem(DEFAULT_ENTRY_DATE.toString())))
                .andExpect(jsonPath("$.[*].processed").value(hasItem(DEFAULT_PROCESSED.toString())));
    }

    @Test
    @Transactional
    public void getManualEntryStock() throws Exception {
        // Initialize the database
        manualEntryStockRepository.saveAndFlush(manualEntryStock);

        // Get the manualEntryStock
        restManualEntryStockMockMvc.perform(get("/api/manual-entry-stocks/{id}", manualEntryStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(manualEntryStock.getId().intValue()))
            .andExpect(jsonPath("$.symbols").value(DEFAULT_SYMBOLS.toString()))
            .andExpect(jsonPath("$.entryDate").value(DEFAULT_ENTRY_DATE.toString()))
            .andExpect(jsonPath("$.processed").value(DEFAULT_PROCESSED.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingManualEntryStock() throws Exception {
        // Get the manualEntryStock
        restManualEntryStockMockMvc.perform(get("/api/manual-entry-stocks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateManualEntryStock() throws Exception {
        // Initialize the database
        manualEntryStockRepository.saveAndFlush(manualEntryStock);
        int databaseSizeBeforeUpdate = manualEntryStockRepository.findAll().size();

        // Update the manualEntryStock
        ManualEntryStock updatedManualEntryStock = new ManualEntryStock();
        updatedManualEntryStock.setId(manualEntryStock.getId());
        updatedManualEntryStock.setSymbols(UPDATED_SYMBOLS);
        updatedManualEntryStock.setEntryDate(UPDATED_ENTRY_DATE);
        updatedManualEntryStock.setProcessed(UPDATED_PROCESSED);

        restManualEntryStockMockMvc.perform(put("/api/manual-entry-stocks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedManualEntryStock)))
                .andExpect(status().isOk());

        // Validate the ManualEntryStock in the database
        List<ManualEntryStock> manualEntryStocks = manualEntryStockRepository.findAll();
        assertThat(manualEntryStocks).hasSize(databaseSizeBeforeUpdate);
        ManualEntryStock testManualEntryStock = manualEntryStocks.get(manualEntryStocks.size() - 1);
        assertThat(testManualEntryStock.getSymbols()).isEqualTo(UPDATED_SYMBOLS);
        assertThat(testManualEntryStock.getEntryDate()).isEqualTo(UPDATED_ENTRY_DATE);
        assertThat(testManualEntryStock.getProcessed()).isEqualTo(UPDATED_PROCESSED);
    }

    @Test
    @Transactional
    public void deleteManualEntryStock() throws Exception {
        // Initialize the database
        manualEntryStockRepository.saveAndFlush(manualEntryStock);
        int databaseSizeBeforeDelete = manualEntryStockRepository.findAll().size();

        // Get the manualEntryStock
        restManualEntryStockMockMvc.perform(delete("/api/manual-entry-stocks/{id}", manualEntryStock.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ManualEntryStock> manualEntryStocks = manualEntryStockRepository.findAll();
        assertThat(manualEntryStocks).hasSize(databaseSizeBeforeDelete - 1);
    }
}
