package com.nipuna.stockadvisor.repository;

import com.nipuna.stockadvisor.domain.AlertType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the AlertType entity.
 */
@SuppressWarnings("unused")
public interface AlertTypeRepository extends JpaRepository<AlertType,Long> {

}
