package com.nipuna.stockadvisor.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HeartBeatJob extends AbstractJob {

	@Scheduled(cron = "${stockadvisor.jobs.heartbeat.schedule}", zone = "America/New_York")
	public void executeJob() {
		System.out.println("HeartBeatJob running....");
		performAudit();
	}

	@Override
	public String getJobId() {
		return this.getClass().getSimpleName();
	}

}