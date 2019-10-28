package firsttest;

import java.util.IdentityHashMap;
import java.util.Random;

/**
 *
 * @author andre
 */
public class IdentityHash implements SimpleKV {

    private final IdentityHashMap<Object, Object> i_hashMap = new IdentityHashMap<>();
    private final long MAX;

    public IdentityHash(long max_length) {
        this.MAX = max_length;
    }

    @Override
    public void put(Object k, Object v) {
        if (MAX > 0) {
            if (i_hashMap.size() >= MAX) {
                removeMax_Random();
            }
            this.i_hashMap.put(k, v);
        } else {
            this.i_hashMap.put(k, v);
        }
    }

    @Override
    public Object get(Object k) {
        return this.i_hashMap.get(k);
    }

    @Override
    public Object remove(Object k) {
        return this.i_hashMap.remove(k);
    }

    @Override
    public void getAll() {
        for (Object key : this.i_hashMap.keySet()) {
            this.i_hashMap.get(key);
            //System.out.println(key +" : "+ this.i_hashMap.get(key));
        }
    }

    @Override
    public void removeMax_Random() {
        Random r = new Random();
        Object[] keys = i_hashMap.keySet().toArray();
        Object key = keys[r.nextInt(keys.length)];
        i_hashMap.remove(key);
    }

    @Override
    public int getSize() {
        return this.i_hashMap.size();
    }

    @Override
    public boolean containsKey(Object k) {
        return this.i_hashMap.containsKey(k);
    }


}
