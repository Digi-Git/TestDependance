package com.dgtd.ecole.ws.config;


import com.dgtd.ecole.ws.service.DBentities.EcoleService;
import com.dgtd.ecole.ws.service.DBentities.EducationnelleService;
import com.dgtd.rdc.localisation.service.LocalisationService;
import com.dgtd.security.domain.service.ProfilService;
import com.dgtd.security.domain.service.UtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InitBaseDatabase {
    private final Logger log = LoggerFactory.getLogger(InitBaseDatabase.class);

    @Autowired
    private ProfilService profilService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private LocalisationService localisationService;

    @Autowired
    private EducationnelleService educationnelleService;

    @Autowired
    private EcoleService ecoleService;

    @EventListener
    public void OnApplicationEvent (ContextRefreshedEvent event) {

        log.debug("-- Post initialisation Spring - début");

        try {
            profilService.loadBaseData();
        }
        catch (Exception ex) {
            log.error("erreur : impossiblité de créer les profils utilisateur : " + ex.getLocalizedMessage());
        }

        try {
            utilisateurService.loadBaseData();
        }
        catch (Exception ex) {
            log.error("erreur : impossiblité de créer les utilisateurs par defaut : " + ex.getLocalizedMessage());
        }

        try {
            localisationService.loadBaseData();
        }
        catch (Exception ex) {
            log.error("erreur : impossiblité de créer / charger les localisations : " + ex.getLocalizedMessage());
        }

        try{
            educationnelleService.loadBaseData();
        }catch (Exception ex){
            log.error("erreur lors du chargement des sous divisions " + ex.getLocalizedMessage());
        }

        try {
            utilisateurService.appendUser();
        }
        catch (Exception ex) {
            log.error("erreur : impossiblité d'ajouter les utilisateurs complémentaires : " + ex.getLocalizedMessage());
        }
/*
        try {
            ecoleService.generateBaseEcole();
        }
        catch (Exception ex) {
            log.error("erreur : impossiblité d'ajouter les écoles : " + ex.getLocalizedMessage());
        }
*/


/*

*/

        log.debug("-- Post initialisation Spring - fin");
    }


}
