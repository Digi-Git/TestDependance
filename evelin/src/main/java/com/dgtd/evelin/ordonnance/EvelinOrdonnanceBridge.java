package com.dgtd.evelin.ordonnance;

import com.dgtd.common.error.ErrorObject;
import com.dgtd.common.error.ErrorService;
import com.dgtd.common.error.TypeError;
import com.dgtd.common.error.TypeIdError;
import com.dgtd.common.exception.DataException;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.type.TypeActe;
import com.dgtd.ecole.ws.service.DBentities.DossierService;
import com.dgtd.ecole.ws.service.DBentities.EnfantService;
import com.dgtd.evelin_common.Certificate;
import com.dgtd.evelin_common.Certificates;
import com.dgtd.evelin_common.CertificatesDAO;
import com.dgtd.evelin_common.config.EvelinProperties;
import com.dgtd.rdc.entity.Dossier;
import com.dgtd.rdc.entity.Enfant;
import com.dgtd.rdc.localisation.service.LocalisationService;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class EvelinOrdonnanceBridge extends Certificates<Dossier> implements CertificatesDAO<Dossier> {

    private TypeActe typeActe;
    private int acteGeneres;
    private final DossierService dossierService;
    private final EnfantService enfantService;
    private final LocalisationService localisationService;
    private final int nbMaxEnfantsParCertificat;

    private final Logger log = LoggerFactory.getLogger(EvelinOrdonnanceBridge.class);


    public EvelinOrdonnanceBridge(

            EvelinProperties evelinProperties,
            ErrorService errorService,
            DossierService dossierService,
            EnfantService enfantService,
            LocalisationService localisationService

           ) throws Exception {

        super(evelinProperties,errorService);
        this.typeActe = TypeActe.ORDONNANCE;

        this.dossierService = dossierService;
        this.enfantService = enfantService;
        this.localisationService = localisationService;
        nbMaxEnfantsParCertificat = 5;
    }


    @Override
    public List<Dossier> getData() {

        return dossierService.getDataToDeclare();
    }


    @Override
    @Transactional
    public String run(List dossiers) throws DataException, WsBackOfficeException, ZipException, IOException {
        List<Dossier> dossiersList = dossiers;
        List<String> ordonnance = null;
        List<String> certificates = new LinkedList<>();
        miskTargetFolders();
        int acteGeneres=0;
        for(Dossier dossier : dossiersList){
            try{

                List<Enfant> enfants = enfantService.getAllByDossierByDossierId(dossier);
                if(enfants.size()>nbMaxEnfantsParCertificat){
                    List<Enfant> enfantsListPart = new ArrayList<>();
                    int diff = enfants.size()-nbMaxEnfantsParCertificat;
                    for(int i=0; i<diff; i++){
                        enfantsListPart.add(enfants.get(i));
                    }
                    Certificate otherCertificate = new Ordonnance(dossierService,enfantService,localisationService,
                            dossier, enfantsListPart,evelinProperties,errorService);
                    ordonnance = otherCertificate.generateECW();

                    for(int i=0;i<diff; i++){
                        enfants.remove(i);
                    }
                    certificates.addAll(ordonnance);

                }

                Certificate certificate = new Ordonnance(dossierService,enfantService,localisationService,
                        dossier, enfants,evelinProperties,errorService);

                ordonnance = certificate.generateECW();
                if (ordonnance == null) {
                    log.error("Annulation de l'enregistrement de l'ECW correspondant à l'enfant id bdd " + dossier.getId());
                    continue;
                }
                //Concaténation des certificats
                certificates.addAll(ordonnance);
                acteGeneres++;
            }catch (DataException | WsBackOfficeException e){
                log.error(e.getLocalizedMessage());
                errorService.insertError(TypeIdError.BDD.getTypeIdError()+ dossier.getId() ,
                        new ErrorObject(TypeError.TRANSFERT_EVELIN, e.getLocalizedMessage()),false);
            }catch (Exception e){
                log.error(e.getLocalizedMessage());
                errorService.insertError(TypeIdError.BDD.getTypeIdError()+ dossier.getId() ,
                        new ErrorObject(TypeError.TRANSFERT_EVELIN,e.getLocalizedMessage()),false);
            }

        }
        //Si aucun dossier n'a fonctionné retour null
        if(ordonnance == null){
            return null ;
        }
        //3 - Finalisation du fichier complet xml avec l'ensemble des certificats
        List<String> globalXML = createGlobalXML(certificates);

        log.debug("Nombre d'actes générés " + acteGeneres + " sur / " + dossiers.size());

        String targetFile= null;
        try{
            targetFile = createTemporaryFile(globalXML);
        }catch (Exception e){
            String error = "Problème de configuration dédié à l'export de l'ECW " + e.getLocalizedMessage();
            //TODO errorService + vérifier en cas de vide
            log.error(error);
            throw new WsBackOfficeException(error);
        }

        if(targetFile!= null){
            createZipAndClean(targetFile);
        }


        return targetFile;


    }




    public int getActeGeneres() {
        return acteGeneres;
    }

    public void setActeGeneres(int acteGeneres) {
        this.acteGeneres = acteGeneres;
    }

    /**
     * Fonction générique pour log message d'erreur et exception si besoin
     * @param message Message à log
     * @param exMessage Message de l'exception à log (ou vide)
     * @param thrEx Si true, génére également une exception
     */
    private void pushErreur (String message, String exMessage, boolean thrEx ) throws WsBackOfficeException {

        String mess = message;
        if (!"".contentEquals(exMessage.trim()) )
            mess = mess + " ex -> " + exMessage;

        log.error(mess);
        if (thrEx) {
            throw new WsBackOfficeException(mess);
        }
    }



    @Override
    public TypeActe getTypeActe() {
        return this.typeActe;
    }



}
