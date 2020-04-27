package com.dgtd.security.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("digitech.user")
public class UtilisateurConfig {

    // Id de profil convenu dans la base de donnée pour les administrateurs
    public static final int PROFIL_ADMINISTRATEUR = 1;
    public static final int PROFIL_SUPERVISEUR = 3;

    private String defautAdmin;
    private String defautPassword;

    public String getDefautAdmin() {
        return defautAdmin;
    }

    public void setDefautAdmin(String defautAdmin) {
        this.defautAdmin = defautAdmin;
    }

    public String getDefautPassword() {
        return defautPassword;
    }

    public void setDefautPassword(String defautPassword) {
        this.defautPassword = defautPassword;
    }
}
