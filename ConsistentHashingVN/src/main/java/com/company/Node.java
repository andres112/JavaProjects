package com.company;

import java.util.LinkedHashMap;

public class Node implements SimpleKV{
    private String addres;
    private int port;
    private LinkedHashMap<Object, Object> hashmap = new LinkedHashMap<>();

    public Node(String addres, int port, int max_size) {
        this.addres = addres;
        this.port = port;
    }

    @Override
    public void put(Object k, Object v) {
        this.hashmap.put(k,v);
    }

    @Override
    public Object get(Object k) {
        return this.hashmap.get(k);
    }

    public String getAddres(){
        return addres;
    }
    public int getPort(){
        return port;
    }
    public Object getHashMap(){
        return this.hashmap;
    }
}
