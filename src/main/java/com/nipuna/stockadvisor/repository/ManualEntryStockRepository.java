package com.nipuna.stockadvisor.repository;

import com.nipuna.stockadvisor.domain.ManualEntryStock;
import com.nipuna.stockadvisor.domain.Watchlist;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the ManualEntryStock entity.
 */
@SuppressWarnings("unused")
public interface ManualEntryStockRepository extends JpaRepository<ManualEntryStock,Long> {

	@Query("select m from ManualEntryStock m where m.processed =:status")
    List<ManualEntryStock> findManualEntryStockByProcessingStatus(@Param("status") String status);
	
}
