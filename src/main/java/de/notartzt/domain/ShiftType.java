package de.notartzt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ShiftType.
 */
@Entity
@Table(name = "shift_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ShiftType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Min(value = 0)
    @Max(value = 23)
    @Column(name = "start_hour", nullable = false)
    private Integer startHour;

    @NotNull
    @Min(value = 0)
    @Max(value = 59)
    @Column(name = "start_minute", nullable = false)
    private Integer startMinute;

    @NotNull
    @Min(value = 0)
    @Max(value = 23)
    @Column(name = "end_hour", nullable = false)
    private Integer endHour;

    @NotNull
    @Min(value = 0)
    @Max(value = 59)
    @Column(name = "end_minute", nullable = false)
    private Integer endMinute;

    @OneToMany(mappedBy = "type")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "workLogs", "type" }, allowSetters = true)
    private Set<Shift> shifts = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "shiftTypes" }, allowSetters = true)
    private Location location;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShiftType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ShiftType name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStartHour() {
        return this.startHour;
    }

    public ShiftType startHour(Integer startHour) {
        this.setStartHour(startHour);
        return this;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getStartMinute() {
        return this.startMinute;
    }

    public ShiftType startMinute(Integer startMinute) {
        this.setStartMinute(startMinute);
        return this;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public Integer getEndHour() {
        return this.endHour;
    }

    public ShiftType endHour(Integer endHour) {
        this.setEndHour(endHour);
        return this;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public Integer getEndMinute() {
        return this.endMinute;
    }

    public ShiftType endMinute(Integer endMinute) {
        this.setEndMinute(endMinute);
        return this;
    }

    public void setEndMinute(Integer endMinute) {
        this.endMinute = endMinute;
    }

    public Set<Shift> getShifts() {
        return this.shifts;
    }

    public void setShifts(Set<Shift> shifts) {
        if (this.shifts != null) {
            this.shifts.forEach(i -> i.setType(null));
        }
        if (shifts != null) {
            shifts.forEach(i -> i.setType(this));
        }
        this.shifts = shifts;
    }

    public ShiftType shifts(Set<Shift> shifts) {
        this.setShifts(shifts);
        return this;
    }

    public ShiftType addShift(Shift shift) {
        this.shifts.add(shift);
        shift.setType(this);
        return this;
    }

    public ShiftType removeShift(Shift shift) {
        this.shifts.remove(shift);
        shift.setType(null);
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ShiftType location(Location location) {
        this.setLocation(location);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShiftType)) {
            return false;
        }
        return id != null && id.equals(((ShiftType) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShiftType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", startHour=" + getStartHour() +
            ", startMinute=" + getStartMinute() +
            ", endHour=" + getEndHour() +
            ", endMinute=" + getEndMinute() +
            "}";
    }
}
