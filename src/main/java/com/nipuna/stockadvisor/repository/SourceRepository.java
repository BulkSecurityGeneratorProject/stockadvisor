package com.nipuna.stockadvisor.repository;

import com.nipuna.stockadvisor.domain.Source;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Source entity.
 */
@SuppressWarnings("unused")
public interface SourceRepository extends JpaRepository<Source,Long> {

}
