package com.nipuna.stockadvisor.web.rest;

import com.nipuna.stockadvisor.StockadvisorApp;
import com.nipuna.stockadvisor.domain.AlertType;
import com.nipuna.stockadvisor.repository.AlertTypeRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nipuna.stockadvisor.domain.enumeration.ParamType;

/**
 * Test class for the AlertTypeResource REST controller.
 *
 * @see AlertTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StockadvisorApp.class)
@WebAppConfiguration
@IntegrationTest
public class AlertTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_FQDN = "AAAAA";
    private static final String UPDATED_FQDN = "BBBBB";

    private static final ParamType DEFAULT_PARAM_TYPE = ParamType.STRING;
    private static final ParamType UPDATED_PARAM_TYPE = ParamType.NUMBER;
    private static final String DEFAULT_PARAM_VALUE = "AAAAA";
    private static final String UPDATED_PARAM_VALUE = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private AlertTypeRepository alertTypeRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAlertTypeMockMvc;

    private AlertType alertType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AlertTypeResource alertTypeResource = new AlertTypeResource();
        ReflectionTestUtils.setField(alertTypeResource, "alertTypeRepository", alertTypeRepository);
        this.restAlertTypeMockMvc = MockMvcBuilders.standaloneSetup(alertTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        alertType = new AlertType();
        alertType.setName(DEFAULT_NAME);
        alertType.setFqdn(DEFAULT_FQDN);
        alertType.setParamType(DEFAULT_PARAM_TYPE);
        alertType.setParamValue(DEFAULT_PARAM_VALUE);
        alertType.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createAlertType() throws Exception {
        int databaseSizeBeforeCreate = alertTypeRepository.findAll().size();

        // Create the AlertType

        restAlertTypeMockMvc.perform(post("/api/alert-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alertType)))
                .andExpect(status().isCreated());

        // Validate the AlertType in the database
        List<AlertType> alertTypes = alertTypeRepository.findAll();
        assertThat(alertTypes).hasSize(databaseSizeBeforeCreate + 1);
        AlertType testAlertType = alertTypes.get(alertTypes.size() - 1);
        assertThat(testAlertType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAlertType.getFqdn()).isEqualTo(DEFAULT_FQDN);
        assertThat(testAlertType.getParamType()).isEqualTo(DEFAULT_PARAM_TYPE);
        assertThat(testAlertType.getParamValue()).isEqualTo(DEFAULT_PARAM_VALUE);
        assertThat(testAlertType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = alertTypeRepository.findAll().size();
        // set the field null
        alertType.setName(null);

        // Create the AlertType, which fails.

        restAlertTypeMockMvc.perform(post("/api/alert-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alertType)))
                .andExpect(status().isBadRequest());

        List<AlertType> alertTypes = alertTypeRepository.findAll();
        assertThat(alertTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFqdnIsRequired() throws Exception {
        int databaseSizeBeforeTest = alertTypeRepository.findAll().size();
        // set the field null
        alertType.setFqdn(null);

        // Create the AlertType, which fails.

        restAlertTypeMockMvc.perform(post("/api/alert-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alertType)))
                .andExpect(status().isBadRequest());

        List<AlertType> alertTypes = alertTypeRepository.findAll();
        assertThat(alertTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAlertTypes() throws Exception {
        // Initialize the database
        alertTypeRepository.saveAndFlush(alertType);

        // Get all the alertTypes
        restAlertTypeMockMvc.perform(get("/api/alert-types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(alertType.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].fqdn").value(hasItem(DEFAULT_FQDN.toString())))
                .andExpect(jsonPath("$.[*].paramType").value(hasItem(DEFAULT_PARAM_TYPE.toString())))
                .andExpect(jsonPath("$.[*].paramValue").value(hasItem(DEFAULT_PARAM_VALUE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getAlertType() throws Exception {
        // Initialize the database
        alertTypeRepository.saveAndFlush(alertType);

        // Get the alertType
        restAlertTypeMockMvc.perform(get("/api/alert-types/{id}", alertType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(alertType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.fqdn").value(DEFAULT_FQDN.toString()))
            .andExpect(jsonPath("$.paramType").value(DEFAULT_PARAM_TYPE.toString()))
            .andExpect(jsonPath("$.paramValue").value(DEFAULT_PARAM_VALUE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAlertType() throws Exception {
        // Get the alertType
        restAlertTypeMockMvc.perform(get("/api/alert-types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAlertType() throws Exception {
        // Initialize the database
        alertTypeRepository.saveAndFlush(alertType);
        int databaseSizeBeforeUpdate = alertTypeRepository.findAll().size();

        // Update the alertType
        AlertType updatedAlertType = new AlertType();
        updatedAlertType.setId(alertType.getId());
        updatedAlertType.setName(UPDATED_NAME);
        updatedAlertType.setFqdn(UPDATED_FQDN);
        updatedAlertType.setParamType(UPDATED_PARAM_TYPE);
        updatedAlertType.setParamValue(UPDATED_PARAM_VALUE);
        updatedAlertType.setDescription(UPDATED_DESCRIPTION);

        restAlertTypeMockMvc.perform(put("/api/alert-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAlertType)))
                .andExpect(status().isOk());

        // Validate the AlertType in the database
        List<AlertType> alertTypes = alertTypeRepository.findAll();
        assertThat(alertTypes).hasSize(databaseSizeBeforeUpdate);
        AlertType testAlertType = alertTypes.get(alertTypes.size() - 1);
        assertThat(testAlertType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAlertType.getFqdn()).isEqualTo(UPDATED_FQDN);
        assertThat(testAlertType.getParamType()).isEqualTo(UPDATED_PARAM_TYPE);
        assertThat(testAlertType.getParamValue()).isEqualTo(UPDATED_PARAM_VALUE);
        assertThat(testAlertType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteAlertType() throws Exception {
        // Initialize the database
        alertTypeRepository.saveAndFlush(alertType);
        int databaseSizeBeforeDelete = alertTypeRepository.findAll().size();

        // Get the alertType
        restAlertTypeMockMvc.perform(delete("/api/alert-types/{id}", alertType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<AlertType> alertTypes = alertTypeRepository.findAll();
        assertThat(alertTypes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
