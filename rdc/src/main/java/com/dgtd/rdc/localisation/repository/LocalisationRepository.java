package com.dgtd.rdc.localisation.repository;

import com.dgtd.rdc.localisation.entity.Localisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalisationRepository extends JpaRepository<Localisation, Integer> {

    @Query
    Localisation findByNiv1AndNiv3(String niv_1, String niv_3);

    @Query
    Localisation findByNiv1AndNiv2(String niv_1, String niv_2);

    @Query
    Localisation findByNiv2AndNiv3(String niv_2, String niv_3);
}
