package com.nipuna.stockadvisor.web.rest;

import com.nipuna.stockadvisor.StockadvisorApp;
import com.nipuna.stockadvisor.domain.JobLog;
import com.nipuna.stockadvisor.repository.JobLogRepository;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nipuna.stockadvisor.domain.enumeration.JobRunType;

/**
 * Test class for the JobLogResource REST controller.
 *
 * @see JobLogResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StockadvisorApp.class)
@WebAppConfiguration
@IntegrationTest
public class JobLogResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_JOB_ID = "AAAAA";
    private static final String UPDATED_JOB_ID = "BBBBB";

    private static final JobRunType DEFAULT_RUN_TYPE = JobRunType.MANUAL;
    private static final JobRunType UPDATED_RUN_TYPE = JobRunType.SCHEDULED;

    private static final ZonedDateTime DEFAULT_RUN_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_RUN_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_RUN_DATE_STR = dateTimeFormatter.format(DEFAULT_RUN_DATE);

    @Inject
    private JobLogRepository jobLogRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restJobLogMockMvc;

    private JobLog jobLog;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JobLogResource jobLogResource = new JobLogResource();
        ReflectionTestUtils.setField(jobLogResource, "jobLogRepository", jobLogRepository);
        this.restJobLogMockMvc = MockMvcBuilders.standaloneSetup(jobLogResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        jobLog = new JobLog();
        jobLog.setJobId(DEFAULT_JOB_ID);
        jobLog.setRunType(DEFAULT_RUN_TYPE);
        jobLog.setRunDate(DEFAULT_RUN_DATE);
    }

    @Test
    @Transactional
    public void createJobLog() throws Exception {
        int databaseSizeBeforeCreate = jobLogRepository.findAll().size();

        // Create the JobLog

        restJobLogMockMvc.perform(post("/api/job-logs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(jobLog)))
                .andExpect(status().isCreated());

        // Validate the JobLog in the database
        List<JobLog> jobLogs = jobLogRepository.findAll();
        assertThat(jobLogs).hasSize(databaseSizeBeforeCreate + 1);
        JobLog testJobLog = jobLogs.get(jobLogs.size() - 1);
        assertThat(testJobLog.getJobId()).isEqualTo(DEFAULT_JOB_ID);
        assertThat(testJobLog.getRunType()).isEqualTo(DEFAULT_RUN_TYPE);
        assertThat(testJobLog.getRunDate()).isEqualTo(DEFAULT_RUN_DATE);
    }

    @Test
    @Transactional
    public void checkJobIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobLogRepository.findAll().size();
        // set the field null
        jobLog.setJobId(null);

        // Create the JobLog, which fails.

        restJobLogMockMvc.perform(post("/api/job-logs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(jobLog)))
                .andExpect(status().isBadRequest());

        List<JobLog> jobLogs = jobLogRepository.findAll();
        assertThat(jobLogs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllJobLogs() throws Exception {
        // Initialize the database
        jobLogRepository.saveAndFlush(jobLog);

        // Get all the jobLogs
        restJobLogMockMvc.perform(get("/api/job-logs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(jobLog.getId().intValue())))
                .andExpect(jsonPath("$.[*].jobId").value(hasItem(DEFAULT_JOB_ID.toString())))
                .andExpect(jsonPath("$.[*].runType").value(hasItem(DEFAULT_RUN_TYPE.toString())))
                .andExpect(jsonPath("$.[*].runDate").value(hasItem(DEFAULT_RUN_DATE_STR)));
    }

    @Test
    @Transactional
    public void getJobLog() throws Exception {
        // Initialize the database
        jobLogRepository.saveAndFlush(jobLog);

        // Get the jobLog
        restJobLogMockMvc.perform(get("/api/job-logs/{id}", jobLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(jobLog.getId().intValue()))
            .andExpect(jsonPath("$.jobId").value(DEFAULT_JOB_ID.toString()))
            .andExpect(jsonPath("$.runType").value(DEFAULT_RUN_TYPE.toString()))
            .andExpect(jsonPath("$.runDate").value(DEFAULT_RUN_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingJobLog() throws Exception {
        // Get the jobLog
        restJobLogMockMvc.perform(get("/api/job-logs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJobLog() throws Exception {
        // Initialize the database
        jobLogRepository.saveAndFlush(jobLog);
        int databaseSizeBeforeUpdate = jobLogRepository.findAll().size();

        // Update the jobLog
        JobLog updatedJobLog = new JobLog();
        updatedJobLog.setId(jobLog.getId());
        updatedJobLog.setJobId(UPDATED_JOB_ID);
        updatedJobLog.setRunType(UPDATED_RUN_TYPE);
        updatedJobLog.setRunDate(UPDATED_RUN_DATE);

        restJobLogMockMvc.perform(put("/api/job-logs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedJobLog)))
                .andExpect(status().isOk());

        // Validate the JobLog in the database
        List<JobLog> jobLogs = jobLogRepository.findAll();
        assertThat(jobLogs).hasSize(databaseSizeBeforeUpdate);
        JobLog testJobLog = jobLogs.get(jobLogs.size() - 1);
        assertThat(testJobLog.getJobId()).isEqualTo(UPDATED_JOB_ID);
        assertThat(testJobLog.getRunType()).isEqualTo(UPDATED_RUN_TYPE);
        assertThat(testJobLog.getRunDate()).isEqualTo(UPDATED_RUN_DATE);
    }

    @Test
    @Transactional
    public void deleteJobLog() throws Exception {
        // Initialize the database
        jobLogRepository.saveAndFlush(jobLog);
        int databaseSizeBeforeDelete = jobLogRepository.findAll().size();

        // Get the jobLog
        restJobLogMockMvc.perform(delete("/api/job-logs/{id}", jobLog.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<JobLog> jobLogs = jobLogRepository.findAll();
        assertThat(jobLogs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
