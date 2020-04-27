package com.dgtd.common.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/***
 * De base, à l'inverse des fichiers de propriétés .properties, les fichiers yaml ne sont pas reconnus nativement dans un contexte
 * multimodule, où l'objectif est d'avoir un fichier de configuration propre pour le module.
 *
 * https://stackoverflow.com/questions/43020491/spring-boot-external-configuration-of-property-file
 *
 * Par ailleurs, la solution validé dans le post ci-dessus n'est pas trés complête. Il existe des alternatives propres
 * en passant par l'implémentation d'une classe PropertySourceFactory comme ci-dessous.
 * https://mdeinum.github.io/2018-07-04-PropertySource-with-yaml-files/
 * La classe exemple de l'article est récupérée ci-dessous.
 *
 * A noter que même si l'article date de juillet 2018, le traitement des yaml en natif dans le contexte évoqué n'est toujours pas
 * nativement pris en charge en juin 2019, d'où ce code.
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {

        Properties propertiesFromYaml = loadYmamlIntoProperties(resource);
        String sourceName = name !=null ? name : resource.getResource().getFilename();

        return new PropertiesPropertySource(sourceName, propertiesFromYaml);
    }

    private Properties loadYmamlIntoProperties(EncodedResource resource) throws FileNotFoundException {

        try{
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return factory.getObject();

        }catch (IllegalStateException e){

            //Pour les ignoreResourceNotFound
            Throwable cause = e.getCause();
            if(cause instanceof FileNotFoundException)
                throw (FileNotFoundException)e.getCause();
            throw e;
        }
    }
}
