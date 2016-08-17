package com.nipuna.stockadvisor.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Watchlist.
 */
@Entity
@Table(name = "watchlist", uniqueConstraints = @UniqueConstraint(columnNames = { "symbol", "entry_date",
		"entry_price" }))
public class Watchlist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "symbol", nullable = false)
    private String symbol;

    @NotNull
    @Column(name = "entry_price", nullable = false)
    private Double entryPrice;

    @NotNull
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @OneToMany(mappedBy = "watchlist")
    @JsonIgnore
    private Set<AlertHistory> alertHistories = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "watchlist_source",
               joinColumns = @JoinColumn(name="watchlists_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="sources_id", referencedColumnName="ID"))
    private Set<Source> sources = new HashSet<>();

    //Solve “failed to lazily initialize a collection of role” exception
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name = "watchlist_alert",
               joinColumns = @JoinColumn(name="watchlists_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="alerts_id", referencedColumnName="ID"))
    private Set<AlertType> alerts = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(Double entryPrice) {
        this.entryPrice = entryPrice;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public Set<AlertHistory> getAlertHistories() {
        return alertHistories;
    }

    public void setAlertHistories(Set<AlertHistory> alertHistories) {
        this.alertHistories = alertHistories;
    }

    public Set<Source> getSources() {
        return sources;
    }

    public void setSources(Set<Source> sources) {
        this.sources = sources;
    }

    public Set<AlertType> getAlerts() {
        return alerts;
    }

    public void setAlerts(Set<AlertType> alertTypes) {
        this.alerts = alertTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Watchlist watchlist = (Watchlist) o;
        if(watchlist.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, watchlist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Watchlist{" +
            "id=" + id +
            ", symbol='" + symbol + "'" +
            ", entryPrice='" + entryPrice + "'" +
            ", entryDate='" + entryDate + "'" +
            '}';
    }
}
