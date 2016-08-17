package com.nipuna.stockadvisor.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HeartBeatJob extends AbstractJob {

	// @Scheduled(fixedRate = 100000)
	// At every 0th minute past the 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 and
	// 18th hour.
	// http://crontab.guru/#0_8-18_*_*_*
	//@Scheduled(cron = "* 0 8-18 * * *",zone="America/New_York")
	@Scheduled(fixedRate=30000)
	public void executeJob() {
		System.out.println("HeartBeatJob running....");
	}

	@Override
	public String getJobId() {
		return this.getClass().getSimpleName();
	}

}