package com.zgamelogic.data.monitors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Monitor {

    private int id;
    private String name;
    private String type;
    private LinkedList<Status> status;

    public void addStatus(Status status){
        this.status = new LinkedList<>();
        this.status.add(status);
    }

}
