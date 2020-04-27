package com.dgtd.rdc.entity;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

@Entity
@Component
public class Educationnelle implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String province;
    private String sousDivision;
    private Collection<Ecole> ecolesById;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "province",  length = 50)
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Basic
    @Column(name = "sous_division", length = 50)
    public String getSousDivision() {
        return sousDivision;
    }

    public void setSousDivision(String sousDivision) {
        this.sousDivision = sousDivision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Educationnelle that = (Educationnelle) o;
        return id == that.id &&
                Objects.equals(province, that.province) &&
                Objects.equals(sousDivision, that.sousDivision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, province, sousDivision);
    }

    @OneToMany(mappedBy = "educationnelleByEducationnelleId")
    public Collection<Ecole> getEcolesById() {
        return ecolesById;
    }

    public void setEcolesById(Collection<Ecole> ecolesById) {
        this.ecolesById = ecolesById;
    }
}
