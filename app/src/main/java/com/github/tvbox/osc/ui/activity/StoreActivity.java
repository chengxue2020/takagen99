package com.github.tvbox.osc.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.tvbox.osc.store.Store;
import com.github.tvbox.osc.store.StoreManager;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        ListView listView = new ListView(this);

        setContentView(listView);

        List<String> names = new ArrayList<>();

        for(Store s : StoreManager.get().storeList){

            names.add(s.sourceName);

        }

        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                names));

        listView.setOnItemClickListener((a,b,pos,id)->{

            StoreManager.get().current =
                    StoreManager.get().storeList.get(pos);

            finish();

        });

    }
}