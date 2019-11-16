package com.company;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.util.Hashtable;
import java.util.Random;
import java.util.TreeMap;

public class Node implements SimpleKV {
    private Object key;
    private TreeMap<Integer, String> value;
    private int max_size;

    // This variable allows to control the miss or hit events
    private final Hashtable<String, Integer> log = new Hashtable<>();

    public Node(Object key, int max_size){
        this.key = key;
        this.max_size = max_size;
        this.value = new TreeMap<Integer, String>();
        // Initialize the miss and hit counter
        this.log.put("miss",0);
        this.log.put("hit",0);
    }

    @Override
    public void put(Object k, Object v) {
        if(value.size() > this.max_size){
            removeMax_Random();
        }
        value.put(Integer.valueOf(String.valueOf(k)), String.valueOf(v));
    }

    @Override
    public Object get(Object k) {
        return null;
    }

    @Override
    public int join(Object port) {
        int position = 1;
        String string_port = String.valueOf(port);
        HttpResponse<String> response = Unirest.get("http://127.0.0.1:"+7000+"/add/{port}")
                .routeParam("port", string_port)
                .asString();
        if(response.getStatus() == 200)
            position = Integer.valueOf(response.getBody());
        System.out.println(response.getBody());
        return position;
    }

    public void removeMax_Random() {
        Random r = new Random();
        Object[] keys = this.value.keySet().toArray();
        Object key = keys[r.nextInt(keys.length)];
        this.value.remove(key);
    }

    public void addHit(){
        this.log.put("hit", this.log.get("hit") + 1);
    }

    public void addMiss(){
        this.log.put("miss", this.log.get("miss") + 1);
    }

    public Hashtable getLog(){
        return this.log;
    }

    public void temporal(Object k, Object v, int next_node){
        HttpResponse<JsonNode> response = Unirest.post("http://127.0.0.1:"+next_node+"/")
                .header("accept", "application/json")
                .field("key", k)
                .field("value", "bar")
                .asJson();
        System.out.println(response);
    }
}
