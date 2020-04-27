package com.dgtd.security.domain.repository;


import com.dgtd.security.domain.entity.Profil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProfilRepository extends JpaRepository<Profil, Integer> {
    Profil findByIntitule(String intitule);
}
