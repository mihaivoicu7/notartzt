package de.notartzt.service;

import de.notartzt.domain.*; // for static metamodels
import de.notartzt.domain.WorkLog;
import de.notartzt.repository.WorkLogRepository;
import de.notartzt.service.criteria.WorkLogCriteria;
import de.notartzt.service.dto.WorkLogDTO;
import de.notartzt.service.mapper.WorkLogMapper;
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
 * Service for executing complex queries for {@link WorkLog} entities in the database.
 * The main input is a {@link WorkLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WorkLogDTO} or a {@link Page} of {@link WorkLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WorkLogQueryService extends QueryService<WorkLog> {

    private final Logger log = LoggerFactory.getLogger(WorkLogQueryService.class);

    private final WorkLogRepository workLogRepository;

    private final WorkLogMapper workLogMapper;

    public WorkLogQueryService(WorkLogRepository workLogRepository, WorkLogMapper workLogMapper) {
        this.workLogRepository = workLogRepository;
        this.workLogMapper = workLogMapper;
    }

    /**
     * Return a {@link List} of {@link WorkLogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WorkLogDTO> findByCriteria(WorkLogCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<WorkLog> specification = createSpecification(criteria);
        return workLogMapper.toDto(workLogRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link WorkLogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkLogDTO> findByCriteria(WorkLogCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WorkLog> specification = createSpecification(criteria);
        return workLogRepository.findAll(specification, page).map(workLogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WorkLogCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<WorkLog> specification = createSpecification(criteria);
        return workLogRepository.count(specification);
    }

    /**
     * Function to convert {@link WorkLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WorkLog> createSpecification(WorkLogCriteria criteria) {
        Specification<WorkLog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), WorkLog_.id));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), WorkLog_.note));
            }
            if (criteria.getStartHour() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartHour(), WorkLog_.startHour));
            }
            if (criteria.getStartMinute() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartMinute(), WorkLog_.startMinute));
            }
            if (criteria.getEndHour() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndHour(), WorkLog_.endHour));
            }
            if (criteria.getEndMinute() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndMinute(), WorkLog_.endMinute));
            }
            if (criteria.getOptional() != null) {
                specification = specification.and(buildSpecification(criteria.getOptional(), WorkLog_.optional));
            }
            if (criteria.getShiftId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getShiftId(), root -> root.join(WorkLog_.shift, JoinType.LEFT).get(Shift_.id))
                    );
            }
        }
        return specification;
    }
}
