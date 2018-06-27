package io.bookup.config;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author woniper
 */
@Configuration
public class WebConfiguration {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowCredentials(true)
                        .allowedOrigins("*")
                        .allowedHeaders("*")
                        .allowedMethods(
                                Arrays.stream(HttpMethod.values())
                                .map(Enum::name)
                                .collect(Collectors.toList())
                                .toArray(new String[]{})
                        );
            }
        };
    }

}
