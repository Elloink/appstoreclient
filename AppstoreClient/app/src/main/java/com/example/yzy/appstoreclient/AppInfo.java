package com.example.yzy.appstoreclient;

import android.graphics.Bitmap;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yzy on 15-2-17.
 */
public class AppInfo implements Serializable {
    public static final String NAME = "name";
    public static final String PHOTO = "photo";
    public static final String SUMMARY = "summary";
    public static final String URL = "url";
    public static final String ICON = "icon";
    private String name;
    private String summary;

    private String photoUrl;
    private String apkUrl;


    private String iconUrl;
    private String categoryName;

    //Bitamp不能Serializable，所以用byte数组实现
    private byte[] appIcon;

    public byte[] getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(byte[] appIcon) {
        this.appIcon = appIcon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static AppInfo initFromJSON(JSONObject app){
        AppInfo info  = new AppInfo();
        try {
            info.setName(app.getString(AppInfo.NAME));
            info.setSummary(app.getString(AppInfo.SUMMARY));
            info.setApkUrl(app.getString(AppInfo.URL));
            info.setPhotoUrl(app.getString(AppInfo.PHOTO));
            info.setIconUrl(app.getString(AppInfo.ICON));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  info;
    }

    //http://51appstore.duapp.com/index.php/api/example/apps/catename/IM/page/2/format/json
    public static ArrayList<AppInfo> getAppsByCategoryName(String category,int page){
        ArrayList<AppInfo> allApps = new  ArrayList<AppInfo>();
        final HttpGet httpRequest = new HttpGet(Global.APPS_IN_ONE_CATEGORY_URL+category+"/page/"+page+"/format/json");
        final HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                final String resultData = EntityUtils.toString(httpResponse.getEntity());

                if (!resultData.isEmpty()){
                    JSONArray allappsArray = new JSONArray(resultData);
                    for(int i = 0;i < allappsArray.length(); i++){
                        JSONObject app = (JSONObject)allappsArray.get(i);
                        allApps.add(AppInfo.initFromJSON(app));
                    }
                }
            } else {
                httpResponse = null;
                httpRequest.abort();
            }
        } catch (Exception e) {
            httpRequest.abort();
        } finally {
            if (httpclient != null) {
                httpclient.getConnectionManager().shutdown();
            }
        }
        return allApps;
    }


    public static ArrayList<AppInfo> getAppsBySearchKey(String key){
            ArrayList<AppInfo> allApps = new  ArrayList<AppInfo>();
            final HttpGet httpRequest = new HttpGet(Global.APPS_KEY_SEACH_URL+key+"/format/json");
            final HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpResponse httpResponse = httpclient.execute(httpRequest);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    final String resultData = EntityUtils.toString(httpResponse.getEntity());

                    if (!resultData.isEmpty()){
                        JSONArray allappsArray = new JSONArray(resultData);
                        for(int i = 0;i < allappsArray.length(); i++){
                            JSONObject app = (JSONObject)allappsArray.get(i);
                            allApps.add(AppInfo.initFromJSON(app));
                        }
                    }
                } else {
                    httpResponse = null;
                    httpRequest.abort();
                }
            } catch (Exception e) {
                httpRequest.abort();
            } finally {
                if (httpclient != null) {
                    httpclient.getConnectionManager().shutdown();
                }
            }
            return allApps;
    }

}
