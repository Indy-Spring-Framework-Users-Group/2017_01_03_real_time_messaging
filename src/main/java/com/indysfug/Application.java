package com.indysfug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author russell.scheerer
 */
@SpringBootApplication
@EnableOAuth2Sso
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    public static class WebConfig extends WebMvcConfigurerAdapter {

        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            configurer.setDefaultTimeout(1000000);
        }
    }
}
