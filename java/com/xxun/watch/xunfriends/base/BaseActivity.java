package com.xxun.watch.xunfriends.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.app.Activity;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xiaoxun.smart.uploadfile.UploadFile;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.bean.HeadBean;
import com.xxun.watch.xunfriends.bean.UserBean;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;
import com.xxun.watch.xunfriends.widget.GlideCircleTransform;

import org.json.JSONException;
import org.json.JSONObject;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;

/**
 * @author cuiyufeng
 * @Description: BaseActivity
 * @date 2018/12/21 15:24
 */
public class BaseActivity extends Activity {
    public BaseApplication myApp = null;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (BaseApplication) getApplication();
    }

    public void showDialog(String title) {
        if(alertDialog!=null){
        }else{
            alertDialog = new AlertDialog.Builder(this).create();
        }
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        //true点击屏幕dialog不消失 点击物理返回键dialog消失
        alertDialog.setCancelable(true);
        /*alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK)
                    return true;
                return false;
            }
        });*/
        alertDialog.show();
        alertDialog.setContentView(R.layout.loading_alert);
        alertDialog.setCanceledOnTouchOutside(false);

        TextView tv_content=(TextView)alertDialog.findViewById(R.id.tv_content);
        if(TextUtils.isEmpty(title)){
            tv_content.setText("正在发布朋友圈");
        }else{
            //tv_content.setText("请稍候...");
            tv_content.setText("");
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.i("cui","addFlags---");
    }

    public void dismissDialog(){
        try {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Log.i("cui","clearFlags---");
            if(alertDialog != null && alertDialog .isShowing()){
                alertDialog.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
            Log.e("cui",""+e);
        } catch (final Exception e) {
            // Handle or log or ignore
            Log.e("cui",""+e);
        } finally {
            alertDialog = null;
        }
    }
}