package com.nipuna.stockadvisor.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import com.nipuna.stockadvisor.domain.enumeration.JobRunType;

/**
 * A JobLog.
 */
@Entity
@Table(name = "job_log")
public class JobLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "job_id", nullable = false)
    private String jobId;

    @Enumerated(EnumType.STRING)
    @Column(name = "run_type")
    private JobRunType runType;

    @Column(name = "run_date")
    private ZonedDateTime runDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public JobRunType getRunType() {
        return runType;
    }

    public void setRunType(JobRunType runType) {
        this.runType = runType;
    }

    public ZonedDateTime getRunDate() {
        return runDate;
    }

    public void setRunDate(ZonedDateTime runDate) {
        this.runDate = runDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobLog jobLog = (JobLog) o;
        if(jobLog.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, jobLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "JobLog{" +
            "id=" + id +
            ", jobId='" + jobId + "'" +
            ", runType='" + runType + "'" +
            ", runDate='" + runDate + "'" +
            '}';
    }
}
