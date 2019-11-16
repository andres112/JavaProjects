package com.company;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Consistency Hash algorithm without virtual nodes
 */
public class ConsistentHashing {

    private static Node node1 = new Node("127.0.0.1", 7000, 5);
    private static Node node2 = new Node("127.0.0.1", 7001, 5);
    private static Node node3 = new Node("127.0.0.1", 7002, 5);
    private static Node node4 = new Node("127.0.0.1", 7003, 5);

    //List of nodes to be added to the Hash ring
    private static Node[] servers = { node1, node2, node3, node4 };

    //key represents the hash value of the server and value represents the server
    private static SortedMap<Integer, Object> nodes = new TreeMap<>();

    //Program initialization, put all servers into nodes
    static {
        for (int i=0; i<servers.length; i++) {
//            int hash = getHash(servers[i].getAddres()+":"+servers[i].getPort());
            System.out.println("[" + servers[i].getAddres()+":"+servers[i].getPort() + "] Join the collection, his Hash The value is" + (i+1));
            nodes.put(i+1, servers[i]);
        }
        System.out.println();
    }

    private static void join(Node nodex){
        nodes.put(nodes.size(),nodex);
    }

    //Get the node that should be routed to
    private static Object getServer(String key) {
        // Address:Port
        String full_address;
        //Get the hash value of the key
        int hash = getHash(key);
        //Get all Map s that are larger than the Hash value
        SortedMap<Integer, Object> subMap = nodes.tailMap(hash);
        if(subMap.isEmpty()){
            //If there is no one larger than the hash value of the key, start with the first node
            Integer i = nodes.firstKey();
            Node current = (Node)nodes.get(i);
            current.put(hash, key);
            //Return to the corresponding server
            full_address = ((Node)nodes.get(i)).getAddres() +":"+ ((Node)nodes.get(i)).getPort();
            return full_address;
        }else{
            //The first Key is the nearest node clockwise past the node.
            Integer i = subMap.firstKey();
            //Return to the corresponding server
            full_address = ((Node)subMap.get(i)).getAddres() +":"+ ((Node)subMap.get(i)).getPort();
            return full_address;
        }
    }

    //Using FNV1_32_HASH algorithm to calculate the Hash value of the server, there is no need to rewrite hashCode method, the final effect is no difference.
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // If the calculated value is negative, take its absolute value.
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    public static void main(String[] args) {
        String[] keys = {"a", "b", "c", "a", "e", "d"};
        for(int i=0; i<keys.length; i++)
            System.out.println("[" + keys[i] + "]Of hash The value is" + getHash(keys[i])
                    + ", Routed to Node[" + getServer(keys[i]) + "]");
    }
}
