package com.dgtd.security.service;



import com.dgtd.security.domain.entity.Profil;
import com.dgtd.security.domain.entity.Utilisateur;
import com.dgtd.security.domain.repository.ProfilRepository;
import com.dgtd.security.domain.repository.UtilisateurRepository;
import com.dgtd.common.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Component
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountServiceImpl implements AccountService<Utilisateur>{


    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UtilisateurRepository utilisateurRepository;

    private ProfilRepository profilRepository;


    private Utilisateur utilisateur;

    private final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);


    @Autowired
    public AccountServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UtilisateurRepository utilisateurRepository,
                              ProfilRepository profilRepository,
                              Utilisateur utilisateur) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.utilisateurRepository = utilisateurRepository;
        this.profilRepository = profilRepository;

        this.utilisateur = utilisateur;
    }

    @Override
    public Utilisateur saveUser(Utilisateur user) {
        String hashPass = bCryptPasswordEncoder.encode(user.getMotDePasse());
        user.setMotDePasse(hashPass);
        return utilisateurRepository.save(user);
    }



    @Override
    public void addRoleToUser(String username, String rolename) throws SecurityException {

        Profil profil;
        try{
            profil = profilRepository.findByIntitule(rolename);
        }catch (Exception e){
            throw e;
        }
        if(profil==null){
            throw new DataException("Le profil que vous essayez d'ajouter n'existe pas");
        }

        try {
            utilisateur = utilisateurRepository.findByIdentifiant(username);
        }catch (Exception e) {
            throw e;
        }

        List<Profil> profils ;
        profils = (List<Profil>) utilisateur.getProfils();

        if(!(profils == null)){
            for(Profil pro : profils){
                if(pro.getIntitule().equals(profil.getIntitule())){
                    throw new DataException("Le profil que vous essayez d'ajouter est déjà présent pour cet utilisateur");
                }
            }
            profils.add(profil);
        }else{
            profils = new ArrayList<>();
        }


        utilisateur.setProfils(profils);
        utilisateurRepository.save(utilisateur);

    }

    @Override
    public Utilisateur findUserByUserName(String username) {

        try{
           utilisateur = utilisateurRepository.findByIdentifiant(username);

        }catch (Exception e){
            log.error("Utilisateur inexistant avec l'identifiant {}", username, " " + e.getLocalizedMessage());
            throw new NullPointerException(e.getLocalizedMessage());
        }
        return utilisateur;
    }


    @Override
    public void deleteUser(String username) {

        log.debug("Suppression de l'utilisateur {}", username);
       try{
           utilisateur = findUserByUserName(username);
       }catch (Exception e){
           throw e;
       }

        utilisateurRepository.delete(utilisateur);

    }
}
