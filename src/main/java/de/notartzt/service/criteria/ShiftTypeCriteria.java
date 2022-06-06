package de.notartzt.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link de.notartzt.domain.ShiftType} entity. This class is used
 * in {@link de.notartzt.web.rest.ShiftTypeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /shift-types?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ShiftTypeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private IntegerFilter startHour;

    private IntegerFilter startMinute;

    private IntegerFilter endHour;

    private IntegerFilter endMinute;

    private LongFilter shiftId;

    private LongFilter locationId;

    private Boolean distinct;

    public ShiftTypeCriteria() {}

    public ShiftTypeCriteria(ShiftTypeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.startHour = other.startHour == null ? null : other.startHour.copy();
        this.startMinute = other.startMinute == null ? null : other.startMinute.copy();
        this.endHour = other.endHour == null ? null : other.endHour.copy();
        this.endMinute = other.endMinute == null ? null : other.endMinute.copy();
        this.shiftId = other.shiftId == null ? null : other.shiftId.copy();
        this.locationId = other.locationId == null ? null : other.locationId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ShiftTypeCriteria copy() {
        return new ShiftTypeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public IntegerFilter getStartHour() {
        return startHour;
    }

    public IntegerFilter startHour() {
        if (startHour == null) {
            startHour = new IntegerFilter();
        }
        return startHour;
    }

    public void setStartHour(IntegerFilter startHour) {
        this.startHour = startHour;
    }

    public IntegerFilter getStartMinute() {
        return startMinute;
    }

    public IntegerFilter startMinute() {
        if (startMinute == null) {
            startMinute = new IntegerFilter();
        }
        return startMinute;
    }

    public void setStartMinute(IntegerFilter startMinute) {
        this.startMinute = startMinute;
    }

    public IntegerFilter getEndHour() {
        return endHour;
    }

    public IntegerFilter endHour() {
        if (endHour == null) {
            endHour = new IntegerFilter();
        }
        return endHour;
    }

    public void setEndHour(IntegerFilter endHour) {
        this.endHour = endHour;
    }

    public IntegerFilter getEndMinute() {
        return endMinute;
    }

    public IntegerFilter endMinute() {
        if (endMinute == null) {
            endMinute = new IntegerFilter();
        }
        return endMinute;
    }

    public void setEndMinute(IntegerFilter endMinute) {
        this.endMinute = endMinute;
    }

    public LongFilter getShiftId() {
        return shiftId;
    }

    public LongFilter shiftId() {
        if (shiftId == null) {
            shiftId = new LongFilter();
        }
        return shiftId;
    }

    public void setShiftId(LongFilter shiftId) {
        this.shiftId = shiftId;
    }

    public LongFilter getLocationId() {
        return locationId;
    }

    public LongFilter locationId() {
        if (locationId == null) {
            locationId = new LongFilter();
        }
        return locationId;
    }

    public void setLocationId(LongFilter locationId) {
        this.locationId = locationId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ShiftTypeCriteria that = (ShiftTypeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(startHour, that.startHour) &&
            Objects.equals(startMinute, that.startMinute) &&
            Objects.equals(endHour, that.endHour) &&
            Objects.equals(endMinute, that.endMinute) &&
            Objects.equals(shiftId, that.shiftId) &&
            Objects.equals(locationId, that.locationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startHour, startMinute, endHour, endMinute, shiftId, locationId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShiftTypeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (startHour != null ? "startHour=" + startHour + ", " : "") +
            (startMinute != null ? "startMinute=" + startMinute + ", " : "") +
            (endHour != null ? "endHour=" + endHour + ", " : "") +
            (endMinute != null ? "endMinute=" + endMinute + ", " : "") +
            (shiftId != null ? "shiftId=" + shiftId + ", " : "") +
            (locationId != null ? "locationId=" + locationId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
