package com.example.yzy.appstoreclient;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;
import android.widget.Scroller;

/**
 * Created by yangzhongyu on 15-3-5.
 *
 * 包含若干个子View，当某个子view可见的时候，通过callback机制通知调用者，动态加载子view的内容
 *
 * 类比于Workspce 和 CellLayout,当某一个Cellayout可见的时候，动态加载CellLayout中的app
 */
public class MyHorizontalScrollView extends ViewGroup {
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller = null;
    //private OverScroller mScroller;
    public MyHorizontalScrollView(Context context) {
        this(context, null);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);

    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return super.onInterceptHoverEvent(event);
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            Log.d("yzy","computeScroll="+mScroller.getCurrX());
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void startMove(int startX,int dx){
        //使用动画控制偏移过程 , 3s内到位
        mScroller.startScroll(startX, 0, dx , 0,1000);
        //其实点击按钮的时候，系统会自动重新绘制View，我们还是手动加上吧。
        invalidate();
        //使用scrollTo一步到位
        //scrollTo(curScreen * MultiScreenActivity.screenWidth, 0);
    }

    int mLastionMotionX = 0;
    int mDownMotionX = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventX = (int) event.getX();
        int eventY = (int) event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mDownMotionX = eventX;
                mLastionMotionX = eventX;
                break;
            case MotionEvent.ACTION_MOVE: {
                int detaX = (int)(mLastionMotionX - eventX ); //每次滑动屏幕，屏幕应该移动的距离

            //    if (Math.abs(detaX) > 20) {
                    scrollBy(detaX, 0);//开始缓慢滑屏咯。 detaX > 0 向右滑动 ， detaX < 0 向左滑动 ，
                    Log.d("yzy","ACTION_MOVE="+detaX);
            //    }



                mLastionMotionX = eventX ;

                //mChild.layout(eventX, eventY, eventX + mChild.getWidth(), eventY + mChild.getHeight());

                break;
            }
            case MotionEvent.ACTION_UP: {
                int childWidth = getChildAt(0).getWidth();
                Log.d("yzy","mDownMotionX="+mDownMotionX+" eventX="+eventX+" childWith="+childWidth);
                int moveDx = Math.abs((int)(eventX - mDownMotionX));
                if (moveDx > 20) {
                    if (getChildAt(0).getX() > 0  || getChildAt(getChildCount()-1).getX() < childWidth) {
                        startMove(eventX, (eventX - mDownMotionX) > 0 ? -moveDx : moveDx);
                    }
                    startMove(eventX, (eventX - mDownMotionX) > 0 ? moveDx - childWidth :  childWidth - moveDx);//是相反方向！！！
                    invalidate();
                }

            }
        }
        return true;
      //  return super.onTouchEvent(event);
    }



    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return super.generateLayoutParams(p);
    }

    /**
     * 计算控件的大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      //  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = measureWidth(widthMeasureSpec);
        int measureHeight = measureHeight(heightMeasureSpec);
        // 计算自定义的ViewGroup中所有子控件的大小
        measureChildren(widthMeasureSpec, heightMeasureSpec);//很重要
        // 设置自定义的控件MyViewGroup的大小
        Log.d("yzy","measureWidth="+measureWidth+" measureHeight="+measureHeight);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

        switch (widthMode) {
            /**
             * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
             * MeasureSpec.AT_MOST。
             *
             *
             * MeasureSpec.EXACTLY是精确尺寸，
             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
             * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
             *
             *
             * MeasureSpec.AT_MOST是最大尺寸，
             * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
             * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
             * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
             *
             *
             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
             * 通过measure方法传入的模式。
             */
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = widthSize;
                break;
        }
        return result;
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;

        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);

        Log.d("yzy","heightMode="+heightMode+" heightSize="+heightSize+" MeasureSpec.AT_MOST="+MeasureSpec.AT_MOST);
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                int maxHeight = 0;
                for(int i = 0;i<getChildCount();i++){
                    maxHeight = getChildAt(i).getHeight() > maxHeight ? getChildAt(i).getHeight() : maxHeight;
                }
                result = maxHeight;
                break;
            case MeasureSpec.EXACTLY:
                result = heightSize;
                break;
        }
        return result;
    }


    @Override
    protected void onLayout(boolean change, int left, int top, int right, int bottom) {

// 记录总高度
        int mTotalWidth = 0;
        // 遍历所有子视图
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            // 获取在onMeasure中计算的视图尺寸
            int measureHeight = childView.getMeasuredHeight();
            int measuredWidth = childView.getMeasuredWidth();

            childView.layout(mTotalWidth, 0,mTotalWidth+measuredWidth, measureHeight);

            mTotalWidth += measuredWidth;

        }

    }
}
