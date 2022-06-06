package de.notartzt.service.criteria;

import de.notartzt.domain.enumeration.ShiftStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link de.notartzt.domain.Shift} entity. This class is used
 * in {@link de.notartzt.web.rest.ShiftResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /shifts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ShiftCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ShiftStatus
     */
    public static class ShiftStatusFilter extends Filter<ShiftStatus> {

        public ShiftStatusFilter() {}

        public ShiftStatusFilter(ShiftStatusFilter filter) {
            super(filter);
        }

        @Override
        public ShiftStatusFilter copy() {
            return new ShiftStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter date;

    private ShiftStatusFilter status;

    private LongFilter workLogId;

    private LongFilter typeId;

    private Boolean distinct;

    public ShiftCriteria() {}

    public ShiftCriteria(ShiftCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.workLogId = other.workLogId == null ? null : other.workLogId.copy();
        this.typeId = other.typeId == null ? null : other.typeId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ShiftCriteria copy() {
        return new ShiftCriteria(this);
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

    public LocalDateFilter getDate() {
        return date;
    }

    public LocalDateFilter date() {
        if (date == null) {
            date = new LocalDateFilter();
        }
        return date;
    }

    public void setDate(LocalDateFilter date) {
        this.date = date;
    }

    public ShiftStatusFilter getStatus() {
        return status;
    }

    public ShiftStatusFilter status() {
        if (status == null) {
            status = new ShiftStatusFilter();
        }
        return status;
    }

    public void setStatus(ShiftStatusFilter status) {
        this.status = status;
    }

    public LongFilter getWorkLogId() {
        return workLogId;
    }

    public LongFilter workLogId() {
        if (workLogId == null) {
            workLogId = new LongFilter();
        }
        return workLogId;
    }

    public void setWorkLogId(LongFilter workLogId) {
        this.workLogId = workLogId;
    }

    public LongFilter getTypeId() {
        return typeId;
    }

    public LongFilter typeId() {
        if (typeId == null) {
            typeId = new LongFilter();
        }
        return typeId;
    }

    public void setTypeId(LongFilter typeId) {
        this.typeId = typeId;
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
        final ShiftCriteria that = (ShiftCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(status, that.status) &&
            Objects.equals(workLogId, that.workLogId) &&
            Objects.equals(typeId, that.typeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, status, workLogId, typeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShiftCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (workLogId != null ? "workLogId=" + workLogId + ", " : "") +
            (typeId != null ? "typeId=" + typeId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
