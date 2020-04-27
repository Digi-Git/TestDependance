package com.dgtd.security.controller;


import com.dgtd.security.config.Constant;
import com.dgtd.security.domain.entity.Utilisateur;
import com.dgtd.security.service.AccountServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;


@RestController
public class UserAccountController {

    private Utilisateur utilisateur;
    private AuthenticationManager authenticationManager;

    private AccountServiceImpl accountService;

    private static final Logger log = LoggerFactory.getLogger(UserAccountController.class);

    public UserAccountController(Utilisateur utilisateur, AuthenticationManager authenticationManager,
                                 AccountServiceImpl accountService) {
        this.utilisateur = utilisateur;
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;

    }


    @PostMapping(value = "/login")
    public ResponseEntity authenticate(@RequestParam("username") String username, @RequestParam("password") String password) {


        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = (User)authentication.getPrincipal();

        String jwtToken;
        if( authentication.isAuthenticated()) {
            jwtToken = Jwts.builder()
                    .setSubject(user.getUsername())
                    .setExpiration(new Date(System.currentTimeMillis()+ Constant.getExpirationTime()))
                    .signWith(SignatureAlgorithm.HS256, Constant.getSigningKey())
                    .claim("roles", user.getAuthorities())
                    .compact();
            return ResponseEntity.ok().body(jwtToken);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }


    @PostMapping(value = "/users/{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity register(@PathVariable("username") String username, @RequestParam("password") String password,
                                   @RequestParam("nom")String nom, @RequestParam("prenom")String prenom,
                                   @RequestParam(value="authorities", required = false) ArrayList<String> authorities) throws SecurityException {

        //Vérification si l'utilisateur n'est pas déjà présent en BDD
        Utilisateur user = accountService.findUserByUserName(username);
        if(user!=null) throw new SecurityException("L'utilisateur existe déjà avec l'identifiant " + username);

        //Création de l'utilisateur
        utilisateur.setIdentifiant(username);
        utilisateur.setMotDePasse(password);
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setIsAffecte(0);
        accountService.saveUser(utilisateur);

        //Récupération des profils
        if(authorities == null) {
            accountService.addRoleToUser(username, "Analyste");
        }else {

            for (String role : authorities) {
                if (role != null) {
                  accountService.addRoleToUser(username, role);
                  }
            }
        }

        return ResponseEntity.ok("Uilisateur "+ utilisateur.getIdentifiant()+ " enregistré");
    }



    /**
     * @param username identifiant
     * @param authorities rôle souhaitant être ajouté
     * @return confirmation ou exception
     */
    @PutMapping("/users/{username}")
    public ResponseEntity registerRoleToUser(@PathVariable("username") String username,
                                             @RequestParam("authorities") String authorities) {

        try{
            accountService.addRoleToUser(username,authorities);
        }catch (Exception e){
            throw new SecurityException(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Suppression d'un utilisateur
     * @param username identifiant
     * @return confirmation prise en compte
     */
    @DeleteMapping("/users/{username}")
    public ResponseEntity deleteUser(@PathVariable("username") String username){
        try{
            accountService.deleteUser(username);
        }catch(SecurityException e){
            throw new SecurityException(e.getMessage());
        }
        return ResponseEntity.ok().build();

    }

}
