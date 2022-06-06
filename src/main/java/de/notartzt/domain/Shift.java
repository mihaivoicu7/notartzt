package de.notartzt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.notartzt.domain.enumeration.ShiftStatus;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Shift.
 */
@Entity
@Table(name = "shift")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Shift implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShiftStatus status;

    @OneToMany(mappedBy = "shift")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "shift" }, allowSetters = true)
    private Set<WorkLog> workLogs = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "shifts", "location" }, allowSetters = true)
    private ShiftType type;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Shift id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Shift date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ShiftStatus getStatus() {
        return this.status;
    }

    public Shift status(ShiftStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ShiftStatus status) {
        this.status = status;
    }

    public Set<WorkLog> getWorkLogs() {
        return this.workLogs;
    }

    public void setWorkLogs(Set<WorkLog> workLogs) {
        if (this.workLogs != null) {
            this.workLogs.forEach(i -> i.setShift(null));
        }
        if (workLogs != null) {
            workLogs.forEach(i -> i.setShift(this));
        }
        this.workLogs = workLogs;
    }

    public Shift workLogs(Set<WorkLog> workLogs) {
        this.setWorkLogs(workLogs);
        return this;
    }

    public Shift addWorkLog(WorkLog workLog) {
        this.workLogs.add(workLog);
        workLog.setShift(this);
        return this;
    }

    public Shift removeWorkLog(WorkLog workLog) {
        this.workLogs.remove(workLog);
        workLog.setShift(null);
        return this;
    }

    public ShiftType getType() {
        return this.type;
    }

    public void setType(ShiftType shiftType) {
        this.type = shiftType;
    }

    public Shift type(ShiftType shiftType) {
        this.setType(shiftType);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shift)) {
            return false;
        }
        return id != null && id.equals(((Shift) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Shift{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
