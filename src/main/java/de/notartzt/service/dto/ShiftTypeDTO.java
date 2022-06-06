package de.notartzt.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link de.notartzt.domain.ShiftType} entity.
 */
public class ShiftTypeDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Min(value = 0)
    @Max(value = 23)
    private Integer startHour;

    @NotNull
    @Min(value = 0)
    @Max(value = 59)
    private Integer startMinute;

    @NotNull
    @Min(value = 0)
    @Max(value = 23)
    private Integer endHour;

    @NotNull
    @Min(value = 0)
    @Max(value = 59)
    private Integer endMinute;

    private LocationDTO location;

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

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public Integer getEndHour() {
        return endHour;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public Integer getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(Integer endMinute) {
        this.endMinute = endMinute;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShiftTypeDTO)) {
            return false;
        }

        ShiftTypeDTO shiftTypeDTO = (ShiftTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shiftTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShiftTypeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", startHour=" + getStartHour() +
            ", startMinute=" + getStartMinute() +
            ", endHour=" + getEndHour() +
            ", endMinute=" + getEndMinute() +
            ", location=" + getLocation() +
            "}";
    }
}
