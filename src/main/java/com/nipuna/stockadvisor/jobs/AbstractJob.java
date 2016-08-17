package com.nipuna.stockadvisor.jobs;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.nipuna.stockadvisor.domain.JobLog;
import com.nipuna.stockadvisor.domain.enumeration.JobRunType;
import com.nipuna.stockadvisor.repository.JobLogRepository;
import com.nipuna.stockadvisor.util.EmailSender;

public abstract class AbstractJob {

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
		List<JobLog> logs = jobLogRepository.findByJobId(getJobId());
		for (JobLog jobLog : logs) {
			sb.append(jobLog.getRunDate().withZoneSameLocal(ZONEID_EST));
			sb.append("\n");
		}

		sendEmail(getJobId() + " running", sb.toString());
	}

	protected void sendEmail(String subj, String body) {
		EmailSender.sendEmail(subj, body);
	}
}
