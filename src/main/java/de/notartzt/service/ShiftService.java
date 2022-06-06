package de.notartzt.service;

import de.notartzt.service.dto.ShiftDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link de.notartzt.domain.Shift}.
 */
public interface ShiftService {
    /**
     * Save a shift.
     *
     * @param shiftDTO the entity to save.
     * @return the persisted entity.
     */
    ShiftDTO save(ShiftDTO shiftDTO);

    /**
     * Updates a shift.
     *
     * @param shiftDTO the entity to update.
     * @return the persisted entity.
     */
    ShiftDTO update(ShiftDTO shiftDTO);

    /**
     * Partially updates a shift.
     *
     * @param shiftDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ShiftDTO> partialUpdate(ShiftDTO shiftDTO);

    /**
     * Get all the shifts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ShiftDTO> findAll(Pageable pageable);

    /**
     * Get the "id" shift.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ShiftDTO> findOne(Long id);

    /**
     * Delete the "id" shift.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
