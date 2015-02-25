package com.example.yzy.appstoreclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.BufferedReader;
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
    private ListView mListView = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        String category = getIntent().getStringExtra("category_name");
        mListView = (ListView) this.findViewById(R.id.cloudapps);

        final HttpGet httpRequest = new HttpGet(Global.APPS_IN_ONE_CATEGORY_URL+category+"/format/json");
        final HttpClient httpclient = new DefaultHttpClient();
        Thread getThread = new Thread() {
            @Override
            public void run() {
                try {
                    HttpResponse httpResponse = httpclient.execute(httpRequest);
                    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        final String resultData = EntityUtils.toString(httpResponse.getEntity());

                        if (!resultData.isEmpty()){
                            JSONArray allappsArray = new JSONArray(resultData);
                            for(int i = 0;i < allappsArray.length(); i++){
                                JSONObject app = (JSONObject)allappsArray.get(i);
                                mAllApps.add(AppInfo.initFromJSON(app));
                            }
                            mListView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mListView.setAdapter(new ListViewAdapter(MyActivity.this , mAllApps));
                                }
                            });
                        }
                    } else {
                        httpResponse = null;
                        httpRequest.abort();
                        interrupted();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception "+e);
                    httpRequest.abort();
                    interrupted();
                } finally {
                    if (httpclient != null) {
                        httpclient.getConnectionManager().shutdown();
                    }
                }
            }
        };
        getThread.start();
    }

    class ListViewAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<AppInfo> appInfos;

        public ListViewAdapter(Context context, List<AppInfo> appInfos) {
            inflater = LayoutInflater.from(context);
            this.appInfos = appInfos;
        }

        @Override
        public int getCount() {
            return appInfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            return appInfos.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final  int position, View convertView, ViewGroup parent) {
            //to do:convertView  holder 避免多次findViewByI
            View view = inflater.inflate(R.layout.gv_item, null);
            final TextView tv = (TextView) view.findViewById(R.id.gv_item_appname);
            final ImageView iv = (ImageView) view.findViewById(R.id.gv_item_icon);
            tv.setText(appInfos.get(position).getName());
            Thread t = new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        URL uri = new URL(appInfos.get(position).getPhotoUrl());
                        final Bitmap bitmap = BitmapFactory.decodeStream(uri.openStream());
                        iv.post(new Runnable() {
                            @Override
                            public void run() {
                                iv.setImageBitmap(bitmap);

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            t.start();

            Button btnDetail = (Button) view.findViewById(R.id.btnDetail);
            btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppInfo app = appInfos.get(position);
                    Intent intent = new Intent(MyActivity.this,AppDetailInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("appinfo", app);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            return view;
        }

    }


}
