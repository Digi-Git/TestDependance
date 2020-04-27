package com.dgtd.evelin_common;

import com.dgtd.common.error.ErrorService;
import com.dgtd.common.exception.DataException;
import com.dgtd.evelin_common.config.EvelinProperties;
import com.dgtd.rdc.entity.Ecole;
import com.dgtd.rdc.localisation.entity.Localisation;
import com.dgtd.rdc.localisation.service.LocalisationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public abstract class RDC2Certificate<T> extends Certificate<T> {


    protected Ecole ecole;

    public RDC2Certificate(EvelinProperties evelinProperties, ErrorService errorService, LocalisationService localisationService) throws Exception {
        super(evelinProperties, errorService, localisationService);

    }




    public void setEcole(Ecole ecole) {
        this.ecole = ecole;
    }

    /**
     * @return province
     */
    protected String getProvinceCode(){
        String province = "";
        if(this.ecole != null){
            int commune = ecole.getLocalisationId();
            Optional<Localisation> localisation = localisationService.findOne(commune);
            if(localisation.isPresent()){
                province = localisation.get().getNiv1();
            }
        }else{
            throw new DataException("To développeur : Instancier l'école!!! ");
        }

        return province;
    }


}
