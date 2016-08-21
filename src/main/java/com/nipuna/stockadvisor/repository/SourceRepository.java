package com.nipuna.stockadvisor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nipuna.stockadvisor.domain.Source;

/**
 * Spring Data JPA repository for the Source entity.
 */
@SuppressWarnings("unused")
public interface SourceRepository extends JpaRepository<Source, Long> {

	@Query("select source from Source source where source.name like %:name%")
	Source findOneByName(@Param("name") String name);

}
