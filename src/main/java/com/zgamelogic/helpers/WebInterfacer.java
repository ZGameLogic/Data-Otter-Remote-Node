package com.zgamelogic.helpers;

import com.zgamelogic.data.monitors.Status;
import com.zgamelogic.data.monitors.WebMonitor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;

public abstract class WebInterfacer {

    public static Status pingWeb(WebMonitor webMonitor) {
        Status mh = new Status();
        mh.setup();
        int tries = 0;
        while(tries < 3) {
            final String url = webMonitor.getUrl() + ":" + webMonitor.getPort();
            RestTemplate restTemplate = new RestTemplateBuilder()
                    .setConnectTimeout(Duration.ofSeconds(2))
                    .setReadTimeout(Duration.ofSeconds(2))
                    .build();
            try {
                String response = restTemplate.getForObject(new URI(url), String.class);
                mh.setStatus(response.contains(webMonitor.getRegex()));
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
