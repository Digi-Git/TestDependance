package com.dgtd.security.domain.repository;

import com.dgtd.security.domain.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    List<Utilisateur> findAllByIdentifiant(String identifiant);

    Utilisateur findByIdentifiant(String identifiant);
}
