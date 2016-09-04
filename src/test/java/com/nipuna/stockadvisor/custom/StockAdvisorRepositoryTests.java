package com.nipuna.stockadvisor.custom;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.nipuna.stockadvisor.StockadvisorApp;
import com.nipuna.stockadvisor.domain.JobLog;
import com.nipuna.stockadvisor.domain.enumeration.JobRunType;
import com.nipuna.stockadvisor.domain.util.JSR310DateConverters.ZonedDateTimeToDateConverter;
import com.nipuna.stockadvisor.repository.JobLogRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StockadvisorApp.class)
public class StockAdvisorRepositoryTests {

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));
	private static final String DEFAULT_JOB_ID = "AAAAA";
	private static final String UPDATED_JOB_ID = "BBBBB";

	private static final JobRunType DEFAULT_RUN_TYPE = JobRunType.MANUAL;
	private static final JobRunType UPDATED_RUN_TYPE = JobRunType.SCHEDULED;

	private static final ZonedDateTime DEFAULT_RUN_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L),
			ZoneId.systemDefault());
	private static final ZonedDateTime UPDATED_RUN_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
	private static final String DEFAULT_RUN_DATE_STR = dateTimeFormatter.format(DEFAULT_RUN_DATE);

	   private static final Logger LOG = LoggerFactory.getLogger(StockAdvisorRepositoryTests.class);
	@Inject
	private JobLogRepository jobLogRepository;

	@Inject
	private EntityManager em;

	private JobLog jobLog;

	/**
	 * Create an entity for this test.
	 *
	 * This is a static method, as tests for other entities might also need it,
	 * if they test an entity which requires the current entity.
	 */
	public static JobLog createEntity(EntityManager em) {
		JobLog jobLog = new JobLog();
		jobLog = new JobLog();
		jobLog.setJobId(DEFAULT_JOB_ID);
		jobLog.setRunType(DEFAULT_RUN_TYPE);
		jobLog.setRunDate(DEFAULT_RUN_DATE);
		return jobLog;
	}

	@Before
	public void initTest() {
		jobLog = createEntity(em);
	}

	@Test
	@Transactional
	public void createJobLog() throws Exception {
		int databaseSizeBeforeCreate = jobLogRepository.findAll().size();

		// Create the JobLog
		jobLogRepository.saveAndFlush(jobLog);
		
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
	public void findByJobIdAndRunDateAfter() throws Exception {
		int databaseSizeBeforeCreate = jobLogRepository.findAll().size();
		ZonedDateTime now = ZonedDateTime.now();
		JobLog newLog = createEntity(em);
		newLog = createEntity(em);
		newLog.setRunDate(now.minusHours(8));
		jobLogRepository.saveAndFlush(newLog);
		newLog = createEntity(em);
		newLog.setRunDate(now.minusHours(6));
		jobLogRepository.saveAndFlush(newLog);
		newLog = createEntity(em);
		newLog.setRunDate(now.minus(Period.ofDays(5)).plusHours(10));
		jobLogRepository.saveAndFlush(newLog);
		newLog = createEntity(em);
		newLog.setRunDate(now.minusHours(4));
		jobLogRepository.saveAndFlush(newLog);

		
		List<JobLog> jobLogs = jobLogRepository.findAll();
		
		for (JobLog jobLog : jobLogs) {
			LOG.info(jobLog.getJobId() +" "+jobLog.getRunDate());
		}
		assertThat(jobLogs).hasSize(databaseSizeBeforeCreate + 4);

		List<JobLog> filteredLogs = jobLogRepository.findByJobIdAndRunDateAfterOrderByRunDateDesc(DEFAULT_JOB_ID, now.minusHours(10));
		LOG.info("filteredLogs size: "+filteredLogs.size());
		for (JobLog jobLog : filteredLogs) {
			LOG.info(jobLog.getJobId() +" "+jobLog.getRunDate() +" "+ZonedDateTimeToDateConverter.INSTANCE.convert(jobLog.getRunDate()));
		}
		assertThat(filteredLogs).hasSize(databaseSizeBeforeCreate + 3);
	}
	

}
