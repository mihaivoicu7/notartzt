package de.notartzt.service;

import de.notartzt.domain.*; // for static metamodels
import de.notartzt.domain.Shift;
import de.notartzt.repository.ShiftRepository;
import de.notartzt.service.criteria.ShiftCriteria;
import de.notartzt.service.dto.ShiftDTO;
import de.notartzt.service.mapper.ShiftMapper;
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
 * Service for executing complex queries for {@link Shift} entities in the database.
 * The main input is a {@link ShiftCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ShiftDTO} or a {@link Page} of {@link ShiftDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShiftQueryService extends QueryService<Shift> {

    private final Logger log = LoggerFactory.getLogger(ShiftQueryService.class);

    private final ShiftRepository shiftRepository;

    private final ShiftMapper shiftMapper;

    public ShiftQueryService(ShiftRepository shiftRepository, ShiftMapper shiftMapper) {
        this.shiftRepository = shiftRepository;
        this.shiftMapper = shiftMapper;
    }

    /**
     * Return a {@link List} of {@link ShiftDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ShiftDTO> findByCriteria(ShiftCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Shift> specification = createSpecification(criteria);
        return shiftMapper.toDto(shiftRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ShiftDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ShiftDTO> findByCriteria(ShiftCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Shift> specification = createSpecification(criteria);
        return shiftRepository.findAll(specification, page).map(shiftMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShiftCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Shift> specification = createSpecification(criteria);
        return shiftRepository.count(specification);
    }

    /**
     * Function to convert {@link ShiftCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Shift> createSpecification(ShiftCriteria criteria) {
        Specification<Shift> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Shift_.id));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Shift_.date));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Shift_.status));
            }
            if (criteria.getWorkLogId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getWorkLogId(), root -> root.join(Shift_.workLogs, JoinType.LEFT).get(WorkLog_.id))
                    );
            }
            if (criteria.getTypeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTypeId(), root -> root.join(Shift_.type, JoinType.LEFT).get(ShiftType_.id))
                    );
            }
        }
        return specification;
    }
}
