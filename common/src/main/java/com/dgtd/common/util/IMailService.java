package com.dgtd.common.util;


import javax.mail.MessagingException;
import java.io.File;

public interface IMailService {

    void sendMessage(String to, String subject, String message);
    void sendMessageWithAttachement(String to, String subject, String text, File attachment) throws MessagingException;

}
