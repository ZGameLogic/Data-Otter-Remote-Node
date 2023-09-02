package com.zgamelogic.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.zgamelogic.data.monitors.APIMonitor;
import com.zgamelogic.data.monitors.MinecraftMonitor;
import com.zgamelogic.data.monitors.Monitor;
import com.zgamelogic.data.monitors.WebMonitor;
import com.zgamelogic.data.nodes.Node;
import com.zgamelogic.helpers.APIInterfacer;
import com.zgamelogic.helpers.MCInterfacer;
import com.zgamelogic.helpers.WebInterfacer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;

@Controller
@Slf4j
public class NodeController {

    private static final String BASE_URL = "http://54.211.139.84:8080";

    private HashMap<String, Class> classMap;

    private Node node;

    @Scheduled(cron = "0 */1 * * * *")
    private void oneMinuteTasks(){
        if(node == null) registerNode();
        if(node == null) return;
        LinkedList<Thread> threads = new LinkedList<>();
        LinkedList<Monitor> monitors = getMonitorList();
        monitors.forEach(monitor -> {
            Thread thread = new Thread(() -> runMonitorPing(monitor));
            threads.add(thread);
            thread.start();
        });

        while(!threads.isEmpty()) threads.removeIf(thread -> !thread.isAlive());
        reportMonitors(monitors);
    }

    @PostConstruct
    private void init(){
        classMap = new HashMap<>();
        classMap.put("api", APIMonitor.class);
        classMap.put("minecraft", MinecraftMonitor.class);
        classMap.put("web", WebMonitor.class);
        registerNode();
        log.info("Node: " + node);
    }

    private void runMonitorPing(Monitor monitor){
        switch(monitor.getType()){
            case "minecraft":
                monitor.addStatus(MCInterfacer.pingServer((MinecraftMonitor) monitor));
                break;
            case "api":
                monitor.addStatus(APIInterfacer.pingAPI((APIMonitor) monitor));
                break;
            case "web":
                monitor.addStatus(WebInterfacer.pingWeb((WebMonitor) monitor));
                break;
        }
    }

    private void reportMonitors(LinkedList<Monitor> monitors){
//        monitors.forEach(monitor -> log.info(monitor.toString()));
        log.info("This is a eleventh test");
    }

    private void registerNode(){
        loadConfig();
        if(node != null) return;
        String url = BASE_URL + "/node/register";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            CloseableHttpResponse httpresponse = httpclient.execute(httpPost);
            if (httpresponse.getStatusLine().getStatusCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent()));
                ObjectMapper om = new ObjectMapper();
                node = om.readValue(in.readLine(), Node.class);
                saveConfig();
            } else {
                log.error("Error when registering node: " + httpresponse.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            log.error("Error when registering node", e);
        }
    }


    private void loadConfig(){
        File nodeFile = new File("node.json");
        if(!nodeFile.exists()) return;
        ObjectMapper om = new ObjectMapper();
        try {
            node = om.readValue(nodeFile, Node.class);
        } catch (IOException ignored) {}
    }

    private void saveConfig(){
        ObjectWriter ow = new ObjectMapper().writer(new DefaultPrettyPrinter());
        try {
            ow.writeValue(new File("node.json"), node);
        } catch (IOException ignored) {}
    }

    private LinkedList<Monitor> getMonitorList(){
        String url = BASE_URL + "/monitors";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        try {
            CloseableHttpResponse httpresponse = httpclient.execute(httpget);
            if (httpresponse.getStatusLine().getStatusCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent()));
                JSONArray jsonMonitors = new JSONArray(in.readLine());
                LinkedList<Monitor> monitors = new LinkedList<>();
                jsonMonitors.forEach(jsonMonitor -> {
                    ObjectMapper om = new ObjectMapper();
                    try {
                       monitors.add((Monitor) om.readValue(jsonMonitor.toString(), classMap.get(((JSONObject)jsonMonitor).getString("type"))));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
                return monitors;
            } else {
                log.error("Error when getting monitors list: " + httpresponse.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            log.error("Error when getting monitors list", e);
        }
        return new LinkedList<>();
    }
}
