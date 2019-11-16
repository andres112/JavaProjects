package com.company;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args){
        Main  test = new Main();

        int miss_counter = 0;
        int hit_counter = 0;
        String[] request = {"a","a","d","e","b","b","a","c","f","d","e","a","f","b","e","c"};
        List<String> cache = new ArrayList<String>();
        cache.add("a");
        cache.add("b");
        cache.add("c");

        for(int i =0; i< request.length; i++ ){
            if(cache.contains(request[i])){
                hit_counter++;
            }
            else{
                miss_counter++;
                int index = test.fif(i, request, cache);
                cache.set(index, request[i]);
            }
            System.out.println(cache);
            System.out.println("Hits: "+ hit_counter);
            System.out.println("Miss: "+ miss_counter);

        }

    }

    public int fif(int p, String[] r, List<String> c){
        int farthest =0;
        int cache_index =  0;
        for(String i : c){
            int distance = 0;
            for(int j = p; j < r.length; j ++){
                if(i.equals(r[j])){
                    if(distance > farthest) {
                        farthest = distance;
                        cache_index = c.indexOf(i);
                    }
                    break;
                }
                distance ++;
            }
            if(distance == r.length - p){
                cache_index = c.indexOf(i);
                break;
            }
        }
        return cache_index;
    }
}
