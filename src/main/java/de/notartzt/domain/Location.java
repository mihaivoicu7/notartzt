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
 * A Location.
 */
@Entity
@Table(name = "location")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "location")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "shifts", "location" }, allowSetters = true)
    private Set<ShiftType> shiftTypes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Location id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Location name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ShiftType> getShiftTypes() {
        return this.shiftTypes;
    }

    public void setShiftTypes(Set<ShiftType> shiftTypes) {
        if (this.shiftTypes != null) {
            this.shiftTypes.forEach(i -> i.setLocation(null));
        }
        if (shiftTypes != null) {
            shiftTypes.forEach(i -> i.setLocation(this));
        }
        this.shiftTypes = shiftTypes;
    }

    public Location shiftTypes(Set<ShiftType> shiftTypes) {
        this.setShiftTypes(shiftTypes);
        return this;
    }

    public Location addShiftType(ShiftType shiftType) {
        this.shiftTypes.add(shiftType);
        shiftType.setLocation(this);
        return this;
    }

    public Location removeShiftType(ShiftType shiftType) {
        this.shiftTypes.remove(shiftType);
        shiftType.setLocation(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Location)) {
            return false;
        }
        return id != null && id.equals(((Location) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Location{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
