package com.dgtd.common.util;


import com.dgtd.common.exception.WsBackOfficeException;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static javax.mail.Session.*;

public class Mail extends Authenticator {
    private Properties props = new Properties();
    private String login;
    private String password;
    private String subject;
    private String text;
    private List<File> attachments;


    public Mail(String login, String password, String smtp) {
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtp);
        props.put("mail.smtp.port", "25");
        this.login = login;
        this.password = password;
        attachments = new ArrayList<>();
    }

    public Mail() {
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.digitech.fr");
        props.put("mail.smtp.port", "25");
        this.login ="";
        this.password ="";
        attachments = new ArrayList<>();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addAttachment(File file) {
        attachments.add(file);
    }

    public void send(String from, String to) throws Exception {

       Authenticator authenticator = new Authenticator() {
           @Override
           protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(login, password);
           }
       };
        Session session = getInstance(props,authenticator);
             /*   new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        if (login != null)
                            return new PasswordAuthentication(login, password);
                        else
                            return new PasswordAuthentication("", "");
                    }
                });

              */
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        Multipart multipart = new MimeMultipart();

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(text, "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);

        for (File f : attachments) {
            MimeBodyPart attachmentBodyPart;
            attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(f.getAbsolutePath());
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(f.getName());
            multipart.addBodyPart(attachmentBodyPart);
        }
        message.setContent(multipart);

        try {
            Transport.send(message);
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void send(String to) {
        try
        {
            send("mailer@digitech.fr", to);
        }
        catch (Exception e) {
            String error = "Erreur pendant l'envoi du mail à "+ to + " "+ e.getLocalizedMessage();

            throw new WsBackOfficeException(error);
        }
    }
}
