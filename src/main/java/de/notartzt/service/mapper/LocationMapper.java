package de.notartzt.service.mapper;

import de.notartzt.domain.Location;
import de.notartzt.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Location} and its DTO {@link LocationDTO}.
 */
@Mapper(componentModel = "spring")
public interface LocationMapper extends EntityMapper<LocationDTO, Location> {}
