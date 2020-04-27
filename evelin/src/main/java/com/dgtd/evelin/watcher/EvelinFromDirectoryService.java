package com.dgtd.evelin.watcher;

import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.payload.ResponseType;
import com.dgtd.ecole.ws.payload.EcoleResponseContent;
import com.dgtd.ecole.ws.service.Generic.FileStorageService;
import com.dgtd.ecole.ws.service.Generic.TransformDataService;
import com.dgtd.evelin_common.config.EvelinProperties;
import com.dgtd.security.service.FileService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class EvelinFromDirectoryService {

    private final Logger log = LoggerFactory.getLogger(EvelinFromDirectoryService.class);

    @Autowired
    private EvelinProperties evelinProperties;

    @Autowired
    private TransformDataService transformDataService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileService fileService;


    /**
     * Intégration des fichiers zip des superviseurs en batch.
     *
     * La fonction réalise les tâches suivante :
     * - Préparation des répertoires et nom du fichier de sortie.
     * - Préparation des templates.
     * - Requête de selection des enfants à traiter.
     * - Boucle d'appel par enfant.
     * - Ecriture du fichier XML
     * @throws WsBackOfficeException
     * @TODO La requete va chercher tous les enfants. Il faudrait implémenter le code pour la mise à jour et selectionner uniquement ceux qui n'ont pas encore été exporté.
     */
    public void EvelinTransformer () throws WsBackOfficeException {

        try {

            // Repertoire de scan
            Path pScan = Paths.get(evelinProperties.getEvelinSynchroDirectory());
            if (!Files.exists(pScan)) {
                log.error("Erreur de scan du repertoire : repertoire inexistant ou inaccessible " + evelinProperties.getEvelinSynchroDirectory() );
                return;
            }
            else if (!Files.isDirectory(pScan)) {
                log.error("Erreur de scan du repertoire : n'est pas un repertoire ou inaccessible " + evelinProperties.getEvelinSynchroDirectory() );
                return;
            }

            // Repertoire des KO
            Path pKo = Paths.get(pScan.toString(), "KO");
            try {
                FileUtils.forceMkdir(pKo.toFile());
            }
            catch (Exception ex) {
                log.error("Erreur de creation du répertoire KO " + evelinProperties.getEvelinSynchroDirectory() + " - err = " + ex.getLocalizedMessage());
                return;
            }


            // Scan de la liste des fichiers
            Collection<File> fics = null;
            try (Stream<Path> walk = Files.walk(pScan)) {

                fics = FileUtils.listFiles(pScan.toFile(), new String[] {"zip"}, false);

            } catch (Exception ex) {
                log.error("Erreur de scan du repertoire : " + ex.getLocalizedMessage());
                throw new WsBackOfficeException("Erreur de scan du repertoire : " + ex.getLocalizedMessage());
            }

            // Traitement de chaque fichier
            for (File fic: fics) {

                log.debug("traitement fichier : " + fic.getName());
                EcoleResponseContent ecoleResponseContent;

                try{
                    //Enregistrement du fichier dans le dossier prévu (Datas)
                    String folder = fileStorageService.createFolderNameFromZipFile(fic);
                    fileStorageService.storeZipFile( fic,folder);

                    //Gestion en BDD
                    try {
                        // Gestion du fichier sous forme de ressource
                        // Att : la detection du json à l'interieur est fait dans la fonction ci-dessous.
                        Resource res = fileStorageService.loadZipFileAsResource(fic);

                        //Décryptage de la ressource
                        fileService.decrypt(res.getFile());
                        ecoleResponseContent = transformDataService.handleDataToDB(res);
                        Map<String, ResponseType> dossiers = ecoleResponseContent.getDossiers();
                        for(Map.Entry<String, ResponseType> map: dossiers.entrySet()){
                            log.info("***** Traitement du Dossier n°" + map.getKey() + " Statut enregistrement " + map.getValue());
                        }

                        //Suppression du dossier à traiter
                        Files.delete(fic.toPath());
                    }
                    catch (Exception ex) {
                        log.error("***** Erreur traitement zip handleDataToDb " + fic + " - déplacement dans KO : " + ex.getLocalizedMessage());
                        moveToKo(pKo, fic);
                    }

                    log.debug("traitement fichier terminé : " + fic);

                } catch (Exception e){

                    log.error("***** Erreur lors de l'intégration du dossier " + fic + " - déplacement dans KO : " + e.getLocalizedMessage());
                    moveToKo(pKo, fic);
                }
            }

        } catch (Exception ex) {
            log.error("Erreur de scan du répertoire : " + ex.getLocalizedMessage());
            throw new WsBackOfficeException("Erreur de scan du répertoire : " + ex.getLocalizedMessage());
        }

    }

    /***
     * Déplacement du fichier traité vers s/répertoire KO
     * @param pKo Répertoire des KO
     * @param file Fichier zip source
     * @throws IOException
     */
    private void moveToKo (Path pKo, File file) {

        try {
            Path outp = Paths.get(pKo.toString(), file.getName());
            FileUtils.moveFile(file, outp.toFile());
        }
        catch (Exception ex) {

            log.error("***** Erreur traitement deplacement fichier vers répertoire KO " + file.getName() + " -> " + pKo + " err : " + ex.getLocalizedMessage());

        }
    }
}
