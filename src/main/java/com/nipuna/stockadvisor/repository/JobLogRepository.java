package com.nipuna.stockadvisor.repository;

import com.nipuna.stockadvisor.domain.JobLog;

import org.springframework.data.jpa.repository.*;

import java.util.List;

import javax.persistence.NamedQuery;

/**
 * Spring Data JPA repository for the JobLog entity.
 */
@SuppressWarnings("unused")
public interface JobLogRepository extends JpaRepository<JobLog, Long> {

	@Query("select jobLog from JobLog jobLog where jobLog.jobId = ?1")
	List<JobLog> findByJobId(String jobId);
}
