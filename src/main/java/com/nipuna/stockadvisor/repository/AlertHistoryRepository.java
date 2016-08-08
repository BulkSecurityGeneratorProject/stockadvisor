package com.nipuna.stockadvisor.repository;

import com.nipuna.stockadvisor.domain.AlertHistory;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the AlertHistory entity.
 */
@SuppressWarnings("unused")
public interface AlertHistoryRepository extends JpaRepository<AlertHistory,Long> {

}
