package com.dgtd.evelin_common;

import com.dgtd.common.type.TypeActe;
import net.lingala.zip4j.exception.ZipException;

import java.io.IOException;
import java.util.List;

public interface CertificatesDAO<T> {

    TypeActe getTypeActe();




    List<T> getData();
    /**
     * Fonction pilote qui gére les certificats à créer et englobe l'ensemble des sous fonctionnalités
     * generateEcw / createGlobalXML
     * Permet de gérer les exceptions sur un certificat sans entraver la gestion des autres certificats
     * @param containers objets qui contiennent l'ensemble des infos (BO ecole : model enfant, BO rdc1 : model acte)
     * @return
     */
    String run(List<T> containers) throws ZipException, IOException;

    /**
     * Création des répertoire de stockage dédié aux ECW
     * Utilisation des propriétés paramétrables en configuration YML
     * Possibilité de stocker l'ECW dans un dossier avec le paramètre targetFolder si dans
     * la configuration YML folderMode est à 1
     * sinon le stockage est fait dans le path ecwExport
     */
    void miskTargetFolders();
    /**
     * Création du fichier XML final
     * @param certificates générée avec generateEcw
     * @return certificat global XWL prêt à être zipper pour créer un ECW
     */
    List<String> createGlobalXML(List<String> certificates);
}
