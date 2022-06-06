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
 * Criteria class for the {@link de.notartzt.domain.WorkLog} entity. This class is used
 * in {@link de.notartzt.web.rest.WorkLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /work-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class WorkLogCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter note;

    private IntegerFilter startHour;

    private IntegerFilter startMinute;

    private IntegerFilter endHour;

    private IntegerFilter endMinute;

    private BooleanFilter optional;

    private LongFilter shiftId;

    private Boolean distinct;

    public WorkLogCriteria() {}

    public WorkLogCriteria(WorkLogCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.note = other.note == null ? null : other.note.copy();
        this.startHour = other.startHour == null ? null : other.startHour.copy();
        this.startMinute = other.startMinute == null ? null : other.startMinute.copy();
        this.endHour = other.endHour == null ? null : other.endHour.copy();
        this.endMinute = other.endMinute == null ? null : other.endMinute.copy();
        this.optional = other.optional == null ? null : other.optional.copy();
        this.shiftId = other.shiftId == null ? null : other.shiftId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public WorkLogCriteria copy() {
        return new WorkLogCriteria(this);
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

    public StringFilter getNote() {
        return note;
    }

    public StringFilter note() {
        if (note == null) {
            note = new StringFilter();
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
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

    public BooleanFilter getOptional() {
        return optional;
    }

    public BooleanFilter optional() {
        if (optional == null) {
            optional = new BooleanFilter();
        }
        return optional;
    }

    public void setOptional(BooleanFilter optional) {
        this.optional = optional;
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
        final WorkLogCriteria that = (WorkLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(note, that.note) &&
            Objects.equals(startHour, that.startHour) &&
            Objects.equals(startMinute, that.startMinute) &&
            Objects.equals(endHour, that.endHour) &&
            Objects.equals(endMinute, that.endMinute) &&
            Objects.equals(optional, that.optional) &&
            Objects.equals(shiftId, that.shiftId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, note, startHour, startMinute, endHour, endMinute, optional, shiftId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkLogCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (note != null ? "note=" + note + ", " : "") +
            (startHour != null ? "startHour=" + startHour + ", " : "") +
            (startMinute != null ? "startMinute=" + startMinute + ", " : "") +
            (endHour != null ? "endHour=" + endHour + ", " : "") +
            (endMinute != null ? "endMinute=" + endMinute + ", " : "") +
            (optional != null ? "optional=" + optional + ", " : "") +
            (shiftId != null ? "shiftId=" + shiftId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
