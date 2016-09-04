package com.nipuna.stockadvisor.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nipuna.stockadvisor.domain.JobLog;

/**
 * Spring Data JPA repository for the JobLog entity.
 */
@SuppressWarnings("unused")
public interface JobLogRepository extends JpaRepository<JobLog,Long> {
	List<JobLog> findByJobIdAndRunDateAfterOrderByRunDateDesc(String jobId, ZonedDateTime runDate);
}
