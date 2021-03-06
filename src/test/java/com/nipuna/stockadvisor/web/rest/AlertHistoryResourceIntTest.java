package com.nipuna.stockadvisor.web.rest;

import com.nipuna.stockadvisor.StockadvisorApp;
import com.nipuna.stockadvisor.domain.AlertHistory;
import com.nipuna.stockadvisor.repository.AlertHistoryRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nipuna.stockadvisor.domain.enumeration.AlertPriority;
/**
 * Test class for the AlertHistoryResource REST controller.
 *
 * @see AlertHistoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StockadvisorApp.class)
public class AlertHistoryResourceIntTest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final ZonedDateTime DEFAULT_TRIGGERED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_TRIGGERED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_TRIGGERED_AT_STR = dateTimeFormatter.format(DEFAULT_TRIGGERED_AT);
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final AlertPriority DEFAULT_PRIORITY = AlertPriority.HIGH;
    private static final AlertPriority UPDATED_PRIORITY = AlertPriority.MEDIUM;

    @Inject
    private AlertHistoryRepository alertHistoryRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restAlertHistoryMockMvc;

    private AlertHistory alertHistory;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AlertHistoryResource alertHistoryResource = new AlertHistoryResource();
        ReflectionTestUtils.setField(alertHistoryResource, "alertHistoryRepository", alertHistoryRepository);
        this.restAlertHistoryMockMvc = MockMvcBuilders.standaloneSetup(alertHistoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AlertHistory createEntity(EntityManager em) {
        AlertHistory alertHistory = new AlertHistory();
        alertHistory = new AlertHistory();
        alertHistory.setTriggeredAt(DEFAULT_TRIGGERED_AT);
        alertHistory.setDescription(DEFAULT_DESCRIPTION);
        alertHistory.setPriority(DEFAULT_PRIORITY);
        return alertHistory;
    }

    @Before
    public void initTest() {
        alertHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createAlertHistory() throws Exception {
        int databaseSizeBeforeCreate = alertHistoryRepository.findAll().size();

        // Create the AlertHistory

        restAlertHistoryMockMvc.perform(post("/api/alert-histories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alertHistory)))
                .andExpect(status().isCreated());

        // Validate the AlertHistory in the database
        List<AlertHistory> alertHistories = alertHistoryRepository.findAll();
        assertThat(alertHistories).hasSize(databaseSizeBeforeCreate + 1);
        AlertHistory testAlertHistory = alertHistories.get(alertHistories.size() - 1);
        assertThat(testAlertHistory.getTriggeredAt()).isEqualTo(DEFAULT_TRIGGERED_AT);
        assertThat(testAlertHistory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAlertHistory.getPriority()).isEqualTo(DEFAULT_PRIORITY);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = alertHistoryRepository.findAll().size();
        // set the field null
        alertHistory.setDescription(null);

        // Create the AlertHistory, which fails.

        restAlertHistoryMockMvc.perform(post("/api/alert-histories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alertHistory)))
                .andExpect(status().isBadRequest());

        List<AlertHistory> alertHistories = alertHistoryRepository.findAll();
        assertThat(alertHistories).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPriorityIsRequired() throws Exception {
        int databaseSizeBeforeTest = alertHistoryRepository.findAll().size();
        // set the field null
        alertHistory.setPriority(null);

        // Create the AlertHistory, which fails.

        restAlertHistoryMockMvc.perform(post("/api/alert-histories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alertHistory)))
                .andExpect(status().isBadRequest());

        List<AlertHistory> alertHistories = alertHistoryRepository.findAll();
        assertThat(alertHistories).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAlertHistories() throws Exception {
        // Initialize the database
        alertHistoryRepository.saveAndFlush(alertHistory);

        // Get all the alertHistories
        restAlertHistoryMockMvc.perform(get("/api/alert-histories?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(alertHistory.getId().intValue())))
                .andExpect(jsonPath("$.[*].triggeredAt").value(hasItem(DEFAULT_TRIGGERED_AT_STR)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())));
    }

    @Test
    @Transactional
    public void getAlertHistory() throws Exception {
        // Initialize the database
        alertHistoryRepository.saveAndFlush(alertHistory);

        // Get the alertHistory
        restAlertHistoryMockMvc.perform(get("/api/alert-histories/{id}", alertHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(alertHistory.getId().intValue()))
            .andExpect(jsonPath("$.triggeredAt").value(DEFAULT_TRIGGERED_AT_STR))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAlertHistory() throws Exception {
        // Get the alertHistory
        restAlertHistoryMockMvc.perform(get("/api/alert-histories/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAlertHistory() throws Exception {
        // Initialize the database
        alertHistoryRepository.saveAndFlush(alertHistory);
        int databaseSizeBeforeUpdate = alertHistoryRepository.findAll().size();

        // Update the alertHistory
        AlertHistory updatedAlertHistory = alertHistoryRepository.findOne(alertHistory.getId());
        updatedAlertHistory.setTriggeredAt(UPDATED_TRIGGERED_AT);
        updatedAlertHistory.setDescription(UPDATED_DESCRIPTION);
        updatedAlertHistory.setPriority(UPDATED_PRIORITY);

        restAlertHistoryMockMvc.perform(put("/api/alert-histories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAlertHistory)))
                .andExpect(status().isOk());

        // Validate the AlertHistory in the database
        List<AlertHistory> alertHistories = alertHistoryRepository.findAll();
        assertThat(alertHistories).hasSize(databaseSizeBeforeUpdate);
        AlertHistory testAlertHistory = alertHistories.get(alertHistories.size() - 1);
        assertThat(testAlertHistory.getTriggeredAt()).isEqualTo(UPDATED_TRIGGERED_AT);
        assertThat(testAlertHistory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAlertHistory.getPriority()).isEqualTo(UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    public void deleteAlertHistory() throws Exception {
        // Initialize the database
        alertHistoryRepository.saveAndFlush(alertHistory);
        int databaseSizeBeforeDelete = alertHistoryRepository.findAll().size();

        // Get the alertHistory
        restAlertHistoryMockMvc.perform(delete("/api/alert-histories/{id}", alertHistory.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<AlertHistory> alertHistories = alertHistoryRepository.findAll();
        assertThat(alertHistories).hasSize(databaseSizeBeforeDelete - 1);
    }
}
