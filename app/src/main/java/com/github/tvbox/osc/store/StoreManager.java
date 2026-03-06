package com.github.tvbox.osc.store;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class StoreManager {

    private static StoreManager instance;

    public List<Store> storeList = new ArrayList<>();

    public Store current;

    public static StoreManager get(){

        if(instance == null)
            instance = new StoreManager();

        return instance;
    }

    public void parse(String json){

        storeList.clear();

        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        JsonArray arr = obj.getAsJsonArray("storeHouse");

        for(int i=0;i<arr.size();i++){

            JsonObject o = arr.get(i).getAsJsonObject();

            Store s = new Store();

            s.sourceName = o.get("sourceName").getAsString();

            s.sourceUrl = o.get("sourceUrl").getAsString();

            storeList.add(s);
        }
    }

}