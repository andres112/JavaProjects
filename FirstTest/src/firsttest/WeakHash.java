package firsttest;

import java.util.Random;
import java.util.WeakHashMap;

public class WeakHash implements SimpleKV {

    private final WeakHashMap<Object, Object> w_hashMap = new WeakHashMap<>();
    private final long MAX;

    public WeakHash(long max_length) {
        this.MAX = max_length;
    }

    @Override
    public void put(Object k, Object v) {
        if (MAX > 0) {
            if (w_hashMap.size() >= MAX) {
                removeMax_Random();
            }
            this.w_hashMap.put(k, v);
        } else {
            this.w_hashMap.put(k, v);
        }
    }

    @Override
    public Object get(Object k) {
        return this.w_hashMap.get(k);
    }

    @Override
    public Object remove(Object k) {
        return this.w_hashMap.remove(k);
    }

    @Override
    public void getAll() {
        for (Object key : this.w_hashMap.keySet()) {
            this.w_hashMap.get(key);
            //System.out.println(key +" : "+ this.w_hashMap.get(key));
        }
    }

    @Override
    public void removeMax_Random() {
        Random r = new Random();
        Object[] keys = w_hashMap.keySet().toArray();
        Object key = keys[r.nextInt(keys.length)];
        w_hashMap.remove(key);
    }

    @Override
    public int getSize() {
        return this.w_hashMap.size();
    }

    @Override
    public boolean containsKey(Object k) {
        return this.w_hashMap.containsKey(k);
    }

}
