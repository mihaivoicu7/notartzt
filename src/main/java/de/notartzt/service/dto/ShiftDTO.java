package de.notartzt.service.dto;

import de.notartzt.domain.enumeration.ShiftStatus;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link de.notartzt.domain.Shift} entity.
 */
public class ShiftDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDate date;

    private ShiftStatus status;

    private ShiftTypeDTO type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ShiftStatus getStatus() {
        return status;
    }

    public void setStatus(ShiftStatus status) {
        this.status = status;
    }

    public ShiftTypeDTO getType() {
        return type;
    }

    public void setType(ShiftTypeDTO type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShiftDTO)) {
            return false;
        }

        ShiftDTO shiftDTO = (ShiftDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shiftDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShiftDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", type=" + getType() +
            "}";
    }
}
