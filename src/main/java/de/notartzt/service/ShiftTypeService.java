package de.notartzt.service;

import de.notartzt.service.dto.ShiftTypeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link de.notartzt.domain.ShiftType}.
 */
public interface ShiftTypeService {
    /**
     * Save a shiftType.
     *
     * @param shiftTypeDTO the entity to save.
     * @return the persisted entity.
     */
    ShiftTypeDTO save(ShiftTypeDTO shiftTypeDTO);

    /**
     * Updates a shiftType.
     *
     * @param shiftTypeDTO the entity to update.
     * @return the persisted entity.
     */
    ShiftTypeDTO update(ShiftTypeDTO shiftTypeDTO);

    /**
     * Partially updates a shiftType.
     *
     * @param shiftTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ShiftTypeDTO> partialUpdate(ShiftTypeDTO shiftTypeDTO);

    /**
     * Get all the shiftTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ShiftTypeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" shiftType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ShiftTypeDTO> findOne(Long id);

    /**
     * Delete the "id" shiftType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
