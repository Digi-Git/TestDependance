package com.dgtd.security.filter;


import com.dgtd.common.error.ErrorObject;
import com.dgtd.common.error.ErrorService;
import com.dgtd.common.error.TypeError;
import com.dgtd.security.exception.ForbiddenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Filtrage des requ&ecirc;tes entrantes sur les URI avec authentification
 * @see SecurityConstants
 * D&eacute;codage du JWT : R&eacute;ception des m&eacute;tadonn&eacute;es et du token
 *
 */
@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

private AuthenticationManager authenticationManager;
private ErrorService errorService;

public JWTAuthorizationFilter(AuthenticationManager authenticationManager, ErrorService errorService){
    super();
    this.authenticationManager = authenticationManager;
    this.errorService = errorService;
}


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Récupération de l'entête Authorization
        final Optional<String> token = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));

          if(request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
        }
        //Vérification de la présence de l'entête et de sa construction
        if(token.isPresent() && token.get().startsWith(SecurityConstants.TOKEN_PREFIX)) {
            //Décodage du JWT
            try {
               String jwt = token.get();
                Claims claims = Jwts.parser()
                        .setSigningKey(SecurityConstants.SIGNING_KEY)
                        .parseClaimsJws(jwt.replace(SecurityConstants.TOKEN_PREFIX, ""))
                        .getBody();
                //Lors de l'identification nous avons ajouté l'username dans subject
                String username = claims.getSubject();
                //Récupération du payload "roles"
                ArrayList<Map<String, String>> roles = (ArrayList<Map<String, String>>) claims.get("roles");
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                roles.forEach(role ->
                        authorities.add(new SimpleGrantedAuthority(role.get("authority"))));

                //Création d'un objet User du framework Spring pour utiliser les méthodes du framework
                //Le bean AuthenticationManager, crée dans SecurityConfig, avec argument d'un com.dgtd.com.dgtd.security.service qui implémente UserDetailsService
                //qui fait le lien avec notre BDD et entités Utilisateur et Profil

                UsernamePasswordAuthenticationToken authenticatedUser =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

            } catch (Exception e) {
                //throw new DataException("Token invalide " + e.getLocalizedMessage());
                String error = e.getLocalizedMessage();
                ErrorObject errorObject = new ErrorObject(TypeError.SECURITE,error);
                errorObject.setId(errorObject.getTypeError() + " -"+ errorObject.getDate());
                errorService.insertError(errorObject,false);
                throw new ForbiddenException(error);
            }

        }

        filterChain.doFilter(request,response);
        // Clean authentication after process
        SecurityContextHolder.getContext().setAuthentication(null);
    }


    private static class SecurityConstants {

        private static final String SIGNING_KEY = "MaYzkSjmkzPC57L";
        private static final Long EXPIRATION_TIME = TimeUnit.MILLISECONDS.convert(10, TimeUnit.HOURS);
        private static final String HEADER_REQUEST = "Authorization";
        private static final String TOKEN_PREFIX = "Bearer ";

        //Decrytage
        private static final String JSON_CRYPTED_AK = "!cdKinshasaCOD*";        // Clef
        private static final String JSON_CRYPTED_IV = "Dgtd2-Jc";   			// IV


    }
}
