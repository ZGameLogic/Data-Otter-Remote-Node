package com.zgamelogic.data.monitors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class APIMonitor extends Monitor {

    private String url;
    private int port;
    private String healthCheckUrl;

}
