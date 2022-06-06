package de.notartzt.repository;

import de.notartzt.domain.WorkLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the WorkLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {}
