package com.example.yzy.appstoreclient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Created by yangzhongyu on 15-3-5.
 */
public class BaseActivity extends Activity {
    private Context mContext = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(this.getTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
