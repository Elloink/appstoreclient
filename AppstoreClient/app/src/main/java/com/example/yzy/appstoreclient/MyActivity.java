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

    private static final String TAG = "HTTPCLIENT";
    private ArrayList<AppInfo> mAllApps = new ArrayList<AppInfo>();
    private GridView mGridView = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        //final TextView tv = (TextView) this.findViewById(R.id.tv);
        mGridView = (GridView) this.findViewById(R.id.cloudapps);
        //http address
        String httpurl = "http://51appstore.duapp.com/index.php/api/example/users/format/json";

        //定义Http Get请求
        final HttpGet httpRequest = new HttpGet(httpurl);

        //准备参数设置
        HttpParams params = new BasicHttpParams();

        //使用参数实例化HttpClient
        final HttpClient httpclient = new DefaultHttpClient();

        Thread getThread = new Thread() {
            @Override
            public void run() {
                try {
                    //执行Get请求，返回结果在httpResponse中
                    HttpResponse httpResponse = httpclient.execute(httpRequest);
                    Log.v(TAG, "creat http response object");
                    Log.v(TAG, "Response code:" + httpResponse.getStatusLine().getStatusCode());
                    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        Log.v(TAG, "get result");
                        final String resultData = EntityUtils.toString(httpResponse.getEntity());

                        if (resultData != ""){
                            //tv.post(new Runnable() {
                             //   @Override
                            //    public void run() {
                                //    tv.setText(resultData);
                             //   }
                           // });
                            JSONArray allappsArray = new JSONArray(resultData);
                            for(int i = 0;i<allappsArray.length();i++){
                                JSONObject app = (JSONObject)allappsArray.get(i);
                                AppInfo info  = new AppInfo();
                                info.setName(app.getString(AppInfo.NAME));
                                info.setSummary(app.getString(AppInfo.SUMMARY));
                                info.setApkUrl(app.getString(AppInfo.URL));
                                info.setPhotoUrl(app.getString(AppInfo.PHOTO));
                                mAllApps.add(info);
                            }
                            mGridView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mGridView.setAdapter(new GridViewAdapter(MyActivity.this , mAllApps));
                                }
                            });

                        }

                        else
                        {
                          //  tv.setText( "未读取到网页内容.");
                        }
                    } else {
                        //tv.setText( "code.="+httpResponse.getStatusLine().getStatusCode() );
                        httpResponse = null;
                        httpRequest.abort();
                        interrupted();
                    }
                } catch (ClientProtocolException e) {
                    Log.e(TAG, "ClientProtocolException");
                 //   tv.setText( "Exception");
                    httpRequest.abort();
                    interrupted();
                } catch (IOException e) {
                 //   tv.setText( "Exception");
                    Log.e(TAG, "IOException");
                    httpRequest.abort();
                    interrupted();
                } catch (Exception e) {
                  //  tv.setText( "Exception");
                    Log.e(TAG, "Exception"+e.toString());
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

    class GridViewAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<AppInfo> appInfos;

        public GridViewAdapter(Context context, List<AppInfo> appInfos) {
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
            //to do:convertView
            View view = inflater.inflate(R.layout.gv_item, null);
            final TextView tv = (TextView) view.findViewById(R.id.gv_item_appname);
            final ImageView iv = (ImageView) view.findViewById(R.id.gv_item_icon);
            tv.setText(appInfos.get(position).getName());
            Thread t = new Thread(){
                @Override
                public void run() {
                    super.run();
                    final Bitmap bitmap = getHttpBitmap(appInfos.get(position).getPhotoUrl());
                    iv.post(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(bitmap);

                        }
                    });
                }
            };
            t.start();
            return view;
        }
        /**
         * 从服务器取图片
         *http://bbs.3gstdy.com
         * @param url
         * @return
         */
        public  Bitmap getHttpBitmap(String url) {
            URL myFileUrl = null;
            Bitmap bitmap = null;
            try {
                Log.d(TAG, url);
                Log.d("yzy","url="+url);
                myFileUrl = new URL(url);
                Log.d("yzy","url="+url+" myFileUrl="+myFileUrl);
            } catch (MalformedURLException e) {
                Log.d("yzy","MalformedURLException"+e);
                e.printStackTrace();
            }
            try {
                if(myFileUrl != null){
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                    conn.setConnectTimeout(0);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

    }


}
