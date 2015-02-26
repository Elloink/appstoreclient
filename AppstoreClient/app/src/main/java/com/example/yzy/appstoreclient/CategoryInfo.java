package com.example.yzy.appstoreclient;

import android.widget.ArrayAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yangzhongyu on 15-2-25.
 */
public class CategoryInfo {
    //{"id":"1","name_en":"IM","name_ch":"\u804a\u5929\u793e\u4ea4"}
    public static final String NAME_EN = "name_en";
    public static final String NAME_CH = "name_ch";

    private String nameEN ;

    private String nameCH ;

    public String getNameCH() {
        return nameCH;
    }

    public void setNameCH(String nameCH) {
        this.nameCH = nameCH;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }


    public static CategoryInfo initFromJSON(JSONObject cate) {
        CategoryInfo category = new CategoryInfo();

        try {
            category.setNameCH(cate.getString(NAME_CH));
            category.setNameEN(cate.getString(NAME_EN));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  category;
    }

    public static ArrayList<CategoryInfo> getAllCategory(){
        ArrayList<CategoryInfo> allCategory = new ArrayList<CategoryInfo>();
        final HttpGet httpRequest = new HttpGet(Global.ALL_CATEGORY_URL);
        final HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                final String resultData = EntityUtils.toString(httpResponse.getEntity());

                if (!resultData.isEmpty()){
                    JSONArray allcategoryArray = new JSONArray(resultData);
                    for(int i = 0;i < allcategoryArray.length(); i++){
                        JSONObject app = (JSONObject)allcategoryArray.get(i);
                        allCategory.add(CategoryInfo.initFromJSON(app));
                    }

                }
            } else {
                httpResponse = null;
                httpRequest.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
            httpRequest.abort();
        } finally {
            if (httpclient != null) {
                httpclient.getConnectionManager().shutdown();
            }
        }
        return  allCategory;
    }
}
