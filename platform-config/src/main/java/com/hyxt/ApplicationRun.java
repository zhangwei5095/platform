package com.hyxt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rocky on 14-5-29.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class ApplicationRun {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationRun.class , args);
    }

}
