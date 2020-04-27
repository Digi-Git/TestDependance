package com.dgtd.service_mail;


import com.dgtd.service_mail.config.CredentialProperties;
import com.dgtd.service_mail.entity.Credentials;
import com.dgtd.service_mail.entity.Installed;

import com.dgtd.service_mail.entity.Mail;
import com.dgtd.service_mail.service.MailService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;


import javax.mail.Session;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import java.time.Instant;
import java.util.*;


@Component
public class GmailModule {

    private static final Logger log = LoggerFactory.getLogger(GmailModule.class);
    //private static final String APPLICATION_NAME = "GestionMail";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    //private static final String TOKENS_DIRECTORY_PATH = "E:/RDC/Ecoles/tokenMail";
    //private static final String PATH_ATTACHMENT = "E:/RDC/Ecoles/Zip";
    //private static final int PORT = 8089;
    private static int COUNT = 1;
    private static int nbZipImported = 0;
    private static boolean firstInit = true;
    private static String dateImportTimeStamp = getTimeStamp();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
    //private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private MailService mailService;

    @Autowired
    private CredentialProperties credentialProperties;


    public GmailModule(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        //Load client secrets.
        Credentials credential = new Credentials(new Installed(credentialProperties.getClient_id(),credentialProperties.getProject_id(),
                credentialProperties.getAuth_uri(),credentialProperties.getToken_uri(),credentialProperties.getAuth_provider_x509_cert_url(),
                credentialProperties.getClient_secret(),credentialProperties.getRedirect_uris()));

        //convert to json
        Gson gson = new Gson();
        String json = gson.toJson(credential);

        InputStream in = new ByteArrayInputStream(json.getBytes());
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(credentialProperties.getTokens_directory_path())))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(credentialProperties.getPort()).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    //24h
    @Scheduled(fixedDelay = 86400000)
    public void run()  {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT;
        List<BadMessage> listOfBadMessage = new ArrayList();
        if(firstInit) {
            log.debug("First init");
            File f = new File(credentialProperties.getPath_attachment());
            if (!f.exists()){
                f.mkdirs();
                log.debug("MKDIR");
            }
            log.debug("Get last date import");
            String dateImport = mailService.getLastDateImport();
            log.debug("Date import : " + dateImport);
            if (dateImport != null) {
                dateImportTimeStamp = dateImport;
            }
            firstInit = false;
            log.debug("End first init");
        }
        try {

            nbZipImported = 0;
            log.debug("Start http transport");
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(credentialProperties.getApplication_name())
                    .build();
            String userId = "me";
            log.debug("Init query list");
            List<String> queryList = initQueryList(dateImportTimeStamp,null);
            String queryFilter = generateQueryFilterFromList(queryList);
            log.debug("Filter : " + queryFilter);
            List<Message> messagesId = listMessagesIdMatchingQuery(service,userId,queryFilter);
            log.debug("Nb message : " + messagesId.size());
            log.info("Start mail job on " + messagesId.size() + " messages");
            if(!messagesId.isEmpty()){
                List<Message> allMessage = getMessages(service,messagesId,userId);
                List<Message> messagesTemp;
                List<AttachmentCustom> attachmentCustomList;
                //log.info("all message : " + allMessage);
                int compteurMessages = 0;
                //on prend les 20 premier message en boucle
                while(compteurMessages != allMessage.size() ){
                    log.debug("Compteur : " + compteurMessages + "/ " + (allMessage.size()));
                    if(compteurMessages + 20 <= allMessage.size() ){
                        messagesTemp = allMessage.subList(compteurMessages,compteurMessages + 20);
                        log.debug("Message temp size " + messagesTemp.size());
                        compteurMessages = compteurMessages + 20;
                    }else{
                        messagesTemp = allMessage.subList(compteurMessages,allMessage.size());
                        log.debug("Message temp size " + messagesTemp.size());
                        compteurMessages = allMessage.size();
                    }

                    attachmentCustomList = getAttachments(service,messagesTemp,userId,listOfBadMessage);

                    if(!attachmentCustomList.isEmpty()){
                        log.debug("Write files zip");
                        writeFileAttachment(attachmentCustomList,credentialProperties.getPath_attachment());
                    }

                }
                dateImportTimeStamp = getTimeStamp();
                if(listOfBadMessage.size() > 0){
                    log.info("Send mail with zip error to " + credentialProperties.getMailToZipError());
                    sendMessage(service,userId,createEmail(credentialProperties.getMailToZipError(),credentialProperties.getMailFromZipError(),credentialProperties.getMailSubjectZipError(),formatStringMessage(listOfBadMessage)));
                }

            }
            log.info("End of mail job");
            log.info("Nb zip imported : " + nbZipImported);

        } catch (Exception e) {
            log.error("Exception general: " + e.getLocalizedMessage());
        }
    }

    /**
     * date retourne en seconde epoch UTC
     *
     */
    private static String getTimeStamp(){
        long now = Instant.now().getEpochSecond();
        return String.valueOf(now);
    }

    private List<String> initFromMail(List<String> mailAdress){
        StringBuilder stringBuilder = new StringBuilder();
        List<String> retList = new ArrayList<>();
        for(String s:mailAdress){
            retList.add(stringBuilder.append("from:").append(s).toString());
        }

        return retList;
    }

    private List<String> initQueryList(String timeStamp,List<String> mailList) {
        List<String > queryList = new ArrayList<>();
        //queryList.add("from:romaric.roussel@outlook.fr");
        if(mailList !=null && !mailList.isEmpty()){
            queryList.addAll(mailList);
        }
        queryList.add("has:attachment");
        queryList.add("filename:zip");
        queryList.add("after:" + timeStamp);
        return queryList;
    }

    public List<Message> listMessagesIdMatchingQuery(Gmail service, String userId,
                                                     String query) throws IOException {
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

        List<Message> messagesId = new ArrayList<>();
        while (response.getMessages() != null) {
            messagesId.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {

                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        return messagesId;
    }

    public String generateQueryFilterFromList(List<String> stringList){
        StringBuilder builder = new StringBuilder();
        for(String s:stringList){
            builder.append(s).append(" ");
        }

        return builder.toString();
    }



    public List<Message> getMessages(Gmail service,List<Message> messageList,String userId) throws IOException {
        List<Message> messages = new ArrayList<>();
        int count = 1;
        for (Message m:messageList){
            log.debug("Get message " + count + " / " + (messageList.size()));
            messages.add(service.users().messages().get(userId,m.getId()).execute());
            count++;
        }
        return messages;
    }

    public void writeFileAttachment(List<AttachmentCustom> list,String path) throws IOException
    {

        log.debug("Nb zip to write  : " + list.size());
        int count = 1;
        for(AttachmentCustom attachmentCustom:list){
            log.debug("Write zip " + count + " / " + list.size());
            File f = new File(path + File.separator + attachmentCustom.fileName);
            if (f.exists()){

                String fName = f.getName().substring(0,f.getName().indexOf("."));
                String fExtension = f.getName().substring(f.getName().indexOf("."));
                File copyF = new File(path + File.separator + fName + "(" + COUNT + ")" + fExtension);
                generateCount();
                while (copyF.exists()){
                    copyF = new File(path + File.separator + fName + "(" + COUNT + ")" + fExtension);
                    generateCount();
                }
                resetCount();
                //normalement unique
                // Initialize a pointer in file using OutputStream
                OutputStream os = new FileOutputStream(copyF);
                os.write(attachmentCustom.getFileByteArray());
                // Close the file
                os.close();

            }else{
                // Initialize a pointer in file using OutputStream
                OutputStream os = new FileOutputStream(f);
                os.write(attachmentCustom.getFileByteArray());
                // Close the file
                os.close();
            }
            nbZipImported = nbZipImported +1;
            count++;
            log.debug("Nb zip imported : " + nbZipImported);

        }

    }

    private void generateCount(){
        COUNT = COUNT + 1;
    }
    private void resetCount(){
        COUNT = 1;
    }

    public List<AttachmentCustom> getAttachments(Gmail service, List<Message> list, String userId,List<BadMessage> listOfBadMessage) throws IOException
    {
        List<AttachmentCustom> attachments = new ArrayList<>();
        List<MessagePart> parts;
        String mailFrom = null;
        String dateReceived = null;
        List<MessagePartHeader> myPayload = null;
        String attId = null;
        MessagePartBody attachPart = null;
        BadMessage badMessage = null;
        AttachmentCustom attachment = null;
        for(Message m:list){

            if(m.getPayload()!=null){
                myPayload = new ArrayList<>(m.getPayload().getHeaders());
                parts = m.getPayload().getParts();

                for(MessagePartHeader property:myPayload){
                    if(property.getName().equals("From")){
                        mailFrom = property.getValue();
                    }
                    if(property.getName().equals("Date")) {
                        dateReceived = property.getValue();
                    }
                }

                if (parts != null)
                {


                    for (MessagePart part : parts)
                    {


                        if (part.getFilename() != null && part.getFilename().length() > 0)
                        {


                            if(m.getPayload().getBody().size() > 0) {


                                attId = part.getBody().getAttachmentId();
                                badMessage = new BadMessage();
                                //log.info("mid :" + m.getId());
                                if(attId == null){
                                    log.info("Attid null pour le zip : " + part.getFilename() + ", envoyer par : " +
                                            mailFrom + ", le : " + dateReceived);
                                    attachPart = null;
                                    badMessage.setFileName(part.getFilename());
                                    badMessage.setDateReceived(dateReceived);
                                    badMessage.setMailFrom(mailFrom);
                                    listOfBadMessage.add(badMessage);

                                }else{
                                    log.debug("user id: " + userId + ", mid : " + m.getId() + ", attid :" + attId);
                                    attachPart = service.users().messages().attachments().
                                            get(userId, m.getId(), attId).execute();

                                }



                                //ici save en bdd de l'id et du timestamp
                                try{

                                    if(attachPart != null){
                                        mailService.save(new Mail(m.getId(),attId,dateImportTimeStamp));
                                        byte[] fileByteArray = Base64.decodeBase64(attachPart.getData());
                                        attachment = new AttachmentCustom(fileByteArray,part.getFilename());
                                        attachments.add(attachment);
                                    }

                                }catch (DataIntegrityViolationException e){
                                    log.error("Exception dataIntegrity: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

        return attachments;
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    public static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws javax.mail.MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }
    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     */
    public static Message sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws IOException, javax.mail.MessagingException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        log.debug("Message id: " + message.getId());
        log.debug(message.toPrettyString());
        return message;
    }
    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws  IOException, javax.mail.MessagingException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public static String formatStringMessage(List<BadMessage> badMessages){
        StringBuilder stringBuilder = new StringBuilder();
        for(BadMessage message:badMessages){
            stringBuilder.append("Erreur de récupération du zip : ")
                    .append(message.getFileName())
                    .append(", envoyer par : ")
                    .append(message.getMailFrom())
                    .append(", le :")
                    .append(message.getDateReceived())
                    .append("\r\n");
        }
        return stringBuilder.toString();
    }

    public static class AttachmentCustom{
        private byte[] fileByteArray;
        private String fileName;

        public AttachmentCustom(byte[] fileByteArray) {
            this.fileByteArray = fileByteArray;
        }

        public AttachmentCustom(byte[] fileByteArray, String fileName) {
            this.fileByteArray = fileByteArray;
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public byte[] getFileByteArray() {
            return fileByteArray;
        }

        public void setFileByteArray(byte[] fileByteArray) {
            this.fileByteArray = fileByteArray;
        }
    }

    public static class BadMessage{
        private String fileName;
        private String mailFrom;
        private String dateReceived;

        public BadMessage(String fileName, String mailFrom, String dateReceived) {
            this.fileName = fileName;
            this.mailFrom = mailFrom;
            this.dateReceived = dateReceived;
        }

        public BadMessage() {
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getMailFrom() {
            return mailFrom;
        }

        public void setMailFrom(String mailFrom) {
            this.mailFrom = mailFrom;
        }

        public String getDateReceived() {
            return dateReceived;
        }

        public void setDateReceived(String dateReceived) {
            this.dateReceived = dateReceived;
        }
    }

}