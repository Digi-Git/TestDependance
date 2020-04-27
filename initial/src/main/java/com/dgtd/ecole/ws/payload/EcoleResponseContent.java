package com.dgtd.ecole.ws.payload;

import com.dgtd.common.error.DGTDError;
import com.dgtd.common.payload.DGTDResponseContent;
import com.dgtd.common.payload.ResponseType;
import com.dgtd.common.payload.TypeStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EcoleResponseContent implements DGTDResponseContent {


    private int id;
    private Map<String, ResponseType> dossiers;
    private boolean isError;
    private DGTDError error;
    private TypeStatus typeStatus;

    public EcoleResponseContent(){
       dossiers = new HashMap<>();
    }

    /**
     *
     * @return id Ecole
     */
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public Map<String,ResponseType> getDossiers(){
        return this.dossiers;
    }


    public void addToDossiers(String noDossier, ResponseType type){
        if(type == ResponseType.REFUSE){
            setError(true);
        }
        getDossiers().put(noDossier,type);
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }


    /**
     * Non implémenté
     * @return id Expediteur
     */
    @Override
    public String getIdClient() {
        return null;
    }

    @Override
    public void setIdClient(String idExpediteur) {

    }

    @Override
    public DGTDError getErrorObject() {
        return this.error;
    }

    @Override
    public void setErrorObject(DGTDError DGTDError) {
        this.error = DGTDError;

    }

    @Override
    public TypeStatus getStatus() {
        return this.typeStatus;
    }

    @Override
    public void setStatus(TypeStatus status) {
        this.typeStatus = status;
    }

    @Override
    public String toString() {
        return "ResponseContent{" +
                "id=" + id +
                ", dossiers=" + dossiers +
                ", isError=" + isError +
                '}';
    }
}
