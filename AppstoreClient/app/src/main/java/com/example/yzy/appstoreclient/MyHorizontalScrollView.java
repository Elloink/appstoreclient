package com.example.yzy.appstoreclient;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by yangzhongyu on 15-3-5.
 *
 * 包含若干个子View，当某个子view可见的时候，通过callback机制通知调用者，动态加载子view的内容
 *
 * 类比于Workspce 和 CellLayout,当某一个Cellayout可见的时候，动态加载CellLayout中的app
 */
public class MyHorizontalScrollView extends ViewGroup {
    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {

    }
}
