package com.nipuna.stockadvisor.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nipuna.stockadvisor.domain.AlertHistory;

import com.nipuna.stockadvisor.repository.AlertHistoryRepository;
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
 * REST controller for managing AlertHistory.
 */
@RestController
@RequestMapping("/api")
public class AlertHistoryResource {

    private final Logger log = LoggerFactory.getLogger(AlertHistoryResource.class);
        
    @Inject
    private AlertHistoryRepository alertHistoryRepository;

    /**
     * POST  /alert-histories : Create a new alertHistory.
     *
     * @param alertHistory the alertHistory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new alertHistory, or with status 400 (Bad Request) if the alertHistory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/alert-histories",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlertHistory> createAlertHistory(@Valid @RequestBody AlertHistory alertHistory) throws URISyntaxException {
        log.debug("REST request to save AlertHistory : {}", alertHistory);
        if (alertHistory.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("alertHistory", "idexists", "A new alertHistory cannot already have an ID")).body(null);
        }
        AlertHistory result = alertHistoryRepository.save(alertHistory);
        return ResponseEntity.created(new URI("/api/alert-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("alertHistory", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /alert-histories : Updates an existing alertHistory.
     *
     * @param alertHistory the alertHistory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated alertHistory,
     * or with status 400 (Bad Request) if the alertHistory is not valid,
     * or with status 500 (Internal Server Error) if the alertHistory couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/alert-histories",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlertHistory> updateAlertHistory(@Valid @RequestBody AlertHistory alertHistory) throws URISyntaxException {
        log.debug("REST request to update AlertHistory : {}", alertHistory);
        if (alertHistory.getId() == null) {
            return createAlertHistory(alertHistory);
        }
        AlertHistory result = alertHistoryRepository.save(alertHistory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("alertHistory", alertHistory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /alert-histories : get all the alertHistories.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of alertHistories in body
     */
    @RequestMapping(value = "/alert-histories",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<AlertHistory> getAllAlertHistories() {
        log.debug("REST request to get all AlertHistories");
        List<AlertHistory> alertHistories = alertHistoryRepository.findAll();
        return alertHistories;
    }

    /**
     * GET  /alert-histories/:id : get the "id" alertHistory.
     *
     * @param id the id of the alertHistory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the alertHistory, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/alert-histories/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AlertHistory> getAlertHistory(@PathVariable Long id) {
        log.debug("REST request to get AlertHistory : {}", id);
        AlertHistory alertHistory = alertHistoryRepository.findOne(id);
        return Optional.ofNullable(alertHistory)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /alert-histories/:id : delete the "id" alertHistory.
     *
     * @param id the id of the alertHistory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/alert-histories/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAlertHistory(@PathVariable Long id) {
        log.debug("REST request to delete AlertHistory : {}", id);
        alertHistoryRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("alertHistory", id.toString())).build();
    }

}
