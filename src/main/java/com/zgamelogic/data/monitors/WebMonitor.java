package com.zgamelogic.data.monitors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class WebMonitor extends Monitor {

    private String url;
    private int port;
    private String regex;

}
