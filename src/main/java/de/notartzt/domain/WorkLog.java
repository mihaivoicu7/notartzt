package de.notartzt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WorkLog.
 */
@Entity
@Table(name = "work_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WorkLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "note")
    private String note;

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

    @Column(name = "optional")
    private Boolean optional;

    @ManyToOne
    @JsonIgnoreProperties(value = { "workLogs", "type" }, allowSetters = true)
    private Shift shift;

    @ManyToOne
    @JsonIgnoreProperties(value = { "workLogs", "type" }, allowSetters = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WorkLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNote() {
        return this.note;
    }

    public WorkLog note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getStartHour() {
        return this.startHour;
    }

    public WorkLog startHour(Integer startHour) {
        this.setStartHour(startHour);
        return this;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getStartMinute() {
        return this.startMinute;
    }

    public WorkLog startMinute(Integer startMinute) {
        this.setStartMinute(startMinute);
        return this;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public Integer getEndHour() {
        return this.endHour;
    }

    public WorkLog endHour(Integer endHour) {
        this.setEndHour(endHour);
        return this;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public Integer getEndMinute() {
        return this.endMinute;
    }

    public WorkLog endMinute(Integer endMinute) {
        this.setEndMinute(endMinute);
        return this;
    }

    public void setEndMinute(Integer endMinute) {
        this.endMinute = endMinute;
    }

    public Boolean getOptional() {
        return this.optional;
    }

    public WorkLog optional(Boolean optional) {
        this.setOptional(optional);
        return this;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public Shift getShift() {
        return this.shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public WorkLog shift(Shift shift) {
        this.setShift(shift);
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkLog)) {
            return false;
        }
        return id != null && id.equals(((WorkLog) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkLog{" +
            "id=" + getId() +
            ", note='" + getNote() + "'" +
            ", startHour=" + getStartHour() +
            ", startMinute=" + getStartMinute() +
            ", endHour=" + getEndHour() +
            ", endMinute=" + getEndMinute() +
            ", optional='" + getOptional() + "'" +
            "}";
    }
}
