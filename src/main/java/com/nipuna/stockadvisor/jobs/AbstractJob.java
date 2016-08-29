package com.nipuna.stockadvisor.jobs;

import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nipuna.stockadvisor.domain.JobLog;
import com.nipuna.stockadvisor.domain.enumeration.JobRunType;
import com.nipuna.stockadvisor.repository.JobLogRepository;
import com.nipuna.stockadvisor.util.EmailSender;

public abstract class AbstractJob {
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractJob.class.getName());

	protected static final ZoneId ZONEID_EST = ZoneId.of("America/New_York");
	@Autowired
	private JobLogRepository jobLogRepository;

	public abstract void executeJob() throws Exception;

	public void runJob() throws Exception {
		this.executeJob();
		performAudit();
	}

	public void runJobManually() throws Exception {
		this.executeJob();
		performAudit(JobRunType.MANUAL);
	}

	public void performAudit() {
		performAudit(JobRunType.SCHEDULED);
	}

	public String getJobId() {
		return getClass().getSimpleName();
	}

	public void performAudit(JobRunType runType) {
		JobLog log = new JobLog();
		log.setJobId(getJobId());
		log.setRunDate(ZonedDateTime.now());
		log.setRunType(runType);
		jobLogRepository.save(log);

		StringBuffer sb = new StringBuffer();
		ZonedDateTime since1Day = ZonedDateTime.now().minus(Period.ofDays(1));
		List<JobLog> logs = jobLogRepository.findByJobIdSince(getJobId(), since1Day);
		for (JobLog jobLog : logs) {
			sb.append(jobLog.getRunDate().withZoneSameLocal(ZONEID_EST));
			sb.append("\n");
		}

		sendEmail(getJobId() + " running", sb.toString());

		// TODO:cleanup
		ensureJobRunForMoreThanAMin();
	}

	protected void sendEmail(String subj, String body) {
		EmailSender.sendEmail(subj, body);
	}

	// hack to avoid duplicate run of scheduled tasks...
	protected void ensureJobRunForMoreThanAMin() {
		try {
			Thread.sleep(1000 * 61);
		} catch (InterruptedException e) {
		}
	}
}
