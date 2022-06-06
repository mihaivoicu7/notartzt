package de.notartzt.repository;

import de.notartzt.domain.Shift;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Shift entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long>, JpaSpecificationExecutor<Shift> {}
