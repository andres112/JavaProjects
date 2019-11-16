package com.company;

import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.*;

public class Main{
    public static final int BS_NODE = 7000;

    public static void main(String[] args) {

        List<Javalin> address = new ArrayList<>();
        SortedMap<Integer, Node> nodes = new TreeMap<>();
        NavigableMap<Integer,Integer> positions = new TreeMap<Integer,Integer>();

        Main test = new Main();

        // Bootstrap Node
        address.add(Javalin.create().start(BS_NODE));

        // Generate REST interfaces for Bootstrap Node
        for(Javalin item: address){
            test.createRestInterface(item, positions, address, nodes);
        }

        // Create Bootstrap Node
        nodes.put(1, new Node(BS_NODE, 5));

        // Give position to the Bootstrap Node
        int position = nodes.get(1).join(BS_NODE);
        positions.put(position, BS_NODE);

        // Join 4 elements to the ring
        for(int i = 1; i < 4; i++){
            int port = BS_NODE + i;
            Unirest.get("http://127.0.0.1:"+7000+"/join/{port}")
                    .routeParam("port", port+"")
                    .asString();
        }

        for(Map.Entry<Integer, Integer> item : positions.entrySet())
            System.out.println("position: "+ item.getKey()+" Port: "+ item.getValue());
        for(Map.Entry<Integer, Node> item : nodes.entrySet()) {
            System.out.println("key Node: " + item.getKey()+ "Log: "+item.getValue().getLog());
        }

        test.setCache("3","hello", "7001", positions, nodes);
        for(Map.Entry<Integer, Node> item : nodes.entrySet()) {
            System.out.println("key Node: " + item.getKey()+ "Log: "+item.getValue().getLog());
        }
    }

    public void createRestInterface(Javalin item, NavigableMap<Integer,Integer> positions, List<Javalin> address, SortedMap<Integer,Node> nodes){
        item.get("/join/:port",
                ctx -> {
                    String port = ctx.pathParam("port");
                    joinRing(port, address, nodes, positions);
                });
        item.post("/",
                ctx ->{
                    Map payload = ctx.bodyAsClass(Map.class);
                    Object key = payload.get("key");
                    Object value = payload.get("value");
                    Object port  = ctx.port();
                    setCache(key, value, port, positions, nodes);
                    ctx.json("moved");
                });
        item.get("/add/:port",
                ctx -> {
                    String port = ctx.pathParam("port");
                    ctx.json(setPosition(port, positions, address, nodes));
                });
    }

    public void joinRing(String port, List<Javalin> address, SortedMap<Integer, Node>  nodes, NavigableMap<Integer,Integer> positions){
        int position = nodes.get(1).join(port);
        int port_number = Integer.valueOf(port);
        Node node = new Node(port, 5);
        nodes.put(position, node);
        positions.put(position, port_number);

        address.add(Javalin.create().start(port_number));
        for(Javalin item: address){
            if(item.port() == port_number)
                createRestInterface(item, positions, address, nodes);
        }
    }

    public int setPosition(Object port, NavigableMap<Integer, Integer> positions, List<Javalin> address, SortedMap<Integer, Node> nodes){
        int position = 1;
        if(positions.size() > 0) {
            Map.Entry<Integer, Integer> lastEntry = positions.lastEntry();
            position = lastEntry.getKey() + 1;

        }
        return position;
    }

    public void setCache(Object k, Object v, Object port, NavigableMap<Integer, Integer> positions, SortedMap<Integer, Node> nodes){
        int k_number = Integer.valueOf(String.valueOf(k));
        int current_position = 1;
        int port_number = Integer.valueOf(String.valueOf(port));
        for(Map.Entry<Integer, Integer> item : positions.entrySet()){
            if(port_number == item.getValue()){
                current_position = item.getKey();
                break;
            }
        }

        // Node validation
        if(k_number % current_position == 0){
            nodes.get(current_position).put(k, v);
            nodes.get(current_position).addHit();
        }
        else {
            nodes.get(current_position).addMiss();
            int next_node = nextNode(current_position, positions);
            nodes.get(current_position).temporal(k,v,next_node);
        }
    }

    private int nextNode(int p, NavigableMap<Integer, Integer> positions){
        if( p == positions.size()){
            p = 1;
        }
        else{
            p += 1;
        }
        return positions.get(p);
    }
}
