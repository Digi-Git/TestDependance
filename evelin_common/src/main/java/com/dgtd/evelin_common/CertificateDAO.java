package com.dgtd.evelin_common;


import com.dgtd.common.type.TypeActe;
import com.dgtd.common.type.TypePersonne;
import com.dgtd.rdc.entity.Personne;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Interface faite pour éventuellement faire un DAO à la génération des actes
 * si on ajoute d'autres types d'actes (ou volonté d'uniformisation avec BO RDC1)
 * @param <T>
 */
public interface CertificateDAO<T> {


	TypeActe getTypeActe();

	/**
	 *
	 * @return objet principal de l'acte (enfant, dossier, ou acte en fonction du type de certificat)
	 */
	T getInteresse();

	/**
	 *
	 * @return ID acte de l'acte brouillon envoyé à Evelin et qui sert d'External ID
	 */
	String getIdActe();

	/**
	 * Construction de l'id acte qui sert aussi d'external ID pour EVELIN
	 * @param interesse qui peut être une personne, un enfant, un acte
	 */
	void buildIdActe(T interesse);

	/**
	 *
	 * @param personne personne concernée
	 * @return id de la personne référencé dans l'acte brouillon fourni à Evelin
	 */
	String getIdActePerson( Personne personne);

	/**
	 * Mise en forme des métadonnées suivant les exigences EVELIN
	 * @param mtc map ou se trouve l'ensemble des métas recensées dans le fichier xml
	 * @return une chaine de caractère qui peut s'intégrer directement dans la balise metadata du fichier final XML
	 * @throws IOException lors de la récupération du template dédié aux métas
	 */
	String generateBigMeta(Map<String, String> mtc) throws IOException;


	/**
	 * @return date actuelle
	 */
	String getDateCourante();

	/**
	 * Génération de la date dans le constructeur du certificat
	 * @return date actuelle
	 */
	String generateEventDate();

	/**
	 *
	 * @return l'ensemble des personnes lié à l'acte (sauf enfant dans BO Ecole)
	 */
	Collection<Personne> getPersonnes();

	/**
	 *
	 * @return une map personne d'obtenir facilement la bonne personne en fonction du contexte
	 */
	Map<TypePersonne,Personne> generatePersonnesMap();


	/**
	 *
	 * @param typePersonne
	 * @return tag (XML) renseigné dans les métadata de l'acte brouillon
	 */
	String getPersonTag(TypePersonne typePersonne);


}
