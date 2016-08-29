package com.nipuna.stockadvisor.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import com.nipuna.stockadvisor.domain.enumeration.AlertPriority;

/**
 * A AlertHistory.
 */
@Entity
@Table(name = "alert_history")
public class AlertHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "triggered_at")
    private ZonedDateTime triggeredAt;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private AlertPriority priority;

    @ManyToOne
    private Watchlist watchlist;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(ZonedDateTime triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AlertPriority getPriority() {
        return priority;
    }

    public void setPriority(AlertPriority priority) {
        this.priority = priority;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AlertHistory alertHistory = (AlertHistory) o;
        if(alertHistory.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, alertHistory.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AlertHistory{" +
            "id=" + id +
            ", triggeredAt='" + triggeredAt + "'" +
            ", description='" + description + "'" +
            ", priority='" + priority + "'" +
            '}';
    }
}
