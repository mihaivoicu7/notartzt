package de.notartzt.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link de.notartzt.domain.WorkLog} entity.
 */
public class WorkLogDTO implements Serializable {

    private Long id;

    private String note;

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

    private Boolean optional;

    private ShiftDTO shift;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public ShiftDTO getShift() {
        return shift;
    }

    public void setShift(ShiftDTO shift) {
        this.shift = shift;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkLogDTO)) {
            return false;
        }

        WorkLogDTO workLogDTO = (WorkLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkLogDTO{" +
            "id=" + getId() +
            ", note='" + getNote() + "'" +
            ", startHour=" + getStartHour() +
            ", startMinute=" + getStartMinute() +
            ", endHour=" + getEndHour() +
            ", endMinute=" + getEndMinute() +
            ", optional='" + getOptional() + "'" +
            ", shift=" + getShift() +
            "}";
    }
}
