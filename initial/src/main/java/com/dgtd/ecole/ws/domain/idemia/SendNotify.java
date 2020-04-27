package com.dgtd.ecole.ws.domain.idemia;

import java.io.Serializable;

public class SendNotify implements Serializable {

    String uin;
    String source;

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
