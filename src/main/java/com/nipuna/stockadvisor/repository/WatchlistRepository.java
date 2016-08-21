package com.nipuna.stockadvisor.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nipuna.stockadvisor.domain.Watchlist;

/**
 * Spring Data JPA repository for the Watchlist entity.
 */
@SuppressWarnings("unused")
public interface WatchlistRepository extends JpaRepository<Watchlist,Long> {

    @Query("select distinct watchlist from Watchlist watchlist left join fetch watchlist.sources left join fetch watchlist.alerts")
    List<Watchlist> findAllWithEagerRelationships();

    @Query("select watchlist from Watchlist watchlist left join fetch watchlist.sources left join fetch watchlist.alerts where watchlist.id =:id")
    Watchlist findOneWithEagerRelationships(@Param("id") Long id);
    
    @Query("select w from Watchlist w left join fetch w.sources s where w.entryDate=:entry_date and s.name=:source")
    List<Watchlist> findWatchListEnteredByDateAndSource(@Param("source") String source, @Param("entry_date") LocalDate entry_date);
    
    @Query("select w from Watchlist w  where w.symbol in (:symbols)")
    List<Watchlist> findWatchListBySymbols(@Param("symbols") String...symbols);

}
