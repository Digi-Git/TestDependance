package com.dgtd.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
public class ErrorMailService implements IMailService {


   private JavaMailSender mailSender;

   private final Logger log = LoggerFactory.getLogger(ErrorMailService.class);

   public ErrorMailService(JavaMailSender mailSender){
       this.mailSender = mailSender;
   }


    @Override
    public void sendMessage(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("mailer@digitech.fr");
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    public void sendMessageWithAttachement(String to, String subject, String text, File file) throws MessagingException {
       MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("mailer@digitech.fr");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        helper.addAttachment("Erreurs BO Central", file);

        mailSender.send(message);
    }
}
