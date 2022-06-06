package de.notartzt.service.mapper;

import de.notartzt.domain.Shift;
import de.notartzt.domain.WorkLog;
import de.notartzt.service.dto.ShiftDTO;
import de.notartzt.service.dto.WorkLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WorkLog} and its DTO {@link WorkLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkLogMapper extends EntityMapper<WorkLogDTO, WorkLog> {
    @Mapping(target = "shift", source = "shift", qualifiedByName = "shiftId")
    WorkLogDTO toDto(WorkLog s);

    @Named("shiftId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShiftDTO toDtoShiftId(Shift shift);
}
