package com.shenit.commons.mvc.model;

import com.shenit.commons.repos.domain.Jsonable;
import com.shenit.commons.utils.GsonUtils;
import com.shenit.commons.utils.ShenMaps;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jgnan on 12/01/2017.
 */
public class Payload extends HashMap<String,Object> {
    public Payload(Object... kvs){
        values(kvs);
    }

    public Payload values(Object... kvs){
        int length = kvs == null ? 0 : kvs.length;
        for(int i=0; i< length ;i+=2){
            if(kvs[i] == null) continue;
            if(kvs[i] instanceof Jsonable) {
                putAll(ShenMaps.hash(kvs[i]));
                i--;
                continue;
            }
            else if(kvs[i] instanceof Map) {
                putAll((Map<? extends String, ?>) kvs[i]);
                continue;
            }
            put(kvs[i].toString(), kvs.length == i + 1 ? null : kvs[i+1]);
        }
        return this;
    }

    public static Payload wrap(Object... kvs) {
        return new Payload(kvs);
    }


    public String toString(){
        return GsonUtils.format(this);
    }
}
