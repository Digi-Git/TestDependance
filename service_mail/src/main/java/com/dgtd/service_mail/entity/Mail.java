package com.dgtd.service_mail.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Mail {

    private int id;
    private String mail_id;
    private String attachment_id;
    private String date_import;

    public Mail(String mail_id, String date_import) {

        this.mail_id = mail_id;
        this.date_import = date_import;
    }

    public Mail(String mail_id, String attachment_id, String date_import) {
        this.mail_id = mail_id;
        this.attachment_id = attachment_id;
        this.date_import = date_import;
    }

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "mail_id")
    public String getMail_id() {return mail_id;}

    public void setMail_id(String mail_id) {
        this.mail_id = mail_id;
    }

    @Basic
    @Column(name = "date_import")
    public String getDate_import() { return date_import; }

    public void setDate_import(String date_import) {
        this.date_import = date_import;
    }

    public String getAttachment_id() {
        return attachment_id;
    }

    public void setAttachment_id(String attachment_id) {
        this.attachment_id = attachment_id;
    }
}
