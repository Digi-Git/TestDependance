package com.dgtd.ecole.ws.repository;

import com.dgtd.rdc.entity.Dossier;
import com.dgtd.rdc.entity.Personne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonneRepository extends JpaRepository<Personne,Integer> {

    @Query("select p from Personne p where p.dossierByDossierId = :dossierByDossierId")
    List<Personne> getAllByDAndDossierByDossierId(Dossier dossierByDossierId);



}
