package de.notartzt.service.mapper;

import de.notartzt.domain.Shift;
import de.notartzt.domain.ShiftType;
import de.notartzt.service.dto.ShiftDTO;
import de.notartzt.service.dto.ShiftTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Shift} and its DTO {@link ShiftDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShiftMapper extends EntityMapper<ShiftDTO, Shift> {
    @Mapping(target = "type", source = "type", qualifiedByName = "shiftTypeId")
    ShiftDTO toDto(Shift s);

    @Named("shiftTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShiftTypeDTO toDtoShiftTypeId(ShiftType shiftType);
}
