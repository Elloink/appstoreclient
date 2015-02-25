package com.example.yzy.appstoreclient;

import org.json.JSONException;
import org.json.JSONObject;

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
}
