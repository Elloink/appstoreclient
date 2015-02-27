package com.example.yzy.appstoreclient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class SearchResultActivity extends Activity {

    private static final String TAG = "MyActivity";
    private ArrayList<AppInfo> mAllApps = new ArrayList<AppInfo>();
    private ListViewAdapter  mListViewAdapter = null;
    private MyListView mListView = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        final String searchKey = getIntent().getStringExtra("search_key");

        mListView = (MyListView) this.findViewById(R.id.cloudapps);

        mListView.setCallBack(new MyListView.CallBackInterface() {
            @Override
            public boolean loadNextPageData(int page) {
              //  ArrayList<AppInfo> moreApps = AppInfo.getAppsByCategoryName(category,page);
              //  Log.d("yzy","moreApps..."+moreApps.size());
             //   if (moreApps.size() > 0) {
             //       mAllApps.addAll(moreApps);
            //        return  true;
           //     } else {
                    return false;
           //     }

            }
        });

        Thread getThread = new Thread() {
            @Override
            public void run() {
                mAllApps = AppInfo.getAppsBySearchKey(searchKey);
                mListViewAdapter = new ListViewAdapter(SearchResultActivity.this , mAllApps);
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setAdapter(mListViewAdapter);
                    }
                });
            }
        };
        getThread.start();
    }


}
