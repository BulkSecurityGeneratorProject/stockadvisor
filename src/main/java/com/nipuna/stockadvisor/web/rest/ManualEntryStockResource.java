package com.nipuna.stockadvisor.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nipuna.stockadvisor.domain.ManualEntryStock;
import com.nipuna.stockadvisor.repository.ManualEntryStockRepository;
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
 * REST controller for managing ManualEntryStock.
 */
@RestController
@RequestMapping("/api")
public class ManualEntryStockResource {

    private final Logger log = LoggerFactory.getLogger(ManualEntryStockResource.class);
        
    @Inject
    private ManualEntryStockRepository manualEntryStockRepository;
    
    /**
     * POST  /manual-entry-stocks : Create a new manualEntryStock.
     *
     * @param manualEntryStock the manualEntryStock to create
     * @return the ResponseEntity with status 201 (Created) and with body the new manualEntryStock, or with status 400 (Bad Request) if the manualEntryStock has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/manual-entry-stocks",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ManualEntryStock> createManualEntryStock(@Valid @RequestBody ManualEntryStock manualEntryStock) throws URISyntaxException {
        log.debug("REST request to save ManualEntryStock : {}", manualEntryStock);
        if (manualEntryStock.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("manualEntryStock", "idexists", "A new manualEntryStock cannot already have an ID")).body(null);
        }
        ManualEntryStock result = manualEntryStockRepository.save(manualEntryStock);
        return ResponseEntity.created(new URI("/api/manual-entry-stocks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("manualEntryStock", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /manual-entry-stocks : Updates an existing manualEntryStock.
     *
     * @param manualEntryStock the manualEntryStock to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated manualEntryStock,
     * or with status 400 (Bad Request) if the manualEntryStock is not valid,
     * or with status 500 (Internal Server Error) if the manualEntryStock couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/manual-entry-stocks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ManualEntryStock> updateManualEntryStock(@Valid @RequestBody ManualEntryStock manualEntryStock) throws URISyntaxException {
        log.debug("REST request to update ManualEntryStock : {}", manualEntryStock);
        if (manualEntryStock.getId() == null) {
            return createManualEntryStock(manualEntryStock);
        }
        ManualEntryStock result = manualEntryStockRepository.save(manualEntryStock);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("manualEntryStock", manualEntryStock.getId().toString()))
            .body(result);
    }

    /**
     * GET  /manual-entry-stocks : get all the manualEntryStocks.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of manualEntryStocks in body
     */
    @RequestMapping(value = "/manual-entry-stocks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ManualEntryStock> getAllManualEntryStocks() {
        log.debug("REST request to get all ManualEntryStocks");
        List<ManualEntryStock> manualEntryStocks = manualEntryStockRepository.findAll();
        return manualEntryStocks;
    }

    /**
     * GET  /manual-entry-stocks/:id : get the "id" manualEntryStock.
     *
     * @param id the id of the manualEntryStock to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the manualEntryStock, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/manual-entry-stocks/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ManualEntryStock> getManualEntryStock(@PathVariable Long id) {
        log.debug("REST request to get ManualEntryStock : {}", id);
        ManualEntryStock manualEntryStock = manualEntryStockRepository.findOne(id);
        return Optional.ofNullable(manualEntryStock)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /manual-entry-stocks/:id : delete the "id" manualEntryStock.
     *
     * @param id the id of the manualEntryStock to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/manual-entry-stocks/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteManualEntryStock(@PathVariable Long id) {
        log.debug("REST request to delete ManualEntryStock : {}", id);
        manualEntryStockRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("manualEntryStock", id.toString())).build();
    }

}
