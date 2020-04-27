package com.dgtd.security.domain.entity;

import org.springframework.stereotype.Component;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Component
@Entity
public class Profil implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String intitule;


    public Profil() {
    }

    public Profil(int id, String intitule) {

        this.id = id;
        this.intitule = intitule;
    }


    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "intitule", nullable = false, length = 50)
    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profil profil = (Profil) o;
        return getId() == profil.getId() &&
                Objects.equals(getIntitule(), profil.getIntitule());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIntitule());
    }
}
