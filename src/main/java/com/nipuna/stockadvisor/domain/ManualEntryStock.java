package com.nipuna.stockadvisor.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A ManualEntryStock.
 */
@Entity
@Table(name = "manual_entry_stock")
public class ManualEntryStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "symbols", nullable = false)
    private String symbols;

    @NotNull
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @NotNull
    @Column(name = "processed", nullable = false)
    private String processed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbols() {
        return symbols;
    }

    public void setSymbols(String symbols) {
        this.symbols = symbols;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ManualEntryStock manualEntryStock = (ManualEntryStock) o;
        if(manualEntryStock.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, manualEntryStock.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ManualEntryStock{" +
            "id=" + id +
            ", symbols='" + symbols + "'" +
            ", entryDate='" + entryDate + "'" +
            ", processed='" + processed + "'" +
            '}';
    }
}
