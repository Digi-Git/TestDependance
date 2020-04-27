package com.dgtd.evelin.naissance;

import com.dgtd.common.error.ErrorObject;
import com.dgtd.common.error.ErrorService;
import com.dgtd.common.error.TypeError;
import com.dgtd.common.exception.DataException;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.type.TypeActe;
import com.dgtd.ecole.ws.service.DBentities.DossierService;
import com.dgtd.ecole.ws.service.DBentities.EnfantService;
import com.dgtd.evelin_common.Certificate;
import com.dgtd.evelin_common.Certificates;
import com.dgtd.evelin_common.CertificatesDAO;
import com.dgtd.evelin_common.config.EvelinProperties;
import com.dgtd.rdc.entity.Enfant;
import com.dgtd.rdc.localisation.service.LocalisationService;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe pour la génération des données ECW d'aprés la BDD.
 *
 */

public class EvelinNaissanceBridge extends Certificates<Enfant> implements CertificatesDAO<Enfant> {

    private final Logger log = LoggerFactory.getLogger(EvelinNaissanceBridge.class);

    private TypeActe typeActe;
    private EnfantService enfantService;
    private DossierService dossierService;
    private LocalisationService localisationService;


    public EvelinNaissanceBridge(
            EvelinProperties evelinProperties, ErrorService errorService, EnfantService enfantService,
            DossierService dossierService, LocalisationService localisationService) throws Exception {
        super(evelinProperties,errorService);
        this.typeActe = TypeActe.NAISSANCE;
        this.enfantService = enfantService;
        this.dossierService = dossierService;
        this.localisationService = localisationService;
        this.currentNbActes = 0;

    }

    public List<Enfant> getData(){
       return enfantService.getDataToDeclare();
    }


    @Override
    @Transactional
    public String run(List enfants) throws ZipException, IOException {

        List<Enfant> enfantList = (List<Enfant>) enfants;
        currentNbActes = enfantList.size();
        List<String> acteNaissance = null;
        List<String> certificates = new LinkedList<>();

        //1 - Création des répertoires
        miskTargetFolders();


        //2 - Boucle sur l'ensemble des enfants et construction de la concaténation de
        //plusieurs certificats possible
        int acteGeneres = 0;

        for (Enfant enfant : enfantList){



          try {

                List<Enfant> enfants1 = new ArrayList<>();
                enfants1.add(enfant);
                Certificate certificate = new StandardActeNaissance(enfant.getDossierByDossierId(),enfants1,  enfantService,
                       dossierService,localisationService,evelinProperties,errorService);
                acteNaissance = certificate.generateECW();

                if (acteNaissance == null) {
                    log.error("Annulation de l'enregistrement de l'ECW correspondant à l'enfant id bdd {}",enfant.getIdPersonne());
                    continue;
                }
                //Concaténation des certificats
                certificates.addAll(acteNaissance);
                acteGeneres++;

            }catch (  DataException | WsBackOfficeException e){
                errorService.insertError(new ErrorObject(TypeError.TRANSFERT_EVELIN, e.getLocalizedMessage()),false);
            }catch (Exception e){
                errorService.insertError(new ErrorObject(TypeError.TRANSFERT_EVELIN,e.getLocalizedMessage()),false);
            }
        }

        if(acteNaissance == null){
            return null ;
        }

        //3 - Finalisation du fichier complet xml avec l'ensemble des certificats
        List<String> globalXML = createGlobalXML(certificates);

        log.debug("Nombre d'actes générés {} sur / {}",acteGeneres, currentNbActes);

        if(globalXML== null){
            log.debug("**** Liste vide retournée ");
        }



        String targetFile= null;
        try{
            targetFile = createTemporaryFile(globalXML);
        }catch (Exception e){
            String error = "Problème de configuration dédié à l'export de l'ECW " + e.getLocalizedMessage();
             log.error(error);
            throw new WsBackOfficeException(error);
        }

        if(targetFile!= null){
            createZipAndClean(targetFile);
        }


        return targetFile;
    }

    @Override
    public TypeActe getTypeActe() {
        return this.typeActe;
    }








}
