package com.dgtd.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Interface générique qui reprend les méthodes Hibernate CRUD
 * @param <T>
 * @param <U>
 */
public interface GenericService<T,U> {

    /**
     * Enregistrement d'un utilisateur
     * ou mise à jour si on utilise la clé primaire
     * @param entity Entité à enregistrer
     * @return com.dgtd.rdc.localisation.entity
     */
    T save(T entity);


    /**
     * Retrouver une com.dgtd.rdc.localisation.entity
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    Optional<T> findOne(U id);

    /**
     * Retrouver toutes les entités par page
     * @param pageable
     * @return des pages d'entités
     */
    @Transactional(readOnly = true)
    Page<T> findAll(Pageable pageable);

    /**
     * Retrouver l'ensemble des entités
     * @return une liste d'entités
     */
    @Transactional(readOnly = true)
    List<T> findAll();

    /**
     * Supprimer une entité
     * @param id
     */
    void delete(U id);
}
