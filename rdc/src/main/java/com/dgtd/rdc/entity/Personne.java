package com.dgtd.rdc.entity;

import com.dgtd.rdc.localisation.entity.Localisation;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Component
public class Personne implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idPersonne;
    private String avenue;
    private String dateNaissance;
    private String email;
    private String genre;
    private Integer localisationNaissanceId;
    private Integer localisationResidenceId;
    private String naissanceAutre;
    private String nationalite;
    private String nationaliteAutre;
    private String nom;
    private String numero;
    private String postnom;
    private String prenom;
    private String profession;
    private String professionAutre;
    private String qualite;
    private String qualiteAutre;
    private String quartier;
    private String residenceAutre;
    private String tel;
    private String typePersonne;
    private Dossier dossierByDossierId;
    private Localisation localisationByLocalisationNaissanceId;
    private Localisation localisationByLocalisationResidenceId;

    public Personne(){
        setGenre("");
        setQualite("");
        setQualiteAutre("");
        setPostnom("");
        setTel("");
        setEmail("");
    }

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(int idPersonne) {
        this.idPersonne = idPersonne;
    }

    @Basic
    @Column(name = "avenue",  length = 50)
    public String getAvenue() {
        return avenue;
    }

    public void setAvenue(String avenue) {
        this.avenue = avenue;
    }

    @Basic
    @Column(name = "date_naissance",  length = 10)
    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }


    @Basic
    @Column(name = "email",  length = 100)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "genre",  length = 20)
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Basic
    @Column(name = "localisation_naissance_id")
    public Integer getLocalisationNaissanceId() {
        return localisationNaissanceId;
    }

    public void setLocalisationNaissanceId(Integer localisationNaissanceId) {
        this.localisationNaissanceId = localisationNaissanceId;
    }

    @Basic
    @Column(name = "localisation_residence_id")
    public Integer getLocalisationResidenceId() {
        return localisationResidenceId;
    }

    public void setLocalisationResidenceId(Integer localisationResidenceId) {
        this.localisationResidenceId = localisationResidenceId;
    }

    @Basic
    @Column(name = "naissance_autre",  length = 50)
    public String getNaissanceAutre() {
        return naissanceAutre;
    }

    public void setNaissanceAutre(String naissanceAutre) {
        this.naissanceAutre = naissanceAutre;
    }

    @Basic
    @Column(name = "nationalite",  length = 50)
    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    @Basic
    @Column(name = "nationalite_autre",  length = 50)
    public String getNationaliteAutre() {
        return nationaliteAutre;
    }

    public void setNationaliteAutre(String nationaliteAutre) {
        this.nationaliteAutre = nationaliteAutre;
    }

    @Basic
    @Column(name = "nom",  length = 50)
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Basic
    @Column(name = "numero", length = 50)
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Basic
    @Column(name = "postnom",  length = 50)
    public String getPostnom() {
        return postnom;
    }

    public void setPostnom(String postnom) {
        this.postnom = postnom;
    }

    @Basic
    @Column(name = "prenom", length = 50)
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Basic
    @Column(name = "profession",  length = 50)
    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    @Basic
    @Column(name = "profession_autre",  length = 50)
    public String getProfessionAutre() {
        return professionAutre;
    }

    public void setProfessionAutre(String professionAutre) {
        this.professionAutre = professionAutre;
    }

    @Basic
    @Column(name = "qualite", length = 50)
    public String getQualite() {
        return qualite;
    }

    public void setQualite(String qualite) {
        this.qualite = qualite;
    }

    @Basic
    @Column(name = "qualite_autre", length = 50)
    public String getQualiteAutre() {
        return qualiteAutre;
    }

    public void setQualiteAutre(String qualiteAutre) {
        this.qualiteAutre = qualiteAutre;
    }

    @Basic
    @Column(name = "quartier",  length = 50)
    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    @Basic
    @Column(name = "residence_autre",  length = 50)
    public String getResidenceAutre() {
        return residenceAutre;
    }

    public void setResidenceAutre(String residenceAutre) {
        this.residenceAutre = residenceAutre;
    }

    @Basic
    @Column(name = "tel",  length = 50)
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Basic
    @Column(name = "type_personne",  length = 50)
    public String getTypePersonne() {
        return typePersonne;
    }
    public void setTypePersonne(String typepersonne) {
        this.typePersonne = typepersonne;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Personne personne = (Personne) o;
        return getIdPersonne() == personne.getIdPersonne() &&
                Objects.equals(getAvenue(), personne.getAvenue()) &&
                Objects.equals(getDateNaissance(), personne.getDateNaissance()) &&
                Objects.equals(getEmail(), personne.getEmail()) &&
                Objects.equals(getGenre(), personne.getGenre()) &&
                Objects.equals(getLocalisationNaissanceId(), personne.getLocalisationNaissanceId()) &&
                Objects.equals(getLocalisationResidenceId(), personne.getLocalisationResidenceId()) &&
                Objects.equals(getNaissanceAutre(), personne.getNaissanceAutre()) &&
                Objects.equals(getNationalite(), personne.getNationalite()) &&
                Objects.equals(getNationaliteAutre(), personne.getNationaliteAutre()) &&
                Objects.equals(getNom(), personne.getNom()) &&
                Objects.equals(getNumero(), personne.getNumero()) &&
                Objects.equals(getPostnom(), personne.getPostnom()) &&
                Objects.equals(getPrenom(), personne.getPrenom()) &&
                Objects.equals(getProfession(), personne.getProfession()) &&
                Objects.equals(getProfessionAutre(), personne.getProfessionAutre()) &&
                Objects.equals(getQualite(), personne.getQualite()) &&
                Objects.equals(getQualiteAutre(), personne.getQualiteAutre()) &&
                Objects.equals(getQuartier(), personne.getQuartier()) &&
                Objects.equals(getResidenceAutre(), personne.getResidenceAutre()) &&
                Objects.equals(getTel(), personne.getTel()) &&
                Objects.equals(getTypePersonne(), personne.getTypePersonne()) &&
                Objects.equals(getLocalisationByLocalisationNaissanceId(), personne.getLocalisationByLocalisationNaissanceId()) &&
                Objects.equals(getLocalisationByLocalisationResidenceId(), personne.getLocalisationByLocalisationResidenceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdPersonne(), getAvenue(), getDateNaissance(), getEmail(), getGenre(), getLocalisationNaissanceId(), getLocalisationResidenceId(), getNaissanceAutre(), getNationalite(), getNationaliteAutre(), getNom(), getNumero(), getPostnom(), getPrenom(), getProfession(), getProfessionAutre(), getQualite(), getQualiteAutre(), getQuartier(), getResidenceAutre(), getTel(), getTypePersonne(), getLocalisationByLocalisationNaissanceId(), getLocalisationByLocalisationResidenceId());
    }

    @ManyToOne
    @JoinColumn(name = "dossier_id", referencedColumnName = "id", nullable = false)
    public Dossier getDossierByDossierId() {
        return dossierByDossierId;
    }

    public void setDossierByDossierId(Dossier dossierByDossierId) {
        this.dossierByDossierId = dossierByDossierId;
    }


    @ManyToOne
    @JoinColumn(name = "localisation_naissance_id", referencedColumnName = "id",insertable = false, updatable=false)
    public Localisation getLocalisationByLocalisationNaissanceId() {
        return localisationByLocalisationNaissanceId;
    }

    public void setLocalisationByLocalisationNaissanceId(Localisation localisationByLocalisationNaissanceId) {
        this.localisationByLocalisationNaissanceId = localisationByLocalisationNaissanceId;
    }

    @ManyToOne
    @JoinColumn(name = "localisation_residence_id", referencedColumnName = "id", insertable = false, updatable=false)
    public Localisation getLocalisationByLocalisationResidenceId() {
        return localisationByLocalisationResidenceId;
    }

    public void setLocalisationByLocalisationResidenceId(Localisation localisationByLocalisationResidenceId) {
        this.localisationByLocalisationResidenceId = localisationByLocalisationResidenceId;
    }


}
