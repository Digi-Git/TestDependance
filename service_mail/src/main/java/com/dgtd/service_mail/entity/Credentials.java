package com.dgtd.service_mail.entity;

import java.io.Serializable;

public class Credentials implements Serializable {

    private Installed installed;

    public Credentials() {
    }

    public Credentials(Installed installed) {
        this.installed = installed;
    }

    public Installed getInstalled() {
        return installed;
    }

    public void setInstalled(Installed installed) {
        this.installed = installed;
    }
}
