package com.dgtd.ecole.ws.service.Generic;

import com.dgtd.common.exception.DataException;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.payload.ResponseType;
import com.dgtd.ecole.ws.domain.json.Document;
import com.dgtd.ecole.ws.domain.json.EcoleJson;
import com.dgtd.ecole.ws.payload.EcoleResponseContent;
import com.dgtd.ecole.ws.service.DBentities.DossierService;
import com.dgtd.ecole.ws.service.DBentities.EcoleService;
import com.dgtd.rdc.entity.Dossier;
import com.dgtd.rdc.entity.Ecole;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Classe de gestion globale de transfert des données Json vers
 * la base de données : désérialisation, transformation des données JSON en modèles de la BDD
 *
 * @see DossierService classe qui pilote l'ensemble des services d'enregistrement en BDD
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TransformDataService {

    private final Logger log = LoggerFactory.getLogger(TransformDataService.class);

    private EcoleService ecoleService;
    private DossierService dossierService;


    public TransformDataService(EcoleService ecoleService, DossierService dossierService
                               ) {
        this.ecoleService = ecoleService;
        this.dossierService = dossierService;

    }

    /**
     * Fonction maitresse de la gestion de la transformation des données du fichier
     * datas.json vers la BDD
     *
     * @param resource Resource
     * @return réponse qui reprend le statut de traitement de chaque dossier
     * @see EcoleResponseContent
     */
    public EcoleResponseContent handleDataToDB(Resource resource) {
        EcoleResponseContent ecoleResponseContent = new EcoleResponseContent();
        boolean deleted;
        //Gestion de l'école
        //Sauvegarde ou Mise à jour si l'identifiant de l'école existe déjà
        EcoleJson ecoleJson = deserialize(resource);


        //Suppression du fichier décrypté


        Ecole ecole = ecoleService.transformJSONtoJAVA(ecoleJson);
        ecoleService.save(ecole);
        int ecoleId = ecoleJson.getId();
        ecoleResponseContent.setId(ecoleId);

        //Gestion des dossiers
        //Extraction des objets Json représentants les personnes et enfants
        try {
            //Sauvegarde ou mise à jour des dossiers si id du dossier est présent en BDD

            List<Document> docs = ecoleJson.getDocumentList();
            for (Document doc : docs) {
                deleted = doc.isDeleted();

                String docNo = doc.getDossierNumber();
                //Instanciation d'un booleen pour sortir de la fonction en cas ou le dossier est présent et qu'il a
                // déjà été envoyé dans Evelin

                if (docNo != null) {

                   List<Dossier> dossiers = null;
                    try{
                        dossiers = dossierService.findAllByNoDossierContaining("-"+docNo, docNo);
                    }catch(Exception e){
                        log.error("Exception lors de l'interrogation en bdd du dossier " + docNo + " " + e.getLocalizedMessage());
                    }


                    //Création du dossier et des entités associées si "no_dossier" n'est pas présent en BDD
                    if (dossiers != null && dossiers.isEmpty() ) {
                        if (deleted) {
                            ecoleResponseContent.addToDossiers(docNo, ResponseType.ENREGISTRE);
                            log.info("Le dossier {} est demandé en suppression mais n'est pas enregistré en BDD", docNo);
                        } else {
                            Dossier newDossier = dossierService.createCascade(ecoleJson, doc);
                            ecoleResponseContent.addToDossiers(newDossier.getNoDossier(), ResponseType.ENREGISTRE);
                            log.info("Demande effectuée avec succès - Enregistrement du dossier formulaire " + docNo + " doc bdd " + newDossier.getId() + " ecole id " + ecoleId);
                        }


                        // Si dossier déjà existant
                        //1 - Vérification si ecole id + no_dossier existe si non vérification du 2
                        //2 - Vérification du boolean isDeleted dans le json déclaré à true (demande de suppression)
                        //          ---> si non CREATION
                        //3 - Si ecole id + no Dossier existant : Vérification si info envoyée vers Evelin si déjà envoyé sortie
                        //4 - Cas ou dossier id + ecole id existe
                        //5 - Vérification si envoi dans Evelin
                        //6 - Si pas envoyé modification en fonction du flag deleted
                        //7 - Si déjà envoyé refus de la demande

                    } else if(dossiers !=null){

                        //1 - Vérification si ecole id + no_dossier existe déjà en BDD
                        boolean sameIdEcole = false;
                        for (Dossier dossierBd : dossiers) {

                            int ecoleIdFromDocBD = dossierBd.getEcoleId();
                            if (ecoleIdFromDocBD == ecoleId) {
                                sameIdEcole = true;


                            }
                        }
                        //2 - Si les écoles ne sont pas les mêmes le dossier peut être enregistré (vérification que le
                        //flag isDeleted est bien à false
                        if (!sameIdEcole && !deleted) {
                            Dossier newDossier = dossierService.createCascade(ecoleJson, doc);
                            String noDossier = newDossier.getId() + "-" + doc.getDossierNumber();
                            dossierService.updateNoDossier(newDossier, noDossier);
                            ecoleResponseContent.addToDossiers(newDossier.getNoDossier(), ResponseType.ENREGISTRE);
                            log.info("Demande effectuée avec succès - Enregistrement avec école différente et numéro dossier formulaire déjà présent en bdd - noDossier " + noDossier);

                        }

                        //4 - Si id Ecole + dossier id existe en BDD
                        if(sameIdEcole){
                            // 5 -  Vérification si dossier est expédié via Evelin pour accepter d'éventuelles modification/suppression
                           /* isExport = enfantService.checkEnfants(withSameIdEcole);

                            //Si le flag deleted est à true et que l'enfant n'a pas été envoyé dans Evelin
                            //alors on supprime
                            if (deleted && !isExport) {
                                dossierService.deleteCascade(withSameIdEcole);
                                responseContent.addToDossiers("dossier no-" + docNo + " ecoleId-" + ecoleId, ResponseType.ENREGISTRE);
                                log.info("Demande effectuée avec succès - Suppression du dossier no-" + docNo + " ecoleId-" + ecoleId );

                                //Si le flag du Dossier BO n'est pas a deleted true et que le dossier n'a pas été exporté dans Evelin
                                //alors on peut supprimer le dossier et le créer avec les nouvelles informations
                            } else if (!deleted && !isExport) {

                                dossierService.deleteCascade(withSameIdEcole);
                                Dossier newDossier = dossierService.createCascade(ecoleJson, doc);
                                String noDossier = newDossier.getIdPersonne() + "-" + doc.getDossierNumber();
                                dossierService.updateNoDossier(newDossier, noDossier);
                                responseContent.addToDossiers(newDossier.getNoDossier(), ResponseType.ENREGISTRE);
                                log.info("Demande effectuée avec succès - Modification du dossier avec école différente et numéro dossier formulaire déjà présent en bdd: "+ noDossier);

                                //7 - Refus de la demande : le dossier + ecole id est enregistré et envoyé dans Evelin et ne peut donc être accepté à la modification
                            } else {

                            */


                                ecoleResponseContent.addToDossiers(docNo, ResponseType.REFUSE);
                                log.error("Demande refusée : Enregistrement déjà présent en bdd et exporté vers Evelin avec dossier no " + docNo + " - id ecole " + ecoleId );
                          //  }
                        }

                    }


                } else {
                    ecoleResponseContent.addToDossiers("Pas de numéro de dossier", ResponseType.REFUSE);
                    log.error("No de dossier inexistant pour l'école id {}", ecoleId);
                }
            }
        }catch (Exception ex) {
            ex.getLocalizedMessage();
            log.error("Problème d'enregistrement de l'école id " + ecoleJson.getId() + " " + ecoleJson.getNom() + " " + ex.getLocalizedMessage());
            throw new WsBackOfficeException("Données incorrectes pour l'école id " + ecoleJson.getId() + " " + ex.getLocalizedMessage());
        }
        return ecoleResponseContent;
    }


    /**
     * Désérialisation du fichier en un objet principal EcoleJson
     * @param resource du fichier à désérialiser
     * @return EcoleJson (container global)
     */
    private EcoleJson deserialize (Resource resource){
        // Préparation à la deserialisation
        Gson gson = new Gson();
        EcoleJson ecoleJson;
        JsonReader reader = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReaader =null;


        // Lecture du fichier JSON (gson)
        try {
            inputStream = resource.getInputStream();
            inputStreamReaader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            reader = new JsonReader(inputStreamReaader);
            ecoleJson = gson.fromJson(reader,EcoleJson.class);
        }
        catch (Exception e) {
            throw new WsBackOfficeException(e.getLocalizedMessage());
        }
        finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                   log.error(e.getMessage());
                }
            }
            if(inputStreamReaader!=null){
                try {
                    inputStreamReaader.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
          
            
        }
        if(ecoleJson==null){
            throw new DataException("Fichier json vide" );
        }
         return ecoleJson;

    }




}
