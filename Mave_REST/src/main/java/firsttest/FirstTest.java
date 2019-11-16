package firsttest;

import io.javalin.Javalin;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FirstTest {

    // General configuration
    // NUMBER_OF_ELEMENTS: The quantity of elements to generate randomly and put in the hashmap.
    // numberOfQueries: The quantity of queries that getByOne_Benchmark and removeByOne_Benchmark functions execute with randomly keys.
    static final long NUMBER_OF_ELEMENTS = 1000;
    private int numberOfQueries = 100;

    // Specific Configuration 
    // XX_MAXLENGTH: Max length allowed to put elements, after that, the algortihm remove randomly elements to add the new one.
    //               When this parameter is 0 means without limit
    // XX_REDUNDANCY: The number of tries that the set_Benchmark should attemp to add a key/value if this already exists
    // XX_REMOVE_PER: The percentage of elements to remove accoriding to the hashmap object size (not the XX_MAXLENGTH)
    //                The value should be between 0 and 100
    static final long CH_MAX_LENGTH = 100;
    static final int CH_REDUNDANCY = 0;
    static final int CH_REMOVE_PER = 0;
    static final long TH_MAX_LENGTH = 100;
    static final int TH_REDUNDANCY = 0;
    static final int TH_REMOVE_PER = 0;
    static final long LH_MAX_LENGTH =100;
    static final int LH_REDUNDANCY = 0;
    static final int LH_REMOVE_PER = 0;

    // Benchmark variables
    private long startTime, entTime, totalTime;
    // Object that will keep the results
    private final TreeMap<String, Object[]> results = new TreeMap<>();

    //Main execution
    public static void main(String[] args) {

        Javalin app = Javalin.create().start(7000);
        app.get("/", ctx -> ctx.result("Hello World"));

        // Instance of test class for execution
        FirstTest test = new FirstTest();

        // Creation of instance to each hashmap object
        // Execution of methods to gauge the benchmarks

        ConcurrentHash c_table = new ConcurrentHash(CH_MAX_LENGTH);
        test.set_Benchmark(c_table, "false");

        TreeHash r_table = new TreeHash(TH_MAX_LENGTH);
        test.set_Benchmark(r_table, "false");;

        LinkedHash l_table = new LinkedHash(LH_MAX_LENGTH);
        test.set_Benchmark(l_table, "false");

        // Print the results of the whole test
        test.printResults();

        // HTTP Routes
        app.get("/getall/:name",
                ctx -> {
                    Object currentHM = null;
                    switch (ctx.pathParam("name").toLowerCase().trim()) {
                        case "concurrent":
                            currentHM = c_table;
                            break;
                        case "tree":
                            currentHM = r_table;
                            break;
                        default:
                            currentHM = l_table;
                            break;
                    }
                    ctx.json(test.getAll_Benchmark(currentHM));
                });

        app.get("/get/:name",
                ctx -> {
                    Object currentHM = null;
                    switch (ctx.pathParam("name").toLowerCase().trim()) {
                        case "concurrent":
                            currentHM = c_table;
                            break;
                        case "tree":
                            currentHM = r_table;
                            break;
                        default:
                            currentHM = l_table;
                            break;
                    }
                    Object key = ctx.queryParam("key");
                    ctx.json(test.getByOne_Benchmark(currentHM, key));
                });

        app.get("/getrandom/:name",
                ctx -> {
                    Object currentHM = null;
                    switch (ctx.pathParam("name").toLowerCase().trim()) {
                        case "concurrent":
                            currentHM = c_table;
                            break;
                        case "tree":
                            currentHM = r_table;
                            break;
                        default:
                            currentHM = l_table;
                            break;
                    }
                    Object queries = ctx.queryParam("q");
                    ctx.json(test.getRandom_Benchmark(currentHM, queries));
                });

        app.post("/setall/:name",
                ctx ->{
                    Object currentHM = null;
                    Map payload = ctx.bodyAsClass(Map.class);
                    switch (ctx.pathParam("name").toLowerCase().trim()) {
                        case "concurrent":
                            currentHM = c_table;
                            break;
                        case "tree":
                            currentHM = r_table;
                            break;
                        default:
                            currentHM =  l_table;
                            break;
                    }
                    Object lru = payload.getOrDefault("lru", false);
                    ctx.json(test.set_Benchmark(currentHM, lru));
                });

        app.post("/set/:name",
                ctx ->{
                    Object currentHM = null;
                    Map payload = ctx.bodyAsClass(Map.class);
                    switch (ctx.pathParam("name").toLowerCase().trim()) {
                        case "concurrent":
                            currentHM = c_table;
                            break;
                        case "tree":
                            currentHM = r_table;
                            break;
                        default:
                            currentHM = l_table;
                            break;
                    }
                    Object key = payload.get("key");
                    Object lru = payload.getOrDefault("lru", false);
                    ctx.json(test.setByOne_Benchmark(currentHM, key, lru));
                });

        app.delete("/remove/:name",
                ctx ->{
                    Object currentHM = null;
                    Map payload = ctx.bodyAsClass(Map.class);
                    switch (ctx.pathParam("name").toLowerCase().trim()) {
                        case "concurrent":
                            currentHM = c_table;
                            break;
                        case "tree":
                            currentHM = r_table;
                            break;
                        default:
                            currentHM = l_table;
                            break;
                    }
                    Object key = payload.get("key");
                    ctx.json(test.removeByOne_Benchmark(currentHM, key));
                });

        app.get("/results",
                ctx -> ctx.json(test.printResults()));
    }

    // Method to get string randomly
    private String get_RandomString(int len) {
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(len, useLetters, useNumbers);
        return generatedString;
    }

    // Method to set the elements in the hashmap
    public Object set_Benchmark(Object hm, Object lru) {
        long finalLen = 0;
        System.out.println("Starting: Creating: " + hm.getClass().toString());
        startTime = System.nanoTime();
        try {
            int ctrl = 0;
            for (int i = 0; i < NUMBER_OF_ELEMENTS; i++) {
                Object key = new Key(get_RandomString(3), 0);
                Object value = get_RandomString(10);
                if (hm instanceof ConcurrentHash) {
                    ConcurrentHash hashMap = (ConcurrentHash) hm;
                    if (hashMap.containsKey(key) && ctrl < CH_REDUNDANCY) {
                        i--;
                        ctrl++;
                    } else {
                        hashMap.put(key, value, lru);
                        ctrl = 0;
                    }
                    finalLen = hashMap.getSize();
                }
                if (hm instanceof TreeHash) {
                    TreeHash hashMap = (TreeHash) hm;
                    if (hashMap.containsKey(key) && ctrl < TH_REDUNDANCY) {
                        i--;
                        ctrl++;
                    } else {
                        hashMap.put(key, value, lru);
                        ctrl = 0;
                    }
                    finalLen = hashMap.getSize();
                }
                if (hm instanceof LinkedHash) {
                    LinkedHash hashMap = (LinkedHash) hm;
                    if (hashMap.containsKey(key) && ctrl < LH_REDUNDANCY) {
                        i--;
                        ctrl++;
                    } else {
                        hashMap.put(key, value, lru);
                        ctrl = 0;
                    }
                    finalLen = hashMap.getSize();
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        entTime = System.nanoTime();
        totalTime = (entTime - startTime) / 1000000L;
        this.results.put(hm.getClass().toString() + "/create", new Object[]{totalTime, "NA", finalLen, "NA"});
        System.out.println("Executed in in " + totalTime + " ms \n***********************\n");
        HashMap<Object, Object> result = new HashMap<>();
        result.put("type", hm.getClass().toString());
        result.put("total_time",totalTime);
        result.put("size", finalLen);
        return  result;
    }

    // Method to set one element in the hashmap random value
    public Object setByOne_Benchmark(Object hm, Object keyId, Object lru) {
        Object value = get_RandomString(10);
        long finalLen = 0;
        Key key = null;
        System.out.println("Starting: Setting By One: " + hm.getClass().toString());
        startTime = System.nanoTime();
        if (keyId != null) {
            key = new Key(String.valueOf(keyId), 0);
            try {
                if (hm instanceof ConcurrentHash) {
                    ConcurrentHash hashMap = (ConcurrentHash) hm;
                    if (!hashMap.containsKey(key)) {
                        hashMap.put(key, value, lru);
                    }
                    finalLen = hashMap.getSize();
                }
                if (hm instanceof TreeHash) {
                    TreeHash hashMap = (TreeHash) hm;
                    if (!hashMap.containsKey(key)) {
                        hashMap.put(key, value, lru);
                    }
                    finalLen = hashMap.getSize();
                }
                if (hm instanceof LinkedHash) {
                    LinkedHash hashMap = (LinkedHash) hm;
                    if (!hashMap.containsKey(key)) {
                        hashMap.put(key, value, lru);
                    }
                    finalLen = hashMap.getSize();
                }
            } catch(Exception e){
                System.out.println("Error: " + e);
            }
        }
        entTime = System.nanoTime();
        totalTime = (entTime - startTime) / 1000000L;
        this.results.put(hm.getClass().toString() + "/getByOne", new Object[]{totalTime, "NA", finalLen, "NA"});
        System.out.println("Executed in in " + totalTime + " ms \n***********************\n");
        HashMap<Object, Object> result = new HashMap<>();
        result.put("type", hm.getClass().toString());
        result.put("total_time",totalTime);
        result.put("size", finalLen);
        result.put("key",key);
        result.put("value",value);
        return result;
    }

    // Method to get all elements of the hashmap
    public Object getAll_Benchmark(Object hashmap) {
        Object hmObject = " ";
        System.out.println("Starting: Iterating: " + hashmap.getClass().toString());
        startTime = System.nanoTime();
        try {
            if (hashmap instanceof ConcurrentHash) {
                ConcurrentHash hashMap = (ConcurrentHash) hashmap;
                ConcurrentHashMap<Object, Object> current = (ConcurrentHashMap<Object, Object>) hashMap.getAll();
                hmObject = current;
            }
            if (hashmap instanceof TreeHash) {
                TreeHash hashMap = (TreeHash) hashmap;
                TreeMap<Object, Object> current = (TreeMap<Object, Object>) hashMap.getAll();
                hmObject = current;
            }
            if (hashmap instanceof LinkedHash) {
                LinkedHash hashMap = (LinkedHash) hashmap;
                LinkedHashMap<Object, Object> current = (LinkedHashMap<Object, Object>) hashMap.getAll();
                hmObject = current;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        entTime = System.nanoTime();
        totalTime = (entTime - startTime) / 1000000L;
        this.results.put(hashmap.getClass().toString() + "/getAll", new Object[]{totalTime, "NA", "NA", "NA"});
        System.out.println("Executed in in " + totalTime + " ms \n***********************\n");

        return getResult(hmObject);
    }

    // Method to get elements from the hashmap random keys
    public Object getByOne_Benchmark(Object hm, Object key) {
        Object value = null;
        System.out.println("Starting: Getting By One: " + hm.getClass().toString());
        int ctr = 0;
        startTime = System.nanoTime();
        if (key != null) {
            try {
                if (hm instanceof ConcurrentHash) {
                    ConcurrentHash hashMap = (ConcurrentHash) hm;
                    value = hashMap.get(key);
                    if (value != null) {
                        ctr++;
                    }
                }
                if (hm instanceof TreeHash) {
                    TreeHash hashMap = (TreeHash) hm;
                    value = hashMap.get(key);
                    if (value != null) {
                        ctr++;
                    }
                }
                if (hm instanceof LinkedHash) {
                    LinkedHash hashMap = (LinkedHash) hm;
                    value = hashMap.get(key);
                    if (value != null) {
                        ctr++;
                    }
                }
            } catch(Exception e){
                System.out.println("Error: " + e);
            }
        }
        entTime = System.nanoTime();
        totalTime = (entTime - startTime) / 1000000L;
        this.results.put(hm.getClass().toString() + "/getByOne", new Object[]{totalTime, "NA", "NA", ctr});
        System.out.println("Executed in in " + totalTime + " ms \n***********************\n");

        return getResult(value);
    }

    // Method to get elements from the hashmap random keys
    public Object getRandom_Benchmark(Object hm, Object queries) {
        try {
            this.numberOfQueries =  Integer.valueOf(String.valueOf(queries));
        }
        catch (Exception e){
            System.out.println("Error: " + e);
        }

        List<Object> responses = new ArrayList<>();
        int ctr = 0;
        System.out.println("Starting: Getting By One Randomly: " + hm.getClass().toString());

        startTime = System.nanoTime();
        try {

            for (int i = 0; i < numberOfQueries; i++) {
                // generate randomly an string and cast to generic Object
                Object key = get_RandomString(3);
                if (hm instanceof ConcurrentHash) {
                    ConcurrentHash hashMap = (ConcurrentHash) hm;
                    ConcurrentHashMap<Object, Object> value = (ConcurrentHashMap<Object, Object>) hashMap.get(key);
                    if (value.size() > 0) {
                        responses.add(value);
                        ctr++;
                    }
                }
                if (hm instanceof TreeHash) {
                    TreeHash hashMap = (TreeHash) hm;
                    TreeMap<Object, Object> value = (TreeMap<Object, Object>) hashMap.get(key);
                    if (value.size() > 0) {
                        responses.add(value);
                        ctr++;
                    }
                }
                if (hm instanceof LinkedHash) {
                    LinkedHash hashMap = (LinkedHash) hm;
                    LinkedHashMap<Object, Object> value = (LinkedHashMap<Object, Object>) hashMap.get(key);
                    if (value.size() > 0) {
                        responses.add(value);
                        ctr++;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        entTime = System.nanoTime();
        totalTime = (entTime - startTime) / 1000000L;
        this.results.put(hm.getClass().toString() + "/getByOne", new Object[]{totalTime, "NA", "NA", ctr});
        System.out.println("Executed in in " + totalTime + " ms \n***********************\n");

        return responses;
    }

    // Method to remove elements of the hashmap with random keys
    public Object removeByOne_Benchmark(Object hm, Object key) {
        long finalLen = 0, initialLen = 0;
        System.out.println("Starting: Removing By One: " + hm.getClass().toString());
        int ctr = 0;
        Object value = null;
        startTime = System.nanoTime();
        if (key != null) {
            try {
                if (hm instanceof ConcurrentHash) {
                    ConcurrentHash hashMap = (ConcurrentHash) hm;
                    initialLen = hashMap.getSize();
                    value = hashMap.remove(key);
                    if (value != null) {
                        ctr++;
                    }
                    finalLen = hashMap.getSize();
                }
                if (hm instanceof TreeHash) {
                    TreeHash hashMap = (TreeHash) hm;
                    value = hashMap.remove(key);
                    if (value != null) {
                        ctr++;
                    }
                    finalLen = hashMap.getSize();
                }
                if (hm instanceof LinkedHash) {
                    LinkedHash hashMap = (LinkedHash) hm;
                    value = hashMap.remove(key);
                    if (value != null) {
                        ctr++;
                    }
                    finalLen = hashMap.getSize();
                }
            } catch(Exception e){
                System.out.println("Error: " + e);
            }
        }
        entTime = System.nanoTime();
        totalTime = (entTime - startTime) / 1000L;
        this.results.put(hm.getClass().toString() + "/removeByOne", new Object[]{totalTime, initialLen, finalLen, ctr});
        System.out.println("Executed in in " + totalTime + " us \n***********************\n");

        // response creation
        HashMap<Object, Object> result = new HashMap<>();
        if(value == null){
            value = "404 Not Found";
        }
        result.put("key",key);
        result.put("value",value);
        result.put("initial_size", initialLen);
        result.put("final_size", finalLen);
        return result;
    }

    // Method to remove a percentage of elements of the hashmap
    public void removeByPercentage_Benchmark(Object hashmap, int p) {
        long finalLen = 0, initialLen = 0;
        System.out.println("Starting: Removing By Percentage: " + hashmap.getClass().toString());
        startTime = System.nanoTime();
        try {
            if (p >= 0 && p <= 100) {
                if (hashmap instanceof ConcurrentHash) {
                    ConcurrentHash hashMap = (ConcurrentHash) hashmap;
                    initialLen = hashMap.getSize();
                    for (int i = 0; i < initialLen * p / 100; i++) {
                        hashMap.removeMax_Random();
                    }
                    finalLen = hashMap.getSize();
                }

                if (hashmap instanceof TreeHash) {
                    TreeHash hashMap = (TreeHash) hashmap;
                    initialLen = hashMap.getSize();
                    for (int i = 0; i < initialLen * p / 100; i++) {
                        hashMap.removeMax_Random();
                    }
                    finalLen = hashMap.getSize();
                }
                if (hashmap instanceof LinkedHash) {
                    LinkedHash hashMap = (LinkedHash) hashmap;
                    initialLen = hashMap.getSize();
                    for (int i = 0; i < initialLen * p / 100; i++) {
                        hashMap.removeMax_Random();
                    }
                    finalLen = hashMap.getSize();
                }
            } else {
                throw new IllegalArgumentException("Percentage must be between 0 and 100");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        entTime = System.nanoTime();
        totalTime = (entTime - startTime) / 1000000L;
        this.results.put(hashmap.getClass().toString() + "/removeByPercentage", new Object[]{totalTime, initialLen, finalLen, "NA"});
        System.out.println("Executed in in " + totalTime + " ms \n***********************\n");
    }

    public Object printResults() {
        System.out.println("Data configuration: "
                + "\n * Number of elements by HashMap: " + NUMBER_OF_ELEMENTS
                + "\n * Number of queries: " + numberOfQueries
                + "\n");
        for (String key : this.results.keySet()) {
            String unit = "ms";
            if (key.contains("get") || key.contains("removeByOne")) {
                unit = "us";
            }
            System.out.println(key.split("\\.")[1] + ":\n"
                    + "* Time: " + this.results.get(key)[0] + " " + unit + ".  -  "
                    + "* Initial Size: " + this.results.get(key)[1] + "  -  "
                    + "* Final Size: " + this.results.get(key)[2] + "  -  "
                    + "* No. Matchs: " + this.results.get(key)[3]);
        }
        return this.results;
    }

    private Object getResult(Object hmObject){
        HashMap<Object, Object> result = new HashMap<>();
        Key keyId = null;

        if (hmObject instanceof ConcurrentHashMap) {
            ConcurrentHashMap<Object, Object> hashMap = (ConcurrentHashMap) hmObject;
            for(Map.Entry<Object, Object> item : hashMap.entrySet()){
                keyId = (Key) item.getKey();
                result.put("{key: "+keyId.getId()+", weight: "+ keyId.getWeight()+"}", String.valueOf(item.getValue()));
            }
        }
        if (hmObject instanceof TreeMap) {
            TreeMap<Object, Object> hashMap = (TreeMap) hmObject;
            for(Map.Entry<Object, Object> item : hashMap.entrySet()){
                keyId = (Key) item.getKey();
                result.put("{key: "+keyId.getId()+", weight: "+ keyId.getWeight()+"}", String.valueOf(item.getValue()));
            }
        }
        if (hmObject instanceof LinkedHashMap) {
            LinkedHashMap<Object, Object> hashMap = (LinkedHashMap) hmObject;
            for(Map.Entry<Object, Object> item : hashMap.entrySet()){
                keyId = (Key) item.getKey();
                result.put("{key: "+keyId.getId()+", weight: "+ keyId.getWeight()+"}", String.valueOf(item.getValue()));
            }
        }
        return result;
    }
}
