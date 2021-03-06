package com.nipuna.stockadvisor.jobs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
	protected static final ZonedDateTime LAST_12_HOURS = ZonedDateTime.now().minusHours(12);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd hh:mm a");
	
	@Autowired
	private JobLogRepository jobLogRepository;

	@Autowired
	private EmailSender emailSender;
	
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
		List<JobLog> logs = jobLogRepository.findByJobIdAndRunDateAfterOrderByRunDateDesc(getJobId(), LAST_12_HOURS);
		for (JobLog jobLog : logs) {
			//2016-09-04T11:57:54-04:00[America/New_York]
//			sb.append(jobLog.getRunDate().withZoneSameLocal(ZONEID_EST));
			sb.append(jobLog.getRunDate().format(DATE_FORMATTER) +" ("+jobLog.getRunDate().withZoneSameLocal(ZONEID_EST).format(DATE_FORMATTER)+")");
			sb.append("\n");
		}

		sendEmail(getJobId() + " running", sb.toString());

		// TODO:cleanup
		ensureJobRunForMoreThanAMin();
	}

	protected void sendEmail(String subj, String body) {
		emailSender.sendEmail(subj, body);
	}

	// hack to avoid duplicate run of scheduled tasks...
	protected void ensureJobRunForMoreThanAMin() {
		try {
			Thread.sleep(1000 * 61);
		} catch (InterruptedException e) {
		}
	}

	public String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}
}
