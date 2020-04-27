package com.dgtd.ecole.ws.domain.idemia;

import java.io.Serializable;

/**
 * Réponse générique IDEMIA pour les services uin et notify
 *
 */
public class ReponseGenerique implements Serializable {

    int code;
    String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
