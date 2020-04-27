package com.dgtd.security.config;


import com.dgtd.common.error.ErrorService;
import com.dgtd.security.filter.JWTAuthorizationFilter;
import com.dgtd.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs/**",
            "/webjars/**",
            "/login/**",
            "/tools/version",
            "/tools/echo",
            "/tools/profil/all",
            "/rapidpro/**",
            "/v1/**"
    };


    private UserService userService;
    private ErrorService errorService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public SecurityConfig(@Lazy UserService userService, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder, ErrorService errorService) {
        super();
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.errorService = errorService;
    }



    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

   /* @Bean
    public AccountServiceImpl accountService(){
        return new AccountServiceImpl();
    }
*/

    /**
     * Si la methode n'est pas surchargée on utilise la configuration par défaut
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //desactive l'envoi du token synchronisé prévu pour l'attague CSRF
        //pas utile si stockage client dans les cookies
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(new JWTAuthorizationFilter(authenticationManager(),errorService), UsernamePasswordAuthenticationFilter.class);

    }

    /**
     * @param auth authentication
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    /**
     * Encodage du mot de passe avec la méthode bcrypt
     * qui sera retourné dans le JWT
     * @return mot de passe crypté
     */
    @Bean
    protected BCryptPasswordEncoder getbCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

/*
    @Bean
    public UserService userService(){return new UserService();}

 */
    /**
     * Bean utilisé par le serveur autorisation valider ou non les requêtes et
     * pour le serveur ressource pour décoder les tokens d'accès
     * Utilisation d'une clé symétrique
     * @return  token converti
     *
     */
    /*
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(signingKey);
        return converter;
    }
    */
    /**
     * @return datasource
     */
/*
    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(jdbcTemplate.getDataSource());
    }

    @Bean
    @Primary
    //Making this primary to avoid any accidental duplication with another token com.dgtd.com.dgtd.security.service instance of the same name
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }



*/



}
