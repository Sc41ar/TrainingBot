package org.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

//микросервис, взаимодействующий с брокером
@SpringBootApplication
@PropertySource("classpath:application.properties")
public class NodeApp {
    public static void main(String[] args) throws TelegramApiException {
        SpringApplication springApplication = new SpringApplication(NodeApp.class);
        springApplication.run();
    }
}
