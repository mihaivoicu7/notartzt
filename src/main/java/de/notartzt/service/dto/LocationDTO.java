package de.notartzt.service.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link de.notartzt.domain.Location} entity.
 */
public class LocationDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private List<ShiftTypeDTO> shiftTypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ShiftTypeDTO> getShiftTypes() {
        return shiftTypes;
    }

    public void setShiftTypes(List<ShiftTypeDTO> shiftTypes) {
        this.shiftTypes = shiftTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocationDTO)) {
            return false;
        }

        LocationDTO locationDTO = (LocationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, locationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
