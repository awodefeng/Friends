package com.xxun.watch.xunfriends.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.xxun.watch.xunfriends.R;

/**
 * @author cuiyufeng
 * @Description: MyDialog
 * @date 2018/12/26 17:17
 */
public class MyDialog extends Dialog{
    private Activity context;
    private static MyDialog dialog;
    private ImageView ivProgress;


    public MyDialog(Activity context) {
        super(context);
        this.context = context;
    }

    public MyDialog(Activity context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    //显示dialog的方法
    public static MyDialog showDialog(Activity context){
        dialog = new MyDialog(context, R.style.MyDialog);//dialog样式
        dialog.setContentView(R.layout.dialog_layout);//dialog布局文件
        dialog.setCanceledOnTouchOutside(true);//点击外部不允许关闭dialog
        return dialog;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && dialog != null){
            ivProgress = (ImageView) dialog.findViewById(R.id.iv_Progress);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate_anim);
            ivProgress.startAnimation(animation);
            //Glide.with(context).load(R.drawable.gress).into(ivProgress);
        }
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}