package com.nipuna.stockadvisor.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nipuna.stockadvisor.domain.AlertType;

import com.nipuna.stockadvisor.repository.AlertTypeRepository;
import com.nipuna.stockadvisor.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing AlertType.
 */
@RestController
@RequestMapping("/api")
public class AlertTypeResource {

    private final Logger log = LoggerFactory.getLogger(AlertTypeResource.class);
        
    @Inject
    private AlertTypeRepository alertTypeRepository;

    /**
     * POST  /alert-types : Create a new alertType.
     *
     * @param alertType the alertType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new alertType, or with status 400 (Bad Request) if the alertType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/alert-types",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlertType> createAlertType(@Valid @RequestBody AlertType alertType) throws URISyntaxException {
        log.debug("REST request to save AlertType : {}", alertType);
        if (alertType.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("alertType", "idexists", "A new alertType cannot already have an ID")).body(null);
        }
        AlertType result = alertTypeRepository.save(alertType);
        return ResponseEntity.created(new URI("/api/alert-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("alertType", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /alert-types : Updates an existing alertType.
     *
     * @param alertType the alertType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated alertType,
     * or with status 400 (Bad Request) if the alertType is not valid,
     * or with status 500 (Internal Server Error) if the alertType couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/alert-types",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlertType> updateAlertType(@Valid @RequestBody AlertType alertType) throws URISyntaxException {
        log.debug("REST request to update AlertType : {}", alertType);
        if (alertType.getId() == null) {
            return createAlertType(alertType);
        }
        AlertType result = alertTypeRepository.save(alertType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("alertType", alertType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /alert-types : get all the alertTypes.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of alertTypes in body
     */
    @RequestMapping(value = "/alert-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<AlertType> getAllAlertTypes() {
        log.debug("REST request to get all AlertTypes");
        List<AlertType> alertTypes = alertTypeRepository.findAll();
        return alertTypes;
    }

    /**
     * GET  /alert-types/:id : get the "id" alertType.
     *
     * @param id the id of the alertType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the alertType, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/alert-types/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlertType> getAlertType(@PathVariable Long id) {
        log.debug("REST request to get AlertType : {}", id);
        AlertType alertType = alertTypeRepository.findOne(id);
        return Optional.ofNullable(alertType)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /alert-types/:id : delete the "id" alertType.
     *
     * @param id the id of the alertType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/alert-types/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAlertType(@PathVariable Long id) {
        log.debug("REST request to delete AlertType : {}", id);
        alertTypeRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("alertType", id.toString())).build();
    }

}
