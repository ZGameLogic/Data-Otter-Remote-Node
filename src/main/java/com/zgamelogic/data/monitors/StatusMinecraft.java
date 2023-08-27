package com.zgamelogic.data.monitors;

import lombok.Getter;
import lombok.ToString;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@ToString
public class StatusMinecraft extends Status {

    private int max;
    private int online;
    private List<String> onlinePlayers;
    private String version;
    private String motd;

    public void update(JSONObject json){
        if(json == null){
            setStatus(false);
            return;
        }
        setStatus(true);
        max = json.getJSONObject("players").getInt("max");
        online = json.getJSONObject("players").getInt("online");
        if(online > 0) {
            JSONArray players = json.getJSONObject("players").getJSONArray("sample");
            onlinePlayers = new LinkedList<>();
            for (int i = 0; i < players.length(); i++) {
                onlinePlayers.add(players.getJSONObject(i).getString("name"));
            }
        }
        version = json.getJSONObject("version").getString("name");
        motd = json.getJSONObject("description").getString("text");
    }

    @Override
    public boolean softEquals(Object o){
        if (this == o) return true;
        if (!(o instanceof StatusMinecraft)) return false;
        StatusMinecraft that = (StatusMinecraft) o;
        return online == that.online && Objects.equals(onlinePlayers, that.onlinePlayers) && super.softEquals(o);
    }
}
