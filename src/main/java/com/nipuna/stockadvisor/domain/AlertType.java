package com.nipuna.stockadvisor.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.nipuna.stockadvisor.domain.enumeration.ParamType;

/**
 * A AlertType.
 */
@Entity
@Table(name = "alert_type")
public class AlertType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "fqdn", nullable = false)
    private String fqdn;

    @Enumerated(EnumType.STRING)
    @Column(name = "param_type")
    private ParamType paramType=ParamType.NOT_APPLICABLE;

    @Column(name = "param_value")
    private String paramValue;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "alerts")
    @JsonIgnore
    private Set<Watchlist> watchlists = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    public ParamType getParamType() {
        return paramType;
    }

    public void setParamType(ParamType paramType) {
        this.paramType = paramType;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Watchlist> getWatchlists() {
        return watchlists;
    }

    public void setWatchlists(Set<Watchlist> watchlists) {
        this.watchlists = watchlists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AlertType alertType = (AlertType) o;
        if(alertType.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, alertType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AlertType{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", fqdn='" + fqdn + "'" +
            ", paramType='" + paramType + "'" +
            ", paramValue='" + paramValue + "'" +
            ", description='" + description + "'" +
            '}';
    }
}
