package com.nipuna.stockadvisor.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nipuna.stockadvisor.domain.Watchlist;
import com.nipuna.stockadvisor.repository.WatchlistRepository;
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
 * REST controller for managing Watchlist.
 */
@RestController
@RequestMapping("/api")
public class WatchlistResource {

    private final Logger log = LoggerFactory.getLogger(WatchlistResource.class);
        
    @Inject
    private WatchlistRepository watchlistRepository;
    
    /**
     * POST  /watchlists : Create a new watchlist.
     *
     * @param watchlist the watchlist to create
     * @return the ResponseEntity with status 201 (Created) and with body the new watchlist, or with status 400 (Bad Request) if the watchlist has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/watchlists",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Watchlist> createWatchlist(@Valid @RequestBody Watchlist watchlist) throws URISyntaxException {
        log.debug("REST request to save Watchlist : {}", watchlist);
        if (watchlist.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("watchlist", "idexists", "A new watchlist cannot already have an ID")).body(null);
        }
        Watchlist result = watchlistRepository.save(watchlist);
        return ResponseEntity.created(new URI("/api/watchlists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("watchlist", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /watchlists : Updates an existing watchlist.
     *
     * @param watchlist the watchlist to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated watchlist,
     * or with status 400 (Bad Request) if the watchlist is not valid,
     * or with status 500 (Internal Server Error) if the watchlist couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/watchlists",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Watchlist> updateWatchlist(@Valid @RequestBody Watchlist watchlist) throws URISyntaxException {
        log.debug("REST request to update Watchlist : {}", watchlist);
        if (watchlist.getId() == null) {
            return createWatchlist(watchlist);
        }
        Watchlist result = watchlistRepository.save(watchlist);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("watchlist", watchlist.getId().toString()))
            .body(result);
    }

    /**
     * GET  /watchlists : get all the watchlists.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of watchlists in body
     */
    @RequestMapping(value = "/watchlists",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Watchlist> getAllWatchlists() {
        log.debug("REST request to get all Watchlists");
        List<Watchlist> watchlists = watchlistRepository.findAllWithEagerRelationships();
        return watchlists;
    }

    /**
     * GET  /watchlists/:id : get the "id" watchlist.
     *
     * @param id the id of the watchlist to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the watchlist, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/watchlists/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Watchlist> getWatchlist(@PathVariable Long id) {
        log.debug("REST request to get Watchlist : {}", id);
        Watchlist watchlist = watchlistRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(watchlist)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /watchlists/:id : delete the "id" watchlist.
     *
     * @param id the id of the watchlist to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/watchlists/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteWatchlist(@PathVariable Long id) {
        log.debug("REST request to delete Watchlist : {}", id);
        watchlistRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("watchlist", id.toString())).build();
    }
    
}
