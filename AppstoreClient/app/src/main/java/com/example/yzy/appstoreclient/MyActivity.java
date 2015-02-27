package com.example.yzy.appstoreclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    private static final String TAG = "MyActivity";
    private ArrayList<AppInfo> mAllApps = new ArrayList<AppInfo>();
    private ListViewAdapter  mListViewAdapter = null;
    private MyListView mListView = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        final String category = getIntent().getStringExtra("category_name");
        final String searchKey = getIntent().getStringExtra("search_key");

        mListView = (MyListView) this.findViewById(R.id.cloudapps);

        mListView.setCallBack(new MyListView.CallBackInterface() {
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
                mListViewAdapter = new ListViewAdapter(MyActivity.this , mAllApps);
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
