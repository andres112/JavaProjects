package firsttest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.LinkedHashMap;

public class LinkedHash implements SimpleKV {

    private final LinkedHashMap<Object, Object> l_hashMap = new LinkedHashMap<>();
    private final long MAX;
//    private int counter;

    public LinkedHash(long max_length) {
        this.MAX = max_length;
    }

    @Override
    public void put(Object k, Object v, Object lru) {
        if (MAX > 0) {
            if (this.l_hashMap.size() >= MAX) {
                if(Boolean.valueOf(String.valueOf(lru))){
                    removeMax_LRU();
                }
                else {
                    removeMax_Random();
                }
            }
            this.l_hashMap.put(k, v);
        } else {
            this.l_hashMap.put(k, v);
        }
    }

    @Override
    public Object get(Object k) {
        LinkedHashMap<Object, Object> res = new LinkedHashMap<>();
        for(Map.Entry<Object, Object> entry : this.l_hashMap.entrySet()){
            if(entry.getKey() instanceof Key){
                Key currentKey = (Key) entry.getKey();
                if(currentKey.getId().equals(String.valueOf(k))){
                    currentKey.updateWeight(currentKey.getWeight()+1);
                    res.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return res;
    }

    @Override
    public Object remove(Object k) {
        return this.l_hashMap.remove(k);
    }

    @Override
    public Object getAll() {
        for(Object key : this.l_hashMap.keySet()){
            Key currentKey = (Key) key;
            currentKey.updateWeight(currentKey.getWeight()+1);
        }
        return this.l_hashMap;
    }

    @Override
    public void removeMax_Random() {
        Random r = new Random();
        Object[] keys = this.l_hashMap.keySet().toArray();
        Object key = keys[r.nextInt(keys.length)];
        this.l_hashMap.remove(key);

    }

    @Override
    public void removeMax_LRU() {
        Object[] keys = this.l_hashMap.keySet().toArray();
        Key lightKey = new Key(null, Integer.MAX_VALUE);
        for(int i = 0 ; i < keys.length-1 ; i++ ){
            Key current = (Key)keys[i];
            Key next = (Key)keys[i+1];
            lightKey = current.getWeight() > next.getWeight() ?
                    (next.getWeight() < lightKey.getWeight() ? next : lightKey) :
                    (current.getWeight() < lightKey.getWeight() ? current : lightKey);
        }
        Key temporal = (Key) lightKey;
        this.l_hashMap.remove(lightKey);

    }

    @Override
    public int getSize() {
        return this.l_hashMap.size();
    }

    @Override
    public boolean containsKey(Object k) {
        for( Object key : this.l_hashMap.keySet()){
            Key item = (Key) key;
            if(item.getId() == String.valueOf(k))
                return true;
        }
        return false;
    }

}
