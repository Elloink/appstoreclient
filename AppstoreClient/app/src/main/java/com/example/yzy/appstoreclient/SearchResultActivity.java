package com.example.yzy.appstoreclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

public class SearchResultActivity extends BaseActivity {

    private static final String TAG = "SearchResultActivity";
    private ArrayList<AppInfo> mAllApps = new ArrayList<AppInfo>();
    private AppInfoListViewAdapter mAppInfoListViewAdapter = null;
    private PageListView mListView = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list);


        final String searchKey = getIntent().getStringExtra("search_key");

        mListView = (PageListView) this.findViewById(R.id.cloudapps);

        mListView.setCallBack(new PageListView.CallBackInterface() {
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


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppInfo app = mAllApps.get(i);
                Intent intent = new Intent(SearchResultActivity.this,AppDetailInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("appinfo", app);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        Thread getThread = new Thread() {
            @Override
            public void run() {
                mAllApps = AppInfo.getAppsBySearchKey(searchKey);
                mAppInfoListViewAdapter = new AppInfoListViewAdapter(SearchResultActivity.this , mAllApps);
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setAdapter(mAppInfoListViewAdapter);
                    }
                });
            }
        };
        getThread.start();
    }


}
