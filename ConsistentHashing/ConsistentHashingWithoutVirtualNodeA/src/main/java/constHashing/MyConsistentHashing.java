package constHashing;
import io.javalin.Javalin;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.server.Response;
import java.util.*;
import org.apache.log4j.Logger;

public class MyConsistentHashing<K, V> implements SimpleKV2<K, V> {

    //Add any number of nodes and ports here
    private static String[] servers = { "http://localhost:7000", "http://localhost:7001", "http://localhost:7002", "http://localhost:7003", "http://localhost:7004"};
    private static int[] ports = {7000, 7001, 7002, 7003, 7004};
    private static Logger logger = Logger.getLogger(MyConsistentHashing.class);
    public static class address <Integer, String> {
        Integer key;
        String  value;
    }

    static class Node<T, U> {
        MyConsistentHashing.Node<T, U> previous;
        MyConsistentHashing.Node<T, U> next;
        T key;
        U value;

        Node(MyConsistentHashing.Node<T, U> previous, MyConsistentHashing.Node<T, U> next, T key, U value){
            this.previous = previous;
            this.next = next;
            this.key = key;
            this.value = value;
        }

    }

    private HashMap<Integer, HashMap<K, MyConsistentHashing.Node<K, V>>> listCaches = new HashMap<>();

    private static List<address> listAddresses = new ArrayList<>();
    private static SortedMap<Integer, String> sortedMap = new TreeMap<>();
     {
        for (int i=0; i<servers.length; i++) {
            Integer hash = getHash(servers[i]);
            System.out.println("Node " + servers[i] + " has joined the ring. It's value is " + hash);
            sortedMap.put(hash, servers[i]);
            HashMap<K, Node<K, V>> cache = new HashMap<>();
            listCaches.put(hash, cache);
            address addrs = new address<>();
            addrs.key = hash;
            addrs.value = servers[i];
            listAddresses.add(addrs);
        }
        System.out.println();
    }

    private HashMap<K, MyConsistentHashing.Node<K, V>> lruCache;

    private MyConsistentHashing.Node<K, V> leastRecentlyUsed;
    private MyConsistentHashing.Node<K, V> mostRecentlyUsed;
    private int maxSize;
    private int currentSize;

    private MyConsistentHashing(int maxSize){
        this.maxSize = maxSize;
        this.currentSize = 0;
        leastRecentlyUsed = new MyConsistentHashing.Node<>(null, null, null, null);
        mostRecentlyUsed = leastRecentlyUsed;
        lruCache = new HashMap<>();
    }

    private static String getServer(String key) {
        int hash = getHash(key);
        SortedMap<Integer, String> subMap = sortedMap.tailMap(hash);
        if(subMap.isEmpty()){
            Integer i = sortedMap.firstKey();
            return sortedMap.get(i);
        }else{
            Integer i = subMap.firstKey();
            return subMap.get(i);
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

    public V getLRU(K key, Integer hash){

        HashMap<K, MyConsistentHashing.Node<K, V>> HM;
        HM = listCaches.get(hash);
        MyConsistentHashing.Node<K, V> tempNode = HM.get(key);
        if (tempNode == null){
            return null;
        }
        // If MRU leave the list as it is
        else if (tempNode.key == mostRecentlyUsed.key){
            return mostRecentlyUsed.value;
        }

        // Get the next and previous nodes
        MyConsistentHashing.Node<K, V> nextNode = tempNode.next;
        MyConsistentHashing.Node<K, V> previousNode = tempNode.previous;

        // If at the left-most, we update LRU
        if (tempNode.key == leastRecentlyUsed.key){
            nextNode.previous = null;
            leastRecentlyUsed = nextNode;
        }

        // If we are in the middle, we need to update the items before and after our item
        else if (tempNode.key != mostRecentlyUsed.key){
            if(previousNode != null)
            previousNode.next = nextNode;
            if(nextNode != null)
            nextNode.previous = previousNode;
        }

        // Finally move our item to the MRU
        tempNode.previous = mostRecentlyUsed;
        mostRecentlyUsed.next = tempNode;
        mostRecentlyUsed = tempNode;
        mostRecentlyUsed.next = null;
        return tempNode.value;

    }

    public void putLRU(K key, V value, Integer hash){
        String serverName = "";
        for(int i=0; i<listAddresses.size(); i++) {
            if ((Integer) listAddresses.get(i).key == hash.intValue()) {
                serverName = listAddresses.get(i).value.toString();
                i = listAddresses.size();
            }
        }
        lruCache = listCaches.get(hash);

        if (lruCache.containsKey(key)){
            return;
        }
        // Put the new node at the right-most end of the linked-list
        MyConsistentHashing.Node<K, V> myNode = new MyConsistentHashing.Node<>(mostRecentlyUsed, null, key, value);
        mostRecentlyUsed.next = myNode;
      //  sortedMap.tailMap(hash).put(key, myNode);
        lruCache.put(key, myNode);
        listCaches.put(hash, lruCache);
        mostRecentlyUsed = myNode;

        // Delete the left-most entry and update the LRU pointer
        if (currentSize == maxSize){
            lruCache.remove(leastRecentlyUsed.key);
            listCaches.put(hash, lruCache);
            leastRecentlyUsed = leastRecentlyUsed.next;
            leastRecentlyUsed.previous = null;
        }

        // Update cache size, for the first added entry update the LRU pointer
        else if (currentSize < maxSize){
            if (currentSize == 0){
                leastRecentlyUsed = myNode;
            }
            currentSize++;
        }
        //Print the server to which the key is stored
        //System.out.println("Entries inserted in " + serverName);
    }

    public static void MyTest(Boolean isLRU, MyConsistentHashing cache) {
        long averageTime = 0;
        for (int count = 0; count < servers.length; count++) {
            String server = "";
            long startTime = System.nanoTime();
            for (int i = 0; i < cache.maxSize; i++) {
                Integer randomNumber = (int) Math.ceil(Math.random() * cache.maxSize);
               //  server = getServer(String.valueOf(randomNumber));
                server = servers[count];
                Integer hash = getHash(server);
                cache.putLRU(String.valueOf(randomNumber), randomNumber, hash);
            }
            long entTime = System.nanoTime();
            long totalTime = (entTime - startTime) / 1000000L;
            averageTime += totalTime;
            System.out.println("Entries inserted in to " + server + " in " + totalTime + " ms");
        }
        System.out.println("With LRU eviction, the average time for PUT is " + (averageTime / servers.length) + " ms\n");

        averageTime = 0;
        for (int count = 0; count < servers.length; count++) {
            String server = "";
            long startTime = System.nanoTime();
            for (int i = 0; i < cache.maxSize; i++) {
                Integer randomNumber = (int) Math.ceil(Math.random() * cache.maxSize);
                server = servers[count];
                Integer hash = getHash(server);
                cache.getLRU(String.valueOf(randomNumber), hash);
            }
            long entTime = System.nanoTime();
            long totalTime = (entTime - startTime) / 1000000L;
            averageTime += totalTime;
            System.out.println("Entries retrieved from " + server + " in " + totalTime + " ms");
        }
        System.out.println("With LRU eviction, the average time for GET is " + (averageTime / servers.length) + " ms\n");
    }
    public  static  void main(String[] args) {
        int cacheSize = 10000;

        MyConsistentHashing cache = new MyConsistentHashing(cacheSize);

        MyConsistentHashing.MyTest(Boolean.TRUE, cache);
//        Javalin app = null;
        for(int p=0; p< ports.length;p++){
            Javalin app = Javalin.create().start(ports[p]);
            System.out.println("Server http://localhost:" + ports[p] + "/ joined the ring.");
            System.out.println("Number of Nodes in the ring: " + (p+1));
            Integer hash = getHash(servers[p]);
            app.post("/post",
                    ctx -> ctx.status(MyConsistentHashing.put(cache, hash)));
            app.get("/get",
                    ctx -> ctx.json(MyConsistentHashing.get(cache, hash))
            );

        }

//        app.post("/post",
//                ctx -> ctx.status(MyConsistentHashing.put(cache)));
//        app.get("/get",
//                ctx -> ctx.json(MyConsistentHashing.get(cache))
//        );
//               app.post("/post", consistentHash::put);
//        app.post("/", ctx -> {
//            ctx.status(201);
//        });
//        app.post("/post", ctx -> {
//            consistentHash.put(cache);
//            ctx.status(201);
//        });
    }

    public  static Object get(MyConsistentHashing cache, Integer hash){
        ArrayList<Object> result = new ArrayList<>(cache.lruCache.size());
        for (int i = 0; i < cache.lruCache.size(); i++) {
            Integer randomNumber = (int) Math.ceil(Math.random() * cache.lruCache.size());
            if(cache.getLRU(String.valueOf(randomNumber), hash) != null)
                result.add(cache.getLRU(String.valueOf(randomNumber), hash));
            else {
                String serverName = "";
                for(int l=0; l<listAddresses.size(); l++) {
                    if ((Integer) listAddresses.get(l).key == hash.intValue()) {
                        serverName = listAddresses.get(l).value.toString();
                        l = listAddresses.size();
                    }
                }
                //Configure logger
                BasicConfigurator.configure();
                logger.info("Cache miss at Server "+ serverName);
            }
        }
        return result;
    }

    public  static int put(MyConsistentHashing cache, Integer hash){
        ArrayList<Object> result = new ArrayList<>(cache.lruCache.size());
        for (int i = 0; i < cache.lruCache.size(); i++) {
            Integer randomNumber = (int) Math.ceil(Math.random() * cache.lruCache.size());
            cache.putLRU(String.valueOf(randomNumber), randomNumber, hash);
        }
        return Response.SC_CREATED;
    }
}
