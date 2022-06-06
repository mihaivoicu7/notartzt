package de.notartzt.repository;

import de.notartzt.domain.ShiftType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ShiftType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShiftTypeRepository extends JpaRepository<ShiftType, Long>, JpaSpecificationExecutor<ShiftType> {}
