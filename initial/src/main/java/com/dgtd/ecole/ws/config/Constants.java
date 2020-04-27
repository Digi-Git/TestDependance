package com.dgtd.ecole.ws.config;

public class Constants {
    // Regex for acceptable logins
    // Support les chars en maj/min, les chiffres chiffre et quelques caracteres spéciaux :
    // @ ! * . -
    public static final String LOGIN_REGEX = "^[_'.*!@A-Za-z0-9-]*$";

    // Id de profil convenu dans la base de donnée pour les administrateurs
    public static final int PROFIL_ADMINISTRATEUR = 1;
    public static final int PROFIL_SUPERVISEUR = 3;

    // Type d'entité (localisation)
    public static final short ENTITE_URBAINE = 0;                                   // Découpe territoriale, ville
    public static final short ENTITE_RURALE = 1;                                    // Découpe territoriale, territoire

    // Nom du fichier de données dans les zip reçus.
    public static final String NOM_DATA_JSON = "datas.json";

    // Messages d'erreur
    public static final String ERR_ARGUMENT_FORMDATA = "Argument absent ou invalide pour requête de type form data";
    public static final String ERR_CREATE_FOLDER = "Création d'un repertoire impossible sur le serveur";
    public static final String ERR_WRITE_FILE = "Erreur d'écriture du fichier sur le serveur";
    public static final String ERR_JSON_PARSE = "Erreur de parsing du fichier Json d'un centre";
    public static final String ERR_USER_EXIST = "Utilisateur déja existant (identifiant)";
    public static final String ERR_PROFIL_INEXIST = "Profil inexistant";



}
