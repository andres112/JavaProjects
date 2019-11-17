package com.company;

import io.javalin.Javalin;
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
            Unirest.get("http://127.0.0.1:"+7000+"/join/{port}")
                    .routeParam("port", test.nextPort(positions)+"")
                    .asString();
        }
        System.out.println("\nRING");
        System.out.println("====");
        for(Map.Entry<Integer, Integer> item : positions.entrySet()) {
            System.out.println("position: " + item.getKey() + " Port: " + item.getValue());
        }

        // Print before ring accepts requests
        test.printResult(nodes);

        // Join a new node to the ring, requesting to boostrap node
        Unirest.get("http://127.0.0.1:"+7000+"/join/{port}")
                .routeParam("port", test.nextPort(positions)+"")
                .asString();

        // Print ring after new node was joined
        System.out.println("\nNEW RING");
        System.out.println("========");
        for(Map.Entry<Integer, Integer> item : positions.entrySet()) {
            System.out.println("position: " + item.getKey() + " Port: " + item.getValue());
        }

        // Putting data in nodes, according to the position in the ring
        System.out.println("\nPUT REQUESTS from node: 2,  port: 7001");
        System.out.println("============");
        for(int i = 1; i <= 25; i++){
            // Request with random keys
            int key = new Random().nextInt(50);
            System.out.println("key to add: "+key);
            Unirest.post("http://127.0.0.1:"+7001+"/")
                    .body("{\"key\":\""+ key +"\", \"value\":\"message_"+ i +"\"}")
                    .asString();
        }
        // print after REST POST requests
        test.printResult(nodes);

        // Getting elements from nodes, according to the position in the ring
        System.out.println("\nGET REQUESTS from node: 4,  port: 7003");
        System.out.println("============");
        for(int i = 1; i <= 50; i++){
            // Request with random keys
            int key = new Random().nextInt(50);
            System.out.println("key to get: "+key);
            Unirest.get("http://127.0.0.1:"+ 7003 +"/{key}")
                    .routeParam("key", String.valueOf(key))
                    .asString();
        }
        // print after REST GET requests
        test.printResult(nodes);

        // The requests of get or post could be excecuted from an external REST client so:
        // GET:  http://localhost:{PORT-NUMBER}/{KEY}
        // POST: http://localhost:{PORT-NUMBER} ===> body(key, value)
        // GET:  http://localhost:{BOOTSTRAP-PORT-NODE}/join/{NEW-PORT}
    }

    // Function to create the REST interfaces for each node created
    public void createRestInterface(Javalin item, NavigableMap<Integer,Integer> positions, List<Javalin> address, SortedMap<Integer,Node> nodes){
        item.get("/:key",
                ctx -> {
                    String key = ctx.pathParam("key");
                    Object port  = ctx.port();
                    ctx.json(getCache(key, port, positions, nodes));
                });
        item.post("/",
                ctx ->{
                    Map payload = ctx.bodyAsClass(Map.class);
                    Object key = payload.get("key");
                    Object value = payload.get("value");
                    Object port  = ctx.port();
                    setCache(key, value, port, positions, nodes);
                });
        item.get("/join/:port",
                ctx -> {
                    String port = ctx.pathParam("port");
                    joinRing(port, address, nodes, positions);
                });
        item.get("/add/:port",
                ctx -> {
                    String port = ctx.pathParam("port");
                    ctx.json(setPosition(port, positions, address, nodes));
                });
    }

    // Function to join a new node to the ring
    public void joinRing(String port, List<Javalin> address, SortedMap<Integer, Node>  nodes, NavigableMap<Integer,Integer> positions){
        System.out.println("Starting to join new node with port: "+ port);
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

    // Function to set a position to the new node by bootstrap node
    public int setPosition(Object port, NavigableMap<Integer, Integer> positions, List<Javalin> address, SortedMap<Integer, Node> nodes){
        int position = 1;
        if(positions.size() > 0) {
            Map.Entry<Integer, Integer> lastEntry = positions.lastEntry();
            position = lastEntry.getKey() + 1;
        }
        return position;
    }

    // Function to put an element in the node cache
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
        int modNode = k_number % positions.size();
        // If key mod ring size is 0 that correspond to the las node position
        if(modNode == 0)
            modNode = positions.size();
        if(modNode == current_position){
            nodes.get(current_position).put(k, v);
            nodes.get(current_position).addHit(current_position, positions.get(current_position));
            System.out.println("Element added to cache in node with port: "+port_number);
        }
        else {
            nodes.get(current_position).addMiss(current_position, positions.get(current_position));
            int next_node = nextNode(current_position, positions);
            nodes.get(current_position).requestNextNode(k,v,next_node);
        }
    }

    // Function to get the next position to assign to new node
    private int nextNode(int p, NavigableMap<Integer, Integer> positions){
        if( p == positions.size()){
            p = 1;
        }
        else{
            p += 1;
        }
        return positions.get(p);
    }

    // Function to get the next node in the node validation part
    private int nextPort(NavigableMap<Integer, Integer> positions){
        Map.Entry<Integer, Integer> lastEntry = positions.lastEntry();
        return lastEntry.getValue() + 1;
    }

    // Function to get the cache of a node
    public Object getCache(Object k, Object port, NavigableMap<Integer, Integer> positions, SortedMap<Integer, Node> nodes){
        String response = "Null";
        try {
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
            int modNode = k_number % positions.size();
            // If key mod ring size is 0 that correspond to the las node position
            if(modNode == 0)
                modNode = positions.size();
            if(modNode == current_position){
                if(nodes.get(current_position).getCache().containsKey(k_number)){
                    nodes.get(current_position).addHit(current_position, positions.get(current_position));
                    response = nodes.get(current_position).getCache().get(k_number);
                    System.out.println("Key "+ k_number+" found in cache of node with port: "+port_number);
                }
                else{
                    nodes.get(current_position).addMiss(current_position, positions.get(current_position));
                }
            }
            else {
                nodes.get(current_position).addMiss(current_position, positions.get(current_position));
                int next_node = nextNode(current_position, positions);
                nodes.get(current_position).getNextNode(k,next_node);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return response;
    }

    // Print log
    public void printResult(SortedMap<Integer, Node> nodes){
        System.out.println("****************************\nREPORT:\n");
        for(Map.Entry<Integer, Node> item : nodes.entrySet()) {
            System.out.println("key Node: " + item.getKey()+ " Log: "+item.getValue().getLog());
            System.out.println("Cache: "+item.getValue().getCache());
        }
    }
}
