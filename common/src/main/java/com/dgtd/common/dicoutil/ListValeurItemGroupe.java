package com.dgtd.common.dicoutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListValeurItemGroupe {

    private List<ListValeurItem> listValeurItemList;
    private String tag;

    public ListValeurItemGroupe() {

        listValeurItemList = new ArrayList<>();
    }
    public String getTag() {
        return this.tag;
    }
    public ListValeurItemGroupe(String t) {

        listValeurItemList = new ArrayList<>();
        this.tag = t;

    }




    /**
     * Ajout d'une valeur dans la liste
     * Est utilisée uniquement dans le package.
     *
     * @param listValeurItem item à ajouter
     */
    public boolean AddListValeurItem (ListValeurItem listValeurItem) {

        listValeurItem.setArrayPos(listValeurItemList.size());
        return (listValeurItemList.add(listValeurItem));
    }

    /**
     * Retourne le contenu de la liste. Voir si pas préférable de renvoyer une copie.
     * @return
     */
    public List<ListValeurItem> getListValeurItems () {
        return listValeurItemList;
    }

    /**
     * Retourne l'élément dans la liste indiqué par défaut.
     *
     * @return l'élément par défaut, le premier élément si aucun n'a été trouvé ou null si la liste est vide.
     */
    public ListValeurItem getListValeurItemDefaut () {

        ListValeurItem ret = null;

        for (ListValeurItem item : listValeurItemList) {
            if (item.isDefaut()) {
                ret = item;
                break;
            }
        }

        return ret;
    }

    /**
     * Retourne un élément dans la liste correspondant au tag développement passé en argu
     *
     * @param devTag Tag recherché
     * @return L'item trouvé ou null dans le cas contraire.
     */
    public ListValeurItem getListValeurItemDevTag (int devTag) {

        ListValeurItem ret = null;

        for (ListValeurItem item : listValeurItemList) {
            if (item.getDevTag() == devTag) {
                ret = item;
                break;
            }
        }

        return ret;

    }

    /**
     * Retourne l'élément correspondant à l'ID passé en arguement
     *
     * @return l'élément d'aprés l'ID demandé ou null si pas trouvé.
     */
    public ListValeurItem getListValeurItemFromId (int id) {

        ListValeurItem ret = null;

        for (ListValeurItem item : listValeurItemList) {
            if(ret==null && item.isDefaut()==true){
                ret = item;
            }
            if (item.getId() == id) {
                ret = item;
                break;
            }
        }

        return ret;
    }

    /**
     * Retourne l'élément correspondant à la position passée en argument.
     * La position est la postion (l'indice) dans la liste.
     *
     * @return l'élément d'aprés la position dans la liste passée en argument ou null si pas trouvé.
     */
    public ListValeurItem getListValeurItemFromPosition (int position) {

        ListValeurItem ret = null;

        for (ListValeurItem item : listValeurItemList) {
            if (item.getArrayPos() == position) {
                ret = item;
                break;
            }
        }

        return ret;
    }

    /**
     * Tri de la liste d'aprés l'élément ordre d'un ListValeurItem.
     *
     * Pour mémoire : -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
     *
     */
    void sortListValeurGroupe () {

        Collections.sort(this.listValeurItemList, new Comparator<ListValeurItem>() {
            @Override
            public int compare(ListValeurItem tam2, ListValeurItem tam1)
            {
                return tam1.getOrdre() >  tam2.getOrdre() ? -1 : (tam1.getOrdre() <  tam2.getOrdre()) ? 1 : 0;
            }
        });

        for (int i = 0 ; i < this.listValeurItemList.size() ; i++)
            this.listValeurItemList.get(i).setArrayPos(i);
    }
}
