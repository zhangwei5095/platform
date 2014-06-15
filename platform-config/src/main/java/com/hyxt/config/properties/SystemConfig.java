package com.hyxt.config.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

/**
 * Created by rocky on 14-6-5.
 */
@Configuration
public class SystemConfig {

    @Autowired
    private Environment environment;

    public <T> T getValue(String name, Class<T> clz) {
       return environment.getProperty(name , clz);
    }

    public String getValue(String name) {
        return environment.getProperty(name);
    }
}
