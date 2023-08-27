package com.zgamelogic.helpers;

import com.zgamelogic.data.monitors.APIMonitor;
import com.zgamelogic.data.monitors.Status;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;

public abstract class APIInterfacer {

    public static Status pingAPI(APIMonitor apiMonitor){
        Status mh = new Status();
        mh.setup();
        int tries = 0;
        while(tries < 3) {
            final String url = apiMonitor.getUrl() + ":" + apiMonitor.getPort() + "/" + apiMonitor.getHealthCheckUrl();
            RestTemplate restTemplate = new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(2)).setReadTimeout(Duration.ofSeconds(2)).build();
            try {
                String response = restTemplate.getForObject(new URI(url), String.class);
                mh.setStatus(response.toLowerCase().contains("health"));
                return mh;
            } catch (Exception e) {
                tries++;
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        mh.setStatus(false);
        return mh;
    }

}
