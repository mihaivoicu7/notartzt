package de.notartzt.service.mapper;

import de.notartzt.domain.Location;
import de.notartzt.domain.ShiftType;
import de.notartzt.service.dto.LocationDTO;
import de.notartzt.service.dto.ShiftTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ShiftType} and its DTO {@link ShiftTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShiftTypeMapper extends EntityMapper<ShiftTypeDTO, ShiftType> {
    @Mapping(target = "location", source = "location", qualifiedByName = "locationId")
    ShiftTypeDTO toDto(ShiftType s);

    @Named("locationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LocationDTO toDtoLocationId(Location location);
}
