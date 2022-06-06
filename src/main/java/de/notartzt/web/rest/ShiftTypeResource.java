package de.notartzt.web.rest;

import de.notartzt.repository.ShiftTypeRepository;
import de.notartzt.service.ShiftTypeQueryService;
import de.notartzt.service.ShiftTypeService;
import de.notartzt.service.criteria.ShiftTypeCriteria;
import de.notartzt.service.dto.ShiftTypeDTO;
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
 * REST controller for managing {@link de.notartzt.domain.ShiftType}.
 */
@RestController
@RequestMapping("/api")
public class ShiftTypeResource {

    private final Logger log = LoggerFactory.getLogger(ShiftTypeResource.class);

    private static final String ENTITY_NAME = "shiftType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShiftTypeService shiftTypeService;

    private final ShiftTypeRepository shiftTypeRepository;

    private final ShiftTypeQueryService shiftTypeQueryService;

    public ShiftTypeResource(
        ShiftTypeService shiftTypeService,
        ShiftTypeRepository shiftTypeRepository,
        ShiftTypeQueryService shiftTypeQueryService
    ) {
        this.shiftTypeService = shiftTypeService;
        this.shiftTypeRepository = shiftTypeRepository;
        this.shiftTypeQueryService = shiftTypeQueryService;
    }

    /**
     * {@code POST  /shift-types} : Create a new shiftType.
     *
     * @param shiftTypeDTO the shiftTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shiftTypeDTO, or with status {@code 400 (Bad Request)} if the shiftType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/shift-types")
    public ResponseEntity<ShiftTypeDTO> createShiftType(@Valid @RequestBody ShiftTypeDTO shiftTypeDTO) throws URISyntaxException {
        log.debug("REST request to save ShiftType : {}", shiftTypeDTO);
        if (shiftTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new shiftType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ShiftTypeDTO result = shiftTypeService.save(shiftTypeDTO);
        return ResponseEntity
            .created(new URI("/api/shift-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /shift-types/:id} : Updates an existing shiftType.
     *
     * @param id the id of the shiftTypeDTO to save.
     * @param shiftTypeDTO the shiftTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shiftTypeDTO,
     * or with status {@code 400 (Bad Request)} if the shiftTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shiftTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/shift-types/{id}")
    public ResponseEntity<ShiftTypeDTO> updateShiftType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShiftTypeDTO shiftTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ShiftType : {}, {}", id, shiftTypeDTO);
        if (shiftTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shiftTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shiftTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ShiftTypeDTO result = shiftTypeService.update(shiftTypeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shiftTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /shift-types/:id} : Partial updates given fields of an existing shiftType, field will ignore if it is null
     *
     * @param id the id of the shiftTypeDTO to save.
     * @param shiftTypeDTO the shiftTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shiftTypeDTO,
     * or with status {@code 400 (Bad Request)} if the shiftTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shiftTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shiftTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/shift-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShiftTypeDTO> partialUpdateShiftType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShiftTypeDTO shiftTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShiftType partially : {}, {}", id, shiftTypeDTO);
        if (shiftTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shiftTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shiftTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShiftTypeDTO> result = shiftTypeService.partialUpdate(shiftTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shiftTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shift-types} : get all the shiftTypes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shiftTypes in body.
     */
    @GetMapping("/shift-types")
    public ResponseEntity<List<ShiftTypeDTO>> getAllShiftTypes(
        ShiftTypeCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get ShiftTypes by criteria: {}", criteria);
        Page<ShiftTypeDTO> page = shiftTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shift-types/count} : count all the shiftTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/shift-types/count")
    public ResponseEntity<Long> countShiftTypes(ShiftTypeCriteria criteria) {
        log.debug("REST request to count ShiftTypes by criteria: {}", criteria);
        return ResponseEntity.ok().body(shiftTypeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /shift-types/:id} : get the "id" shiftType.
     *
     * @param id the id of the shiftTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shiftTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/shift-types/{id}")
    public ResponseEntity<ShiftTypeDTO> getShiftType(@PathVariable Long id) {
        log.debug("REST request to get ShiftType : {}", id);
        Optional<ShiftTypeDTO> shiftTypeDTO = shiftTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shiftTypeDTO);
    }

    /**
     * {@code DELETE  /shift-types/:id} : delete the "id" shiftType.
     *
     * @param id the id of the shiftTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/shift-types/{id}")
    public ResponseEntity<Void> deleteShiftType(@PathVariable Long id) {
        log.debug("REST request to delete ShiftType : {}", id);
        shiftTypeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
