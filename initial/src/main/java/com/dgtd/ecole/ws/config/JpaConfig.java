package com.dgtd.ecole.ws.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({"com.dgtd"})
@EnableJpaRepositories(basePackages = "com.dgtd")
public class JpaConfig {

   public JpaConfig(){
        super();
    }

}
