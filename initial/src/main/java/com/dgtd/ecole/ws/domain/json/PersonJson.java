package com.dgtd.ecole.ws.domain.json;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public interface PersonJson extends Serializable {

    String getPostnom();

    void setPostnom(String postnom);

    String getNom();

    void setNom(String nom);

    String getPrenom();

    void setPrenom(String prenom);

    int getIdLocalisationLieuNaissance();

    void setIdLocalisationLieuNaissance(int idLocalisationLieuNaissance);

    int getIdLocalisationCommune();

    void setIdLocalisationCommune(int idLocalisationCommune);

    String getAutreLocalisationLieuNaissance();

    void setAutreLocalisationLieuNaissance(String autreLocalisationLieuNaissance);

    String getAutreLocalisation();

    void setAutreLocalisation(String autreLocalisation);

    String getCommune();

    void setCommune(String commune);

    String getDateNaissance();

    void setDateNaissance(String dateNaissance);

    int getIdProfession();

    void setIdProfession(int idProfession);

    int getIdNationalite();

    void setIdNationalite(int idNationalite);

    String getProfession();

    void setProfession(String profession);

    String getAutreProfession();
    void setAutreProfession(String autreProfession);

    String getNationalite();

    void setNationalite(String nationalite);

    String getNationaliteAutre();
    void setNationaliteAutre(String nationaliteAutre);

    String getAdresseAvenue();

    void setAdresseAvenue(String adresseAvenue);

    String getAdresseNumero();

    void setAdresseNumero(String adresseNumero);

    String getAdresseQuartier();

    void setAdresseQuartier(String adresseQuartier);


}
