package io.bookup;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Created by woniper on 2016. 2. 14..
 */
@SpringBootApplication
public class BookUPApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(BookUPApplication.class)
                .web(WebApplicationType.SERVLET);
        builder.build().addListeners(new ApplicationPidFileWriter("./bin/shutdown.pid"));
        builder.run();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}