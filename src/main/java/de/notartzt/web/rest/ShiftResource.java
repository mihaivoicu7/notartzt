package de.notartzt.web.rest;

import de.notartzt.repository.ShiftRepository;
import de.notartzt.service.ShiftQueryService;
import de.notartzt.service.ShiftService;
import de.notartzt.service.criteria.ShiftCriteria;
import de.notartzt.service.dto.ShiftDTO;
import de.notartzt.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link de.notartzt.domain.Shift}.
 */
@RestController
@RequestMapping("/api")
public class ShiftResource {

    private final Logger log = LoggerFactory.getLogger(ShiftResource.class);

    private static final String ENTITY_NAME = "shift";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShiftService shiftService;

    private final ShiftRepository shiftRepository;

    private final ShiftQueryService shiftQueryService;

    public ShiftResource(ShiftService shiftService, ShiftRepository shiftRepository, ShiftQueryService shiftQueryService) {
        this.shiftService = shiftService;
        this.shiftRepository = shiftRepository;
        this.shiftQueryService = shiftQueryService;
    }

    /**
     * {@code POST  /shifts} : Create a new shift.
     *
     * @param shiftDTO the shiftDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shiftDTO, or with status {@code 400 (Bad Request)} if the shift has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/shifts")
    public ResponseEntity<ShiftDTO> createShift(@Valid @RequestBody ShiftDTO shiftDTO) throws URISyntaxException {
        log.debug("REST request to save Shift : {}", shiftDTO);
        if (shiftDTO.getId() != null) {
            throw new BadRequestAlertException("A new shift cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ShiftDTO result = shiftService.save(shiftDTO);
        return ResponseEntity
            .created(new URI("/api/shifts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /shifts/:id} : Updates an existing shift.
     *
     * @param id the id of the shiftDTO to save.
     * @param shiftDTO the shiftDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shiftDTO,
     * or with status {@code 400 (Bad Request)} if the shiftDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shiftDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/shifts/{id}")
    public ResponseEntity<ShiftDTO> updateShift(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShiftDTO shiftDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Shift : {}, {}", id, shiftDTO);
        if (shiftDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shiftDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shiftRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ShiftDTO result = shiftService.update(shiftDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shiftDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /shifts/:id} : Partial updates given fields of an existing shift, field will ignore if it is null
     *
     * @param id the id of the shiftDTO to save.
     * @param shiftDTO the shiftDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shiftDTO,
     * or with status {@code 400 (Bad Request)} if the shiftDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shiftDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shiftDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/shifts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShiftDTO> partialUpdateShift(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShiftDTO shiftDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Shift partially : {}, {}", id, shiftDTO);
        if (shiftDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shiftDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shiftRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShiftDTO> result = shiftService.partialUpdate(shiftDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shiftDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shifts} : get all the shifts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shifts in body.
     */
    @GetMapping("/shifts")
    public ResponseEntity<List<ShiftDTO>> getAllShifts(
        ShiftCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Shifts by criteria: {}", criteria);
        Page<ShiftDTO> page = shiftQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shifts/count} : count all the shifts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/shifts/count")
    public ResponseEntity<Long> countShifts(ShiftCriteria criteria) {
        log.debug("REST request to count Shifts by criteria: {}", criteria);
        return ResponseEntity.ok().body(shiftQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /shifts/:id} : get the "id" shift.
     *
     * @param id the id of the shiftDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shiftDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/shifts/{id}")
    public ResponseEntity<ShiftDTO> getShift(@PathVariable Long id) {
        log.debug("REST request to get Shift : {}", id);
        Optional<ShiftDTO> shiftDTO = shiftService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shiftDTO);
    }

    /**
     * {@code DELETE  /shifts/:id} : delete the "id" shift.
     *
     * @param id the id of the shiftDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/shifts/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        log.debug("REST request to delete Shift : {}", id);
        shiftService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
