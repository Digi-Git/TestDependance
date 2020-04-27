package com.dgtd.common.error;


import com.dgtd.common.config.DigitechProperties;
import com.dgtd.common.excel.ExportToExcel;
import com.dgtd.common.excel.MappingModele;
import com.dgtd.common.exception.DataException;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.payload.DGTDResponseContent;
import com.dgtd.common.util.ErrorMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * Gestion des erreurs au niveau de l'application
 */
@Component
public class ErrorService {

    private final Logger log = LoggerFactory.getLogger(ErrorService.class);

    private boolean sendErrorToClient;
    private Map<String, DGTDError> map;
    private DGTDError currentError;
    private static ErrorService instance ;
    private ErrorMailService mailSender;
    private DigitechProperties digitechProperties;
    private ExportToExcel exportToExcel;


    /**
     * Initialisation à zéro de l'objet erreur en cours et des paramètres d'envoi de l'erreur
     * (Eviter d'envoyer une erreur à un client qui est liée à une tâche de fond (scheduler) faite en parallèle qui
     * n'est pas liée avec l'opération du client)
     */
    private ErrorService() {

        sendErrorToClient = false;
        map = new HashMap<>();
        currentError = new ErrorObject();
    }

    @Autowired
    private ErrorService(ErrorMailService errorMailService, DigitechProperties digitechProperties, ExportToExcel exportToExcel){
        this();
        this.mailSender = errorMailService;
        this.digitechProperties = digitechProperties;
        this.exportToExcel = exportToExcel;
    }

    public synchronized ErrorService getInstance(){
        if(instance== null){
            instance = new ErrorService();
        }
        return this.instance;
    }

    public boolean isSendErrorToClient() {
        return sendErrorToClient;
    }

    public void setSendErrorToClient(boolean sendErrorToClient) {
     //   this.currentError.setCurrent(sendErrorToClient);
        if(!sendErrorToClient){
            setCurrentError(new ErrorObject());
        }
        this.sendErrorToClient = sendErrorToClient;
    }


    public Map<String, DGTDError> getMap() {
        return map;
    }


    public DGTDError getCurrentError() {
        return currentError;
    }

    /**
     * Mise à jour de l'objet erreur en cours de traitement
     * @param DGTDError erreur en cours de traitement
     */
    public void setCurrentError(DGTDError DGTDError) {
        this.currentError = DGTDError;

    }


    /**
     * Introduire l'erreur dans la réponse fournie au client
     * @param responseContent format de réponse au client
     * @return boolean pour définir si la fonction est réalisée avec succès
     */
    public boolean setResponseContent(DGTDResponseContent responseContent) {

        if(sendErrorToClient){
            responseContent.setErrorObject(this.currentError);
        }
        return sendErrorToClient;
    }

    /**
     * Fonction d'insertion pour garder en mémoire les erreurs qui n'ont pas la clé id disponible dans la classe
     * Les services autres que ceux en direct avec la tablette (Bluekango, RapidPro & Evelin ) doivent être mis en sendErrorToClient false pour ne pas interférer
     * avec les réponses faites aux utilisateurs
     *
     * @param error qui implémente l'interface currentError
     */
    public void insertError(DGTDError error, boolean sendToClient){
        //Passe l'information que l'erreur est sur la requête en cours
        /*if(error.getTypeError().equals(TypeError.TRANSFERT_EVELIN) || error.getTypeError().equals(TypeError.BLUEKANGO)
            || error.getTypeError().equals(TypeError.RAPIDPRO) || error.getTypeError().equals(TypeError.SECURITE)){
            setSendErrorToClient(false);
        }else{
            setSendErrorToClient(true);
        }

         */
        if(sendToClient){
            setSendErrorToClient(true);

        }
        setCurrentError(error);
        String id = error.getId();
        map.put(id,error);
    }

    /**
     * Insertion de l'erreur avec un id interne au WS
     * @param id acte id
     * @param error contenu de l'erreur
     * @see DGTDError interface pour permettre la personnalisation des erreurs en fonction des WS
     * en limitant les modifications
     * @see ErrorObject implémentation de l'interface utilisée pour ce WS
     *
     */
    public void insertError(String id,DGTDError error, boolean sendToClient){
        insertError(error, sendToClient);
        setCurrentError(error);
        map.put(id,error);
    }


    public void insertErrors(String id, List<DGTDError> errors, boolean sendToClient){
        for(DGTDError error : errors){
            insertError(id, error,sendToClient );
        }
    }


    public boolean resetErrorList(){
        getMap().clear();
        if(getMap().isEmpty()){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ErrorService{" +
                "sendErrorToClient=" + sendErrorToClient +
                ", map=" + map +
                ", currentError=" + currentError +

                '}';
    }

    /**
     * Fonction générique pour log message d'erreur et push exception si besoin
     *
     * @param message Message à log
     * @param thrEx Si true, génére également une exception
     */
    public static void pushErreur (Logger log, String message, boolean thrEx ) throws WsBackOfficeException {

        pushErreurInternal (log, message, null, thrEx);

    }

    /**
     * Fonction générique pour log message d'erreur et push exception si besoin
     *
     * @param message Message à log
     * @param exMessage Message de l'exception à log (ou vide)
     * @param thrEx Si true, génére également une exception
     */
    public static void pushErreur (Logger log, String message, String exMessage, boolean thrEx ) throws WsBackOfficeException {

        pushErreurInternal (log, message, exMessage, thrEx);

    }


    /**
     * Fonction générique commune pour log message d'erreur et push exception si besoin
     *
     * @param message Message à log
     * @param exMessage Message de l'exception à log (ou vide)
     * @param thrEx Si true, génére également une exception
     */
    private static void pushErreurInternal (Logger log, String message, String exMessage, boolean thrEx ) throws WsBackOfficeException {

        String mess = message;
        if ( (!"".contentEquals(exMessage.trim()) ) || (exMessage == null) )
            mess = mess + " ex -> " + exMessage;

        log.error(mess);
        if (thrEx) {
            throw new WsBackOfficeException(mess);
        }
    }

    /**
     * Envoi d'une erreur par mail
     * @param error concernée
     */
    /*
    public void sendErrorByMail(DGTDError error){

        String subject="";
        String text="";
        try {

            subject = digitechProperties.getApplicationName() + error.getTypeError();
            text = error.getDetails();
            mailSender.sendMessage("celine.charpentier@digitech.fr", subject, text);

        } catch (Exception e) {
            String errorDetails = "Erreur lors de l'envoi du mail " + subject + " "+ text;
            log.error("***** "+ errorDetails);
            insertError(new ErrorObject(TypeError.DATA_INTEGRATION, errorDetails),false);
            throw new DataException(errorDetails);
        }
    }

     */

    /**
     * Envoi de la liste complète des erreurs depuis le déploiement del'application
     * Le détail est envoyée dans un fichier excel
     * L'adresse de destination est configurable dans les paramètres de l'application
     */
    @Scheduled(cron="${digitech.app.error.raiseTime}")
    public void sendErrorsByMail(){
        String subject = "";
        String text = "";
        try {

            subject = digitechProperties.getApplicationName() + ": Liste des erreurs répertoriées ";


            List<DGTDError> errors = new ArrayList<>();
            File file = null;
            if(map == null || map.size()==0){
                text =("Pas d'erreur");
            }else {
                for(Map.Entry<String,DGTDError> errorEntry : map.entrySet()){
                    errors.add(errorEntry.getValue());
                    log.debug("liste d'erreur n'est pas nulle");
                    text = "Ci-joint la liste des erreurs depuis le lancement de l'application " + digitechProperties.getApplicationName();
                    List<MappingModele> mappingModele = new ArrayList<>();
                    mappingModele.add(new MappingModele(new ClassPathResource("exportTemplate/export_error_mapping.txt").getFile(),"Erreurs","Erreurs",false));
                    file = exportToExcel.export(true,  errors, mappingModele, DGTDError.class);

                }
            }


            String[] mailTo = digitechProperties.getMailTo();
            for(String user : mailTo) {
                if(file != null){
                    mailSender.sendMessageWithAttachement(user,subject,text,file);
                }else{
                    mailSender.sendMessage(user,subject,text);
                }

            }

        } catch ( Exception e) {
            String errorDetails = "Erreur lors de l'envoi du mail " + subject + " "+ e.getLocalizedMessage();
            log.error("***** "+ errorDetails);
            insertError(new ErrorObject(TypeError.DATA_INTEGRATION, errorDetails),false);
            throw new DataException(errorDetails);
        }
    }
}
