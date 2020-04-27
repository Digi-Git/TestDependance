package com.dgtd.evelin.config;

import com.dgtd.common.error.ErrorService;
import com.dgtd.rdc.entity.Dossier;
import com.dgtd.ecole.ws.service.DBentities.EnfantService;
import com.dgtd.ecole.ws.service.DBentities.PersonneService;
import com.dgtd.evelin_common.config.EvelinProperties;
import com.dgtd.evelin.ordonnance.EvelinOrdonnanceBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

public class InitOrdonnanceTask {


    @Autowired
    private EnfantService enfantService;

    @Autowired
    private PersonneService personneService;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private EvelinProperties evelinProperties;

    @Autowired
    private Dossier dossier;

    private final Logger log = LoggerFactory.getLogger(InitOrdonnanceTask.class);

    @EventListener
    public void OnApplicationEvent (ContextRefreshedEvent event) {
      //  GenOrdonnance();
    }

    private void GenOrdonnance () {

/*
        try {
            EvelinOrdonnanceBridge ev = new EvelinOrdonnanceBridge(evelinProperties,errorService, enfantService,);
            ev.generateEcw();
        } catch (Exception ex) {
            log.error("erreur : impossiblité de générer les ECW d'evelin : " + ex.getLocalizedMessage());
        }*/
    }


}
