package org.edu;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@PropertySource("classpath:")
@EnableScheduling
public class RemainderApp {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RemainderApp.class);
        springApplication.run();
    }
}
