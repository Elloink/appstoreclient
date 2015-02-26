package com.example.yzy.appstoreclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yangzhongyu on 15-2-25.
 */
public class CategoryActivity  extends Activity {
    ListView mListView = null;
    ArrayList<CategoryInfo> mAllCategory = new ArrayList<CategoryInfo>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
        mListView = (ListView) findViewById(R.id.all_category);

        Thread getThread = new Thread() {
            @Override
            public void run() {

                mAllCategory = CategoryInfo.getAllCategory();
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CategoryActivity.this, android.R.layout.simple_expandable_list_item_1);
                        for (CategoryInfo info : mAllCategory){
                            adapter.add(info.getNameCH());
                        }
                        mListView.setAdapter(adapter);
                    }
                });
            }
        };
        getThread.start();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CategoryActivity.this,MyActivity.class);
                intent.putExtra("category_name",mAllCategory.get(i).getNameEN());
                startActivity(intent);
            }
        });

    }
}
