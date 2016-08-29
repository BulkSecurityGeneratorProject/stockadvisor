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

	@Query("select jobLog from JobLog jobLog where jobLog.jobId = :jobId and jobLog.runDate >= :since")
	List<JobLog> findByJobIdSince(@Param("jobId") String jobId, @Param("since") ZonedDateTime since);
}
