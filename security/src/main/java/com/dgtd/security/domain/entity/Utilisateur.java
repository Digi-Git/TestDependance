package com.dgtd.security.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.BatchSize;
import javax.persistence.Id;
import org.springframework.stereotype.Component;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

@Component
@Entity
public class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String identifiant;
    private int isAffecte;
    private String motDePasse;
    private String nom;
    private int parentId;
    private String prenom;
    private Collection<Profil> profils;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "identifiant", nullable = false, length = 50)
    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    @Basic
    @Column(name = "is_affecte", nullable = false)
    public int getIsAffecte() {
        return isAffecte;
    }

    public void setIsAffecte(int isAffecte) {
        this.isAffecte = isAffecte;
    }

    @JsonIgnore
    @Basic
    @Column(name = "mot_de_passe", nullable = false, length = 60)
    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    @Basic
    @Column(name = "nom",length = 50)
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Basic
    @Column(name = "parent_id")
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "prenom", length = 50)
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return getId() == that.getId() &&
                getIsAffecte() == that.getIsAffecte() &&
                Objects.equals(getIdentifiant(), that.getIdentifiant()) &&
                Objects.equals(getMotDePasse(), that.getMotDePasse()) &&
                Objects.equals(getNom(), that.getNom()) &&
                Objects.equals(getPrenom(), that.getPrenom()) &&
                Objects.equals(getProfils(), that.getProfils());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIdentifiant(), getIsAffecte(), getMotDePasse(), getNom(), getPrenom(), getProfils());
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "profil_utilisateur",
            joinColumns = {@JoinColumn(name = "utilisateur_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "profil_id", referencedColumnName = "id")}
    )
    @BatchSize(size = 20)
    public Collection<Profil> getProfils() {
        return profils;
    }

    public void setProfils(Collection<Profil> profils) {
        this.profils = profils;
    }
}
