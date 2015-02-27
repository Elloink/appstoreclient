package com.example.yzy.appstoreclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class AppInfoListActivity extends Activity {

    private static final String TAG = "AppInfoListActivity";
    private ArrayList<AppInfo> mAllApps = new ArrayList<AppInfo>();
    private AppInfoListViewAdapter mAppInfoListViewAdapter = null;
    private PageListView mListView = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list);

        final String category = getIntent().getStringExtra("category_name");
        final String searchKey = getIntent().getStringExtra("search_key");

        mListView = (PageListView) this.findViewById(R.id.cloudapps);

        mListView.setCallBack(new PageListView.CallBackInterface() {
            @Override
            public boolean loadNextPageData(int page) {
                ArrayList<AppInfo> moreApps = AppInfo.getAppsByCategoryName(category,page);
                Log.d("yzy","moreApps..."+moreApps.size());
                if (moreApps.size() > 0) {
                    mAllApps.addAll(moreApps);
                    return  true;
                } else {
                    return false;
                }

            }
        });

        Thread getThread = new Thread() {
            @Override
            public void run() {
                mAllApps = AppInfo.getAppsByCategoryName(category,1);
                mAppInfoListViewAdapter = new AppInfoListViewAdapter(AppInfoListActivity.this , mAllApps);
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setAdapter(mAppInfoListViewAdapter);
                    }
                });
            }
        };
        getThread.start();


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("yzy","llll="+i);
                AppInfo app = mAllApps.get(i);
                Intent intent = new Intent(AppInfoListActivity.this,AppDetailInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("appinfo", app);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


}
