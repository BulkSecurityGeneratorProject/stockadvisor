package com.nipuna.stockadvisor.repository;

import com.nipuna.stockadvisor.domain.Watchlist;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Watchlist entity.
 */
@SuppressWarnings("unused")
public interface WatchlistRepository extends JpaRepository<Watchlist,Long> {

    @Query("select distinct watchlist from Watchlist watchlist left join fetch watchlist.sources left join fetch watchlist.alerts")
    List<Watchlist> findAllWithEagerRelationships();

    @Query("select watchlist from Watchlist watchlist left join fetch watchlist.sources left join fetch watchlist.alerts where watchlist.id =:id")
    Watchlist findOneWithEagerRelationships(@Param("id") Long id);

}
