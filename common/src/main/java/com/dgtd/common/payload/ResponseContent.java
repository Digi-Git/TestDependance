package com.dgtd.common.payload;

import com.dgtd.common.error.DGTDError;
import org.springframework.stereotype.Component;

@Component
public class ResponseContent implements DGTDResponseContent {

    private String idClient;
    private int id;
    private DGTDError DGTDError;
    private TypeStatus status;

    public ResponseContent(){
        this.status = TypeStatus.EN_COURS;
    }

    public ResponseContent(DGTDError error) {
        this();
        this.DGTDError = error;
    }

    public ResponseContent(String idClient, DGTDError error){
        this.idClient = idClient;
        this.DGTDError = error;
    }
    /**
     *
     * @return data indiquées sur le dossier envoyé
     */
    @Override
    public String getIdClient() {
        return idClient;
    }

    @Override
    public void setIdClient(String idExpediteur) {
        this.idClient = idExpediteur.replace(".zip","");
    }

    /**
     *
     * @return id du dossier traité
     */
    @Override
    public int getId(){
        return id;
    }

    @Override
    public void setId(int id){
        this.id = id;
    }

    @Override
    public com.dgtd.common.error.DGTDError getErrorObject() {
        return DGTDError;
    }

    @Override
    public void setErrorObject(DGTDError DGTDError) {
        this.DGTDError = DGTDError;
    }

    @Override
    public TypeStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(TypeStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String details = "";
        if(DGTDError!=null){
            DGTDError.getDetails();
        }

        return "ResponseContent{" +
                "idClient ='" + idClient + '\'' +
                ", id=" + id +
                ", error =" + details +
                '}';
    }
}
