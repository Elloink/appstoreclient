package com.example.yzy.appstoreclient;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yangzhongyu on 15-2-26.
 */
public class PageListView extends ListView {

    public interface CallBackInterface {

        /**
         *
         * @param page 加载哪一页
         * @return 是否加载到了数据，加载到最后一页的时候就没有了数据了
         */
        public boolean loadNextPageData(int page);
    }
    public ProgressBar mBottomLoadMoreProgressBar = null;
    private int mLastItemIndex;
    private int mCurPage = 1;
    private int mFirstItemIndex;

    public int getmLastItemIndex() {
        return mLastItemIndex;
    }

    public void setmLastItemIndex(int mLastItemIndex) {
        this.mLastItemIndex = mLastItemIndex;
    }

    public int getmFirstItemIndex() {
        return mFirstItemIndex;
    }

    public void setmFirstItemIndex(int mFirstItemIndex) {
        this.mFirstItemIndex = mFirstItemIndex;
    }

    public int getmCurPage() {
        return mCurPage;
    }

    public void setmCurPage(int mCurPage) {
        this.mCurPage = mCurPage;
    }

  //  public int mCount;

    private CallBackInterface mCallback;

    public PageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            //    Log.d("yzy","scrollState="+scrollState);

                // 当滚动停止且滚动的总数等于数据的总数，去加载
               // mCount = mLastItemIndex - mFirstItemIndex;

                int dataCount = getAdapter().getCount();

                Log.d("yzy","scrollState="+scrollState+" dataCount="+dataCount +" mCurPage="+mCurPage +" mLastItemIndex="+mLastItemIndex);
                //当滚动停止且滚动的总数等于数据的总数，去加载

                if (scrollState == SCROLL_STATE_IDLE && mLastItemIndex == dataCount-1) {



                    mCurPage ++;
                    mBottomLoadMoreProgressBar.setVisibility(View.VISIBLE);
                    new  Thread(loadMoreThread).start();
                  //  mCallback.execute();
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                mLastItemIndex = firstVisibleItem + visibleItemCount - 1;
                // mLastItem = firstVisibleItem + visibleItemCount - 2;//如果 header footer都有 -2
                mFirstItemIndex = firstVisibleItem;
            }
        });

        mBottomLoadMoreProgressBar = new ProgressBar(context);
        mBottomLoadMoreProgressBar.setVisibility(View.INVISIBLE);
        addFooterView(mBottomLoadMoreProgressBar);
        Log.d("yzy","add footer view");

    }

    private  AppInfoListViewAdapter mAppInfoListViewAdapter;
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mAppInfoListViewAdapter = (AppInfoListViewAdapter) adapter;
    }

    // 主线程Handler负责更新UI，Handler与 Thread通过Message通信
    private Thread loadMoreThread = new Thread(new Runnable() {


        @Override
        public void run() {
            Message msg = new Message();
            boolean isHasMorePage = mCallback.loadNextPageData(mCurPage);
            if (!isHasMorePage) {
                mCurPage--;//mCurPage复原，因为这一页没有数据了
            } else {

            }
            //ArrayList<ETOrderInfo> moreOrders = info.getVendorOrderListByDate(
            //        merchant_code, state, curPage + "");
            //orders.addAll(moreOrders);
            //if(moreOrders.size() ==0){

            //  msg.what = 1;
            //}else{

            //  msg.what = 0;
            // }
            //OrderListActivity.this.myHandler.sendMessage(msg);
            myHandler.sendMessage(msg);

        }

    });

    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出
            super.handleMessage(msg);
            switch (msg.what) {
              //  case 1:
                   // bottomLoadMore.setText(getResources().getString(R.string.allshow));
             //       break;
            //    case 0:
           //         break;
           //     case 2:
                   // Toast.makeText(OrderListActivity.this, getResources().getString(R.string.refresh_done), Toast.LENGTH_LONG).show();
                  //  topRefresh.setVisibility(View.VISIBLE);
           //         break;
                default:
                    mBottomLoadMoreProgressBar.setVisibility(INVISIBLE);
                    break;

            }

            //   ((BaseAdapter)getAdapter()).notifyDataSetChanged(); // 会出现ClassCastException: android.widget.HeaderViewListAdapter cannot be cast to android.widget.BaseAdapter

            //在ListView源代码中对于有footer header的ListView会把其做一次wrapper为HeaderViewListAdapter  mAdapter = new HeaderViewListAdapter(mHeaderViewInfos, mFooterViewInfos, adapter);
            //解决办法，重写setAdpter方法，保留wrapper之前的adapter的引用

            mAppInfoListViewAdapter.notifyDataSetChanged();



        }

    };

    public PageListView(Context context) {
        super(context);

    }
    public void setCallBack(CallBackInterface callback){
        this.mCallback = callback;
    }


}
