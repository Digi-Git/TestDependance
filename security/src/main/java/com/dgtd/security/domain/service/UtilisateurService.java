package com.dgtd.security.domain.service;

import com.dgtd.common.exception.DataException;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.common.service.GenericService;
import com.dgtd.common.util.Misc;
import com.dgtd.security.config.UtilisateurConfig;
import com.dgtd.security.domain.entity.Profil;
import com.dgtd.security.domain.entity.Utilisateur;
import com.dgtd.security.domain.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.dgtd.security.config.UtilisateurConfig.PROFIL_ADMINISTRATEUR;
import static com.dgtd.security.config.UtilisateurConfig.PROFIL_SUPERVISEUR;


@Service
@Transactional(rollbackFor = Exception.class)
public class UtilisateurService implements GenericService<Utilisateur, Integer> {

    private final Logger log = LoggerFactory.getLogger(UtilisateurService.class);

    private final UtilisateurRepository utilisateurRepository;
    private final ProfilService profilService;
    private final UtilisateurConfig utilisateurConfig;


    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository, ProfilService profilService, UtilisateurConfig utilisateurConfig) {
        this.utilisateurRepository = utilisateurRepository;
        this.profilService = profilService;
        this.utilisateurConfig = utilisateurConfig;


    }

    /**
     * Pré enregistrement d'un utilisateur
     *
     * @param utilisateur Entité à enregistrer
     * @return la persistance de l'entité
     */
    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        log.debug("Requête pour enregistrer l'utilisateur : {}", utilisateur);
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Obtenir l'ensemble des utilisateurs
     *
     * @param pageable information de pagination
     * @return Liste d'entités
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Utilisateur> findAll(Pageable pageable) {
        log.debug("Demande pour obtenir tous les utilisateurs (pageable)");
        return utilisateurRepository.findAll(pageable);
    }

    /**
     * Obtenir un utilisateur
     *
     * @param id id de l'entité
     * @return l'entité
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Utilisateur> findOne(Integer id) {
        log.debug("Demande l'utilisateur : {}", id);
        return Optional.ofNullable(utilisateurRepository.getOne(id));
    }

    /**
     * Supprimer un utilisateur
     *
     * @param id id de l'entité Utilisateur
     */
    @Override
    public void delete(Integer id) {
        log.debug("Demande de suppression de l'utilisateur : {}", id);
        utilisateurRepository.deleteById(id);

    }

    /**
     * Obtenir l'ensemble des utilisateurs
     *
     * @return liste d'utilisateurs
     */
    @Override
    public List<Utilisateur> findAll() {
        log.debug("Requête pour obtenir l'ensemble des utilisateurs");
        return utilisateurRepository.findAll();
    }

    public boolean isUsernameCreated(String identifiant) {
        log.debug("Recherche la présence de l' identifiant {}", identifiant);
        Optional<Utilisateur> opt = Optional.ofNullable(utilisateurRepository.findByIdentifiant(identifiant));
        return opt.isPresent() ? true : false;
    }

    public Utilisateur findByIdentifiant(String identifiant) {
        return utilisateurRepository.findByIdentifiant(identifiant);
    }


    public Utilisateur register(String identifiant, String password, List<Profil> profils) {

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdentifiant(identifiant);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        utilisateur.setMotDePasse(encoder.encode(password));
        utilisateur.setIsAffecte(0);
        utilisateur.setProfils(profils);
        utilisateurRepository.save(utilisateur);
        log.info("utilisateur créé avec idenfiant {}", utilisateur.getIdentifiant());
        return utilisateur;
    }

    /**
     * Creation d'un administrateur Digitech si aucun admin n'est créé
     *
     */
    //@PostConstruct
    public void loadBaseData()  {
        //Recherche d'un admin Digitech
        String def = utilisateurConfig.getDefautAdmin();

        List<Utilisateur> utilisateurs =
                utilisateurRepository.findAllByIdentifiant(def);
        if (!utilisateurs.isEmpty()) {
            return;
        }
        try {

            //En cas d'absence d'utilisateur, création
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String te = utilisateurConfig.getDefautPassword();
            String pwd = passwordEncoder.encode(te);

            Optional<Profil> pro = profilService.findOne(PROFIL_ADMINISTRATEUR);
            if (!pro.isPresent()) {
                throw new WsBackOfficeException("Le profil n'existe pas");
            }



            //Création d'un admin Digitech
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setIdentifiant(utilisateurConfig.getDefautAdmin());
            utilisateur.setMotDePasse(pwd);
            utilisateur.setPrenom("Digitech");
            utilisateur.setNom("Administrateur");
            utilisateur.setIsAffecte(0);
            // utilisateur.setParentId(-1);

            Set<Profil> profilSet = new HashSet<>();
            profilSet.add(pro.get());
            utilisateur.setProfils(profilSet);


            // utilisateur.getProfils().add(proSuperv);
            save(utilisateur);


        } catch (Exception ex) {
            log.error("erreur : impossiblité d'ajouter les utilisateurs complémentaires : " + ex.getLocalizedMessage());

        }
    }

    public boolean handleSuperviseur(String identifiant) {

        if (identifiant == null) {
            return false;
        }
        boolean isUserExist = isUsernameCreated(identifiant);
        if(identifiant ==""){
            throw new DataException("identifiant superviseur inexistant");
        }
        Utilisateur utilisateur;
        if (!isUserExist ) {

            log.debug("création de l'utilisateur {}", identifiant);
            Optional<Profil> profil = profilService.findOne(PROFIL_SUPERVISEUR);
            List<Profil> profils = new ArrayList<>();
            profils.add(profil.get());
            utilisateur = register(identifiant, utilisateurConfig.getDefautPassword(), profils);


        } else {
            utilisateur = findByIdentifiant(identifiant);
            log.debug("utilisateur déjà créé {}", utilisateur.getIdentifiant());
        }
        return true;

    }

    /**
     * Fonction démarrée automatiquement au démarrage de l'application
     * Enregistrement des utilisateurs présents si le fichier definitions/userAppend.txt est présent
     * Création de l'utilisateur seulement s'il n'existe pas
     * Utilisation pour les créations simples : pas de joint agent / superviseur
     *
     * @throws IOException
     * @throws WsBackOfficeException
     */
    public void appendUser() throws IOException, WsBackOfficeException {

        //Chargement du fichier des localisations par défaut
        File file = new ClassPathResource("/appinit/userAppend.txt").getFile();
        if (!file.exists()) {
            log.debug("Pas de fichier d'utilisateurs complémentaires présent");
            return;
        }
        try {
            //Import du fichier
            int lig = 0;

            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                lig++;
                log.debug("***** "+ line);
                String[] str = line.split("\t");
                if (str.length == 5) {
                    //Recherche de l'utilisateur
                    String identifiant = str[0].trim();
                    List<Utilisateur> utilisateurs = utilisateurRepository.findAllByIdentifiant(identifiant);

                    if (utilisateurs.isEmpty()) {
                        //Recherche du profil
                        Optional<Profil> proSuperv = profilService.findOne(Integer.parseInt(str[4].trim()));
                        if (!proSuperv.isPresent()) {
                            throw new WsBackOfficeException("Creation de l'utilisateur additionnel : ligne " +
                                    lig + " Le profil n'existe pas ");
                        }



                        try {
                            Utilisateur utilisateur = new Utilisateur();
                            utilisateur.setIdentifiant(identifiant);
                            utilisateur.setMotDePasse(Misc.encryp(str[1].trim()));
                            utilisateur.setPrenom(str[2].trim());
                            utilisateur.setNom(str[3].trim());
                            utilisateur.setIsAffecte(0);
                            Set<Profil> profilSet = new HashSet<>();
                            profilSet.add(proSuperv.get());
                            utilisateur.setProfils(profilSet);
                            utilisateurRepository.save(utilisateur);

                        } catch (Exception e) {
                            throw new WsBackOfficeException("Erreur save - création d'un utilisateur additionnel : ligne " +
                                    lig + " err= " + e.getLocalizedMessage());
                        }
                    }
                }

            }
            bufferedReader.close();

        } catch (Exception ex) {
            log.error("erreur : impossiblité d'ajouter les utilisateurs complémentaires : " + ex.getLocalizedMessage());
        }
    }
}
