package com.zgamelogic.application;

import com.zgamelogic.controllers.NodeController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Import({NodeController.class})
@Slf4j
@EnableScheduling
public class App {
    public static void main(String[] args){
        SpringApplication app = new SpringApplication(App.class);
        app.run();
    }
}
