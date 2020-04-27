package com.dgtd.evelin.controller;


import com.dgtd.common.exception.DataException;
import com.dgtd.common.type.TypeActe;
import com.dgtd.evelin.watcher.SynchroEvelin;
import com.dgtd.evelin_common.config.EvelinProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class EvelinController {
    private static final Logger log = LoggerFactory.getLogger(EvelinController.class);

    private SynchroEvelin synchroEvelin;
    private EvelinProperties evelinProperties;


    public EvelinController(EvelinProperties evelinProperties, SynchroEvelin synchroEvelin) {
        this.evelinProperties = evelinProperties;
        this.synchroEvelin = synchroEvelin;
    }

    @GetMapping(value="/forceSyncCtw/{typeActe}")
    @Secured(value = {"Administrateur"})
    public ResponseEntity<?> forceSyncCtw (@RequestParam("typeActe") String typeActe) {

        log.debug("REST force synchronisation CityWeb/Evelin");
        try {
            TypeActe ta = TypeActe.get(typeActe);

            if(typeActe == null ){
                ta = evelinProperties.getTypeActe();
            }
            if(ta == null){
                throw  new DataException("Pouvez-vous préciser le type d'acte ?");
            }
            //TODO
        //    certificatDAOFactory.EvelinTransformer(ta);
            synchroEvelin.synchroniseECW();

        } catch (Exception e) {
            log.error("REST force synchronisation CityWeb/Evelin error : " + e.getLocalizedMessage());
            return ResponseEntity.status(500).body(e.getLocalizedMessage());
        }

        log.debug("REST force synchronisation CityWeb/Evelin : done");
        return ResponseEntity.ok().build();
    }

}
