package com.dgtd.evelin.watcher;

import com.dgtd.common.error.ErrorObject;
import com.dgtd.common.error.ErrorService;
import com.dgtd.common.error.TypeError;
import com.dgtd.common.error.TypeIdError;
import com.dgtd.common.type.TypeActe;
import com.dgtd.ecole.ws.service.DBentities.DossierService;
import com.dgtd.ecole.ws.service.DBentities.EnfantService;
import com.dgtd.evelin.naissance.EvelinNaissanceBridge;
import com.dgtd.evelin.ordonnance.EvelinOrdonnanceBridge;
import com.dgtd.evelin_common.Certificates;
import com.dgtd.evelin_common.config.EvelinProperties;
import com.dgtd.rdc.localisation.service.LocalisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Mise à jour batch database vers Evelin
 *
 * La fréquence de mise à jour est définie en MS dans les application.properties : digitech.app.frequence
 */
@Configuration
@EnableScheduling
public class SynchroEvelin {

    private final Logger log = LoggerFactory.getLogger(SynchroEvelin.class);

    private final EvelinProperties evelinProperties;
    private final ErrorService errorService;
    private final DossierService dossierService;
    private final EnfantService enfantService;
    private final LocalisationService localisationService;

    public static final TypeActe NAISSANCE = TypeActe.NAISSANCE;
    public static final TypeActe DECES = TypeActe.DECES;
    public static final TypeActe ORDONNANCE = TypeActe.ORDONNANCE;


    public SynchroEvelin(EvelinProperties evelinProperties, EnfantService enfantService,
                         DossierService dossierService,LocalisationService localisationService,
                         ErrorService errorService) {
        this.evelinProperties = evelinProperties;
        this.errorService = errorService;
        this.enfantService = enfantService;
        this.dossierService = dossierService;
        this.localisationService = localisationService;

    }

    @Scheduled(fixedDelayString="${digitech.evelin.frequence}")
    public void evelinScheduler () {

        try {


            // Uniquement si la synchro est activée dans la configuration
            if (evelinProperties.getEnableSynchroEvelin() == 1) {
                synchroniseECW();

            }
        }
        catch (Exception ex) {
            log.error ("-- Erreur de synchronisation CityWeb/Evelin : " + ex.getLocalizedMessage());
        }

    }



    public void synchroniseECW() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        log.debug ( "-- Debut de la synchronisation CityWeb/Evelin :" + timeStamp );

        TypeActe typeActeConf = evelinProperties.getTypeActe();
        if(typeActeConf == null){
            String error = "Veuillez revoir la configuration de la propriété digitech.evelin.typeActe avec l'un des attributs suivants "
                    +TypeActe.getAllValues();
            log.error(error);
            errorService.insertError(TypeIdError.CONFIGURATION + "Evelin TypeActe", new ErrorObject(TypeError.TRANSFERT_EVELIN, error), false);
            return;
        }

        Certificates certificates = getFactory(typeActeConf,evelinProperties,errorService,dossierService,enfantService,
                localisationService);
        List<?> list;
        if(certificates!= null){
            list =  certificates.getData();
        }
        else{
            log.debug("Pas de données disponible en BDD ");
            return;
        }

        String pathECW = null;

        if(list.size()>0){
            pathECW = certificates.run(list);
            //Si la liste est vide, elle est retournée, pas de synchronisation
            if(pathECW == null){
                log.debug("**** Liste vide retournée ");
                return;
            }
        }




        timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        log.debug ( "-- Fin de la synchronisation CityWeb/Evelin : " + timeStamp );

    }
    /**
     *
     * @param type TypeActe
     * @param evelinProperties propriétés générales
     * @param errorService service général de recensement des erreurs
     * @return CertificatDAO ou null si non TypeActe non valide
     */
    public static Certificates getFactory(TypeActe type, EvelinProperties evelinProperties, ErrorService errorService,
                                            DossierService dossierService, EnfantService enfantService,
                                          LocalisationService localisationService) throws Exception {

        Certificates<?> evb;
        switch(type) {

            case NAISSANCE:
                evb = new EvelinNaissanceBridge(evelinProperties, errorService,enfantService,dossierService,localisationService);
                break;

            case ORDONNANCE:
                evb = new EvelinOrdonnanceBridge(evelinProperties,errorService,dossierService,enfantService,localisationService);
                break;

            default:

                return null;
        }
        return evb;
    }
}
