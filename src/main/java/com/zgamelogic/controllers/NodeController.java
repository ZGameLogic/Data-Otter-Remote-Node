package com.zgamelogic.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class NodeController {

    @Scheduled(cron = "0 */1 * * * *")
    private void oneMinuteTasks(){
        log.info("One minute task");
    }
}
