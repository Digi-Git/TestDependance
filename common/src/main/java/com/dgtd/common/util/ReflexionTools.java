package com.dgtd.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflexionTools {

    /**
     * Recherche de la proprité dans l'instance passée en parametre.
     * Le nom de la propriété cible est transformée en majuscule pour simplifier le mactch.
     *
     * @param objet Instance passée en parametre
     * @param target Le nom de la propriété à rechercher
     * @return La propriété trouvée ou null
     */
    public static Field findPropInObject (Object objet, String target) {

        String rech = target.toUpperCase();

        // Parcours des membres de la classe
        for (Field f : objet.getClass().getDeclaredFields()) {

            if (f.getName().toUpperCase().contentEquals(rech))
                return f;
        }

        return null;
    }

    /**
     * Recherche d'un getter ou setter dans les instances de classes passées en parametre
     *
     * @param object Instance de classe source
     * @param nameProp Champ (propriété) pour laquelle le getter ou setter doit être trouvé.
     * @return La méthode ou null si non trouvé.
     */
    public static Method findMethod (Object object, String nameProp, Boolean wantGetter) {

        Method[] methods = object.getClass().getDeclaredMethods();
        String fieldUpper = nameProp.toUpperCase();

        for (Method method : methods) {

            String proto = "";

            if (wantGetter && isGetter(method)) {

                // Test 1
                proto = "GET" + fieldUpper;
                if (method.getName().toUpperCase().contentEquals(proto))
                    return method;

                // Test 2
                proto = "IS" + fieldUpper;
                if (method.getName().toUpperCase().contentEquals(proto))
                    return method;
            }

            else if (!wantGetter && isSetter(method)) {
                proto = "SET" + fieldUpper;
                if (method.getName().toUpperCase().contentEquals(proto))
                    return method;
            }
        }

        return null;
    }

    /**
     * Une methode est un getter si :
     * - Commence par get ou is
     * - N'a pas de parametre
     * - Un type de retour différent de void
     * @param method Nom d'une des méthodes d'une classe
     * @return True si la methode est un setter.
     */
    private static boolean isGetter(Method method){

        if(method.getParameterCount() == 0 && !method.getReturnType().equals(void.class)){
            return true;
        }
        return false;
    }

    /**
     * Une methode est un setter si :
     * - Commence par set
     * - A qu'un seul parametre
     * - N'a pas de retour (void)
     * @param method Nom d'une des méthodes d'une classe
     * @return True si la methode est un setter.
     */
    private static boolean isSetter(Method method){

        if (method.getParameterCount() == 1 && method.getReturnType().equals(void.class)){
            return true;
        }

        return false;
    }


}
