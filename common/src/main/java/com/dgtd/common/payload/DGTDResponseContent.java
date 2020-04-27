package com.dgtd.common.payload;


import com.dgtd.common.error.DGTDError;

public interface DGTDResponseContent {
    String getIdClient();

    void setIdClient(String idExpediteur);

    int getId();

    void setId(int id);

    DGTDError getErrorObject();

    void setErrorObject(DGTDError DGTDError);

    TypeStatus getStatus();

    void setStatus(TypeStatus status);
}
