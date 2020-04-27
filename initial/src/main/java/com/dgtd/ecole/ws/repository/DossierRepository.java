package com.dgtd.ecole.ws.repository;


import com.dgtd.rdc.entity.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DossierRepository extends JpaRepository<Dossier, Integer> {
  /*  @Query("select case when count(c) >0 then true else false end from Dossier d where d.noDossier =:noDossier")
    boolean existsByNoDossier(String noDossier);
    */


   /* @Query("select d from Dossier d where d.noDossier like :noDossier or d.noDossier like %:noDossier")
    List<Dossier> getByNoDossier(String noDossier);
*/
    @Query("select d from Dossier d where d.noDossier like :noDossier or d.noDossier like %:distinctionAndNoDossier")
    List<Dossier> findAllByNoDossierContaining(String distinctionAndNoDossier, String noDossier);
}
