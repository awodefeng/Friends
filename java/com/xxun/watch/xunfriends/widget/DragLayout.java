package com.xxun.watch.xunfriends.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by huangyouyang on 2017/9/9.
 */

public class DragLayout extends RelativeLayout {

    public void setOnDragStatusChangeListener(OnDragStatusChangeListener mListener) {
        this.mListener = mListener;
    }

    public interface OnDragStatusChangeListener {
        void onDragLeft();

        void onDragRight();
    }

    private OnDragStatusChangeListener mListener;
    private Context context;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.i("cui","onInterceptTouchEventxxxxxxxxxxxxxxx"+event);
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startX = event.getX();
            } else if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
                float endX = event.getX();
                float disX = endX - startX;
//                LogUtil.i(Const.LOG_TAG + "  onInterceptTouchEvent  disX=" + disX);
                // 在onInterceptTouchEvent中处理不太好，后续需要优化
                if (Math.abs(disX) > dip2px(context, 80)) {
                    if (disX > dip2px(context, 80) && mListener != null)
                        mListener.onDragRight();
                    if (disX < dip2px(context, -1 * 80) && mListener != null)
                        mListener.onDragLeft();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private float startX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("cui","onTouchEventxxxxxxxxxxxxxxx"+event);
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startX = event.getX();
            } else if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
                float endX = event.getX();
                float disX = endX - startX;
//                LogUtil.i(Const.LOG_TAG + "  onTouchEvent  disX=" + disX);
                if (disX > dip2px(context, 80) && mListener != null)
                    mListener.onDragRight();
                if (disX < dip2px(context, -1 * 80) && mListener != null)
                    mListener.onDragLeft();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
