package com.dgtd.security.config;


import com.dgtd.security.domain.service.ProfilService;
import com.dgtd.security.domain.service.UtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class  InitUserDatabase {

    private final Logger log = LoggerFactory.getLogger(InitUserDatabase.class);


    private ProfilService profilService;
    private UtilisateurService utilisateurService;

    @Autowired
    public InitUserDatabase(ProfilService profilService, UtilisateurService utilisateurService){
        this.profilService = profilService;
        this.utilisateurService = utilisateurService;
    }

    @EventListener
    public void OnApplicationEvent (ContextRefreshedEvent event) {
        log.debug("-- Post initialisation database pour les utilisateurs - début");

        try {
            profilService.loadBaseData();
        } catch (Exception ex) {
            log.error("erreur : impossiblité de créer les profils utilisateur : " + ex.getLocalizedMessage());
        }

        try {
            utilisateurService.loadBaseData();
        } catch (Exception ex) {
            log.error("erreur : impossiblité de créer les utilisateurs par defaut : " + ex.getLocalizedMessage());
        }

        try {
            utilisateurService.appendUser();
        } catch (Exception ex) {
            log.error("erreur : impossiblité d'ajouter les utilisateurs complémentaires : " + ex.getLocalizedMessage());
        }
        log.debug("-- Post initialisation database pour les utilisateurs - fin");
    }
}
