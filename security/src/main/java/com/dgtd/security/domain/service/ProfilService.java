package com.dgtd.security.domain.service;


import com.dgtd.common.service.GenericService;
import com.dgtd.security.domain.entity.Profil;
import com.dgtd.security.domain.repository.ProfilRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProfilService implements GenericService<Profil, Integer> {
    private final Logger log = LoggerFactory.getLogger(ProfilService.class);

    private final ProfilRepository profilRepository;

    @Autowired
    public ProfilService(@Lazy ProfilRepository profilRepository) {
        this.profilRepository = profilRepository;

    }

    /**
     * Saves a given com.dgtd.rdc.localisation.entity. Use the returned instance for further operations as the save operation might have changed the
     * com.dgtd.rdc.localisation.entity instance completely.
     *
     * @param profil must not be {@literal null}.
     * @return the saved com.dgtd.rdc.localisation.entity will never be {@literal null}.
     */
    @Override
    public Profil save(Profil profil){
        log.debug("Demande de sauvegarde de profil id n° : {}", profil.getIntitule());
        return profilRepository.save(profil);
    }

    /**
     *
     * @return a list of profiles
     */
    @Override
    public List<Profil> findAll() {
        log.debug("Requête pour obtenir l'ensemble des profils");
        return profilRepository.findAll();
    }



    /**
     * Deletes a given com.dgtd.rdc.localisation.entity.
     *
     * @param id
     *
     */
    @Override
    public void delete(Integer id) {
        log.debug("Suppression d'un profil");
        profilRepository.deleteById(id);

    }

    /**
     * Returns a reference to the com.dgtd.rdc.localisation.entity with the given identifier. Depending on how the JPA persistence provider is
     * implemented this is very likely to always return an instance and throw an
     * {@link EntityNotFoundException} on first access. Some of them will reject invalid identifiers
     * immediately.
     *
     * @param id must not be {@literal null}.
     * @return a reference to the com.dgtd.rdc.localisation.entity with the given identifier.
     * @see Profil  for details on when an exception is thrown.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Profil> findOne(Integer id) {
        log.debug("Requête du profil id : {}", id);
        return Optional.ofNullable(profilRepository.getOne(id));
    }

    /**
     * Returns a {@link Page}
     * of DBentities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     * @return a page of DBentities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Profil> findAll(Pageable pageable) {
        log.debug("Requête de l'ensemble des profils");
        return profilRepository.findAll(pageable);
    }
    /**
     * Fonction appelée lors de l'initialisation de la base de donnée
     * pour la création des profils
     */
    @PostConstruct
    public void loadBaseData () {

        // Recherche d'un admin digitech
        List<Profil> profils = findAll();
        if (!profils.isEmpty()){
            return;
        }
        save (new Profil(1,"Administrateur"));
        save(new Profil( 2,"Manager"));
        save(new Profil( 3,"Superviseur"));
        save(new Profil(4,"Agent enquêteur"));
        save(new Profil(5, "Analyste"));
    }
}
