package com.dgtd.ecole.ws.repository;

import com.dgtd.rdc.entity.Dossier;
import com.dgtd.rdc.entity.Enfant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnfantRepository extends JpaRepository<Enfant, Integer> {

    @Query("select e from Enfant e LEFT JOIN FETCH e.localisationByLocalisationNaissanceId where e.dossierByDossierId = :dossierByDossierId")
    List<Enfant> getAllByDossierByDossierId(Dossier dossierByDossierId);

    // Recherche des enfants n'ayant pas fait déja l'objet d'un export (synchro) vers Evelin
    @Query("select e from Enfant e where e.export IS NULL or e.export = 0")
    List<Enfant> findEnfantASynchroniser ();

    // Obtention des 50 premiers enfants non exporté vers Evelin.
    // La magie de JPA et Spring via method name derivation.
    // https://www.baeldung.com/jpa-limit-query-results
    // https://www.logicbig.com/tutorials/spring-framework/spring-data/limiting-query-results.html
    List<Enfant> findFirst50ByExport (int export);


}
