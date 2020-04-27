package com.dgtd.common.dicoutil;

import java.util.HashMap;

public class ListValeur {

    private HashMap <String, ListValeurItemGroupe> listValeurs;

    public ListValeur() {

        listValeurs = new HashMap<>();
    }

    public void clearListValeur(){
        listValeurs.clear();
    }

    public boolean isEmptyListValeur() {
        return listValeurs.isEmpty();
    }



    /**
     * Obtient une liste de valeur déja enregistré (via AddNewListItemGroupe par exemple).
     *
     * @param tag
     * @return la liste si trouvée ou null
     */
    public ListValeurItemGroupe GetListValeurGroupe (String tag) {

        if (listValeurs.containsKey(tag))
            return listValeurs.get(tag);
        else
            return null;
    }

    /**
     * Ajout d'une nouvelle liste.
     * A noter que la liste est triée d'aprés l'élément ordre.
     *
     * @param tag
     * @param listValeurItemGroupe
     * @return True si ok, ou false si le tag de liste existe déja.
     */
    public boolean AddNewListItemGroupe (String tag, ListValeurItemGroupe listValeurItemGroupe) {

        if (listValeurs.containsKey(tag))
            return false;

        listValeurItemGroupe.sortListValeurGroupe();
        listValeurs.put(tag, listValeurItemGroupe);

        return true;
    }

}
