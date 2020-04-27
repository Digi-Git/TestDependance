package com.dgtd.ecole.ws.service.DBentities;



import com.dgtd.common.exception.WsBackOfficeException;

import java.io.IOException;

/**
 * Chargement des données de base
 * TODO template method
 */
public interface LoadBaseData {

    /**
     * Chargement automatique de données en BDD si nécessaire
     *
     * @throws WsBackOfficeException
     * @throws IOException
     */
    default void loadBaseData() throws WsBackOfficeException, IOException {

    }
}
