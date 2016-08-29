package com.nipuna.stockadvisor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nipuna.stockadvisor.domain.AlertHistory;

/**
 * Spring Data JPA repository for the AlertHistory entity.
 */
@SuppressWarnings("unused")
public interface AlertHistoryRepository extends JpaRepository<AlertHistory,Long> {
	@Query("select w from AlertHistory w  where w.watchlist.id in (:ids)") // order by w.triggeredAt desc , w.priority asc
	List<AlertHistory> findAlertHistoryByWatchListIdSorted(@Param("ids") Long... ids);
}
