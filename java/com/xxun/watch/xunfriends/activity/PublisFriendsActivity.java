package com.xxun.watch.xunfriends.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.base.BaseActivity;
import com.xxun.watch.xunfriends.bean.ListBean;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import com.xxun.watch.xunfriends.utils.ImageFactory;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;

import android.content.ComponentName;
import android.util.Log;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import com.xiaoxun.smart.uploadfile.OnUploadResult;
import com.xiaoxun.smart.uploadfile.ProgressListener;
import com.xiaoxun.smart.uploadfile.UploadFile;

import org.json.JSONException;
import org.json.JSONObject;
import android.view.Window;

/**
 * @author cuiyufeng
 * @Description: PublisFriends 发布朋友圈
 * @date 2018/12/21 15:37
 */
public class PublisFriendsActivity extends BaseActivity implements View.OnClickListener{
    private ImageView iv_publisphoto,iv_publislocation,iv_publismood,iv_publistext;
    private XiaoXunNetworkManager mNetService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        setContentView(R.layout.activity_publisfriends);
        mNetService = myApp.getNetService();
        initView();
    }

    private void initView(){
        iv_publisphoto = (ImageView) findViewById(R.id.iv_publisphoto);
        iv_publislocation = (ImageView) findViewById(R.id.iv_publislocation);
        iv_publismood = (ImageView) findViewById(R.id.iv_publismood);
        iv_publistext = (ImageView) findViewById(R.id.iv_publistext);
        iv_publisphoto.setOnClickListener(this);
        iv_publislocation.setOnClickListener(this);
        iv_publismood.setOnClickListener(this);
        iv_publistext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_publisphoto:
                startActivity(new Intent(this,PhotoActivity.class));
                break;
            case R.id.iv_publislocation:
                startActivity(new Intent(this,LocationActivity.class));
                break;
            case R.id.iv_publismood:
                Intent intentmood =new Intent(this,MoodActivity.class);
                startActivity(intentmood);
                break;
            case R.id.iv_publistext:
                Intent intent =new Intent(this,TextActivity.class);
                startActivity(intent);
                break;
        }
    }
}