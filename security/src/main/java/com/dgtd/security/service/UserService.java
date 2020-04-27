package com.dgtd.security.service;


import com.dgtd.security.domain.entity.Profil;
import com.dgtd.security.domain.entity.Utilisateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
@Service
public class UserService implements UserDetailsService {


    private AccountServiceImpl accountService;

    private final Logger log = LoggerFactory.getLogger(UserService.class);



    @Autowired
    public void setAccountService(AccountServiceImpl accountService){
        this.accountService = accountService;
    }

    public AccountService getAccountService(){
       return this.accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Utilisateur utilisateur;

        try{

           utilisateur =  accountService.findUserByUserName(username);
        }catch (Exception e){
            log.error("Identifiant {} non reconnu en base en données", username + e.getLocalizedMessage());

            throw e;
        }
        if(utilisateur == null){
            throw new UsernameNotFoundException(username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        List<Profil> profils = (List<Profil>) utilisateur.getProfils();
        for(Profil profil:profils){
            authorities.add(new SimpleGrantedAuthority(profil.getIntitule()));
        }

        /*utilisateur.getProfils().forEach(role -> {
            String profilName = role.getIntitule();
            authorities.add(new SimpleGrantedAuthority(profilName));
        });
         */
        return new User(utilisateur.getIdentifiant(), utilisateur.getMotDePasse(), authorities);
    }
}
