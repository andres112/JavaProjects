/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package firsttest;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author andre
 */
public class ConcurrentHash implements SimpleKV {

    private final ConcurrentHashMap<Object, Object> c_hashMap = new ConcurrentHashMap<>();
    private final long MAX;

        public ConcurrentHash(long max_length) {
        this.MAX = max_length;
    }

    @Override
    public void put(Object k, Object v) {
        if(MAX > 0){
            if(c_hashMap.size() >= MAX){
                removeMax_Random();
            }
            this.c_hashMap.put(k, v);
        }
        else{
            this.c_hashMap.put(k, v);
        }
    }

    @Override
    public Object get(Object k) {
        return this.c_hashMap.get(k);
    }

    @Override
    public Object remove(Object k) {
        return this.c_hashMap.remove(k);
    }

    @Override
    public void getAll() {
        for (Object key : this.c_hashMap.keySet()) {
            this.c_hashMap.get(key);
            //System.out.println(key +" : "+ this.c_hashMap.get(key));
        }
    }
    
    @Override
    public void removeMax_Random(){
        Random r = new Random();
        Object[] keys = c_hashMap.keySet().toArray();
        Object key = keys[r.nextInt(keys.length)];
        c_hashMap.remove(key);
    }

    @Override
    public int getSize() {
        return this.c_hashMap.size();
    }

    @Override
    public boolean containsKey(Object k) {
        return this.c_hashMap.containsKey(k);
    }
}
