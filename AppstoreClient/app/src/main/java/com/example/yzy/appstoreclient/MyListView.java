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
    public TextView mBottomLoadMore = null;
    private int mLastItem;
    private int mCurPage = 1;
    private int mFirstItem;
    public int mCount;

    private CallBackInterface mCallback;

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d("yzy","scrollState="+scrollState);
                mCallback.execute();
                // 当滚动停止且滚动的总数等于数据的总数，去加载
                mCount = mLastItem - mFirstItem;

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                mLastItem = firstVisibleItem + visibleItemCount - 1;
                // mLastItem = firstVisibleItem + visibleItemCount - 2;//如果 header footer都有 -2
                mFirstItem = firstVisibleItem;
            }
        });
    }


    public MyListView(Context context) {
        super(context);

    }
    public void setCallBack(CallBackInterface callback){
        this.mCallback = callback;
    }


}
