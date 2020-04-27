package com.dgtd.evelin_common;

import java.util.List;

/**
 * @param <T> classe "model" qui fait l'objet de la requête en BDD (Enfant,  Acte, Dossier)
 */
public interface EvelinService<T> {

    /**
     * Fonctionnalité dédiée à récupérer la liste implémentée dans les services appropriés
     * Services adéquats aux modèles utiles : Enfant, Acte, Dossier
     * @return la liste des actes à déclarer
     */
    List<T> getDataToDeclare();


}
