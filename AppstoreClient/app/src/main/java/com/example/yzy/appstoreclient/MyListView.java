package com.example.yzy.appstoreclient;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yangzhongyu on 15-2-26.
 */
public class MyListView extends ListView {

    public interface CallBackInterface {

        public void execute();
    }
    public TextView bottomLoadMore = null;

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d("yzy","scrollState="+scrollState);
                callback.execute();

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

            }
        });
    }

    private CallBackInterface callback;
    public MyListView(Context context) {
        super(context);

    }
    public void setCallBack(CallBackInterface callback){
        this.callback = callback;
    }


}
