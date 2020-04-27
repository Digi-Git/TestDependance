package com.dgtd.rdc.config;

import com.dgtd.rdc.localisation.service.LocalisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InitLocalisationDatabase {

    private final Logger log = LoggerFactory.getLogger(InitLocalisationDatabase.class);

    private LocalisationService localisationService;
    //private EtablissementService etablissementService;

    @Autowired
    private InitLocalisationDatabase(LocalisationService localisationService
            //, EtablissementService etablissementService
                                     ){
        this.localisationService = localisationService;
      //  this.etablissementService = etablissementService;

    }


    @EventListener
    public void OnApplicationEvent(ContextRefreshedEvent event){
        log.debug("-- Post initialisation database pour les localisations - début");
        //Fonction lancée dans initBaseDatabase du module initial : laissé dans initial car doit être lancé avant educationnel qui
        //reste dans le module initial
      /*  try{
            localisationService.loadBaseData();
        }catch (Exception ex){
            log.error("***** Erreur :  impossiblité de créer / charger les localisations : " + ex.getLocalizedMessage());
        }

/*
        try{
            etablissementService.loadBaseData();
        }catch (Exception ex){
            log.error("***** Erreur :  impossiblité de créer / charger les etablissements : " + ex.getLocalizedMessage());
        }

 */
        log.debug("-- Post initialisation database pour les localisations - fin");
    }

}
