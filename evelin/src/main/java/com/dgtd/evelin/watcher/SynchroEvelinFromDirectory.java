package com.dgtd.evelin.watcher;

import com.dgtd.common.exception.DataException;
import com.dgtd.evelin_common.config.EvelinProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Configuration
@EnableScheduling
public class SynchroEvelinFromDirectory {

    private final Logger log = LoggerFactory.getLogger(SynchroEvelinFromDirectory.class);

    @Autowired
    EvelinProperties evelinProperties;

    @Autowired
    EvelinFromDirectoryService evelinFromDirectoryService;

    @Scheduled(fixedDelayString="${digitech.evelin.frequenceFromDir}")
    public void synchroniseCw () {

        try {

            // Uniquement si la synchro est activée dans la configuration
            if (evelinProperties.getEnableSynchroEvelinFromDir() == 1) {

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
                log.debug ( "-- Debut scan folder : " + evelinProperties.getEvelinSynchroDirectory() + " : " + timeStamp );


                 evelinFromDirectoryService.EvelinTransformer();


                timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
                log.debug ( "-- Fin scan folder : {}" , timeStamp );
            }
        }
        catch (Exception ex) {
            String error = "Erreur de synchronisation scan folder : " + ex.getLocalizedMessage();
            log.error (String.format("-- %s", error));
            throw new DataException(error);
        }

    }

}
