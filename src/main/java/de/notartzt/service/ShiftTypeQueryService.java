package de.notartzt.service;

import de.notartzt.domain.*; // for static metamodels
import de.notartzt.domain.ShiftType;
import de.notartzt.repository.ShiftTypeRepository;
import de.notartzt.service.criteria.ShiftTypeCriteria;
import de.notartzt.service.dto.ShiftTypeDTO;
import de.notartzt.service.mapper.ShiftTypeMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ShiftType} entities in the database.
 * The main input is a {@link ShiftTypeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ShiftTypeDTO} or a {@link Page} of {@link ShiftTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShiftTypeQueryService extends QueryService<ShiftType> {

    private final Logger log = LoggerFactory.getLogger(ShiftTypeQueryService.class);

    private final ShiftTypeRepository shiftTypeRepository;

    private final ShiftTypeMapper shiftTypeMapper;

    public ShiftTypeQueryService(ShiftTypeRepository shiftTypeRepository, ShiftTypeMapper shiftTypeMapper) {
        this.shiftTypeRepository = shiftTypeRepository;
        this.shiftTypeMapper = shiftTypeMapper;
    }

    /**
     * Return a {@link List} of {@link ShiftTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ShiftTypeDTO> findByCriteria(ShiftTypeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ShiftType> specification = createSpecification(criteria);
        return shiftTypeMapper.toDto(shiftTypeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ShiftTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ShiftTypeDTO> findByCriteria(ShiftTypeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ShiftType> specification = createSpecification(criteria);
        return shiftTypeRepository.findAll(specification, page).map(shiftTypeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShiftTypeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ShiftType> specification = createSpecification(criteria);
        return shiftTypeRepository.count(specification);
    }

    /**
     * Function to convert {@link ShiftTypeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ShiftType> createSpecification(ShiftTypeCriteria criteria) {
        Specification<ShiftType> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ShiftType_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), ShiftType_.name));
            }
            if (criteria.getStartHour() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartHour(), ShiftType_.startHour));
            }
            if (criteria.getStartMinute() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartMinute(), ShiftType_.startMinute));
            }
            if (criteria.getEndHour() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndHour(), ShiftType_.endHour));
            }
            if (criteria.getEndMinute() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndMinute(), ShiftType_.endMinute));
            }
            if (criteria.getShiftId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getShiftId(), root -> root.join(ShiftType_.shifts, JoinType.LEFT).get(Shift_.id))
                    );
            }
            if (criteria.getLocationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLocationId(),
                            root -> root.join(ShiftType_.location, JoinType.LEFT).get(Location_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
