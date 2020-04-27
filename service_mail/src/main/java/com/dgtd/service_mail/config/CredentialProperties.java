package com.dgtd.service_mail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "digitech.mail")
public class CredentialProperties {

    private String client_id;
    private String project_id;
    private String auth_uri;
    private String token_uri;
    private String auth_provider_x509_cert_url;
    private String client_secret;
    private String[] redirect_uris;
    private String tokens_directory_path;
    private String path_attachment;
    private int port;
    private String application_name;
    private String mailToZipError;
    private String MailFromZipError;
    private String MailSubjectZipError;


    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getAuth_uri() {
        return auth_uri;
    }

    public void setAuth_uri(String auth_uri) {
        this.auth_uri = auth_uri;
    }

    public String getToken_uri() {
        return token_uri;
    }

    public void setToken_uri(String token_uri) {
        this.token_uri = token_uri;
    }

    public String getAuth_provider_x509_cert_url() {
        return auth_provider_x509_cert_url;
    }

    public void setAuth_provider_x509_cert_url(String auth_provider_x509_cert_url) {
        this.auth_provider_x509_cert_url = auth_provider_x509_cert_url;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String[] getRedirect_uris() {
        return redirect_uris;
    }

    public void setRedirect_uris(String[] redirect_uris) {
        this.redirect_uris = redirect_uris;
    }

    public String getTokens_directory_path() {
        return tokens_directory_path;
    }

    public void setTokens_directory_path(String tokens_directory_path) {
        this.tokens_directory_path = tokens_directory_path;
    }

    public String getPath_attachment() {
        return path_attachment;
    }

    public void setPath_attachment(String path_attachment) {
        this.path_attachment = path_attachment;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public String getMailToZipError() {
        return mailToZipError;
    }

    public void setMailToZipError(String mailToZipError) {
        this.mailToZipError = mailToZipError;
    }

    public String getMailFromZipError() {
        return MailFromZipError;
    }

    public void setMailFromZipError(String mailFromZipError) {
        MailFromZipError = mailFromZipError;
    }

    public String getMailSubjectZipError() {
        return MailSubjectZipError;
    }

    public void setMailSubjectZipError(String mailSubjectZipError) {
        MailSubjectZipError = mailSubjectZipError;
    }
}
