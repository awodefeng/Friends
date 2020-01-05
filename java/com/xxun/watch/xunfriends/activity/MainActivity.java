package com.xxun.watch.xunfriends.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.base.BaseActivity;
import com.xxun.watch.xunfriends.bean.FriendsBean;
import com.xxun.watch.xunfriends.bean.HeadBean;
import com.xxun.watch.xunfriends.bean.ListBean;
import com.xxun.watch.xunfriends.bean.ListDataSave;
import com.xxun.watch.xunfriends.bean.SyncArrayBean;
import com.xxun.watch.xunfriends.bean.UserBean;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import com.xxun.watch.xunfriends.utils.ImageCallBack;
import com.xxun.watch.xunfriends.utils.ImageUtil;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;
import com.xxun.watch.xunfriends.widget.CircleTransform;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.google.gson.Gson;
import com.xxun.watch.xunfriends.base.BaseApplication;
import com.xxun.watch.xunfriends.widget.DragLayout;
import com.xxun.watch.xunfriends.widget.GlideCircleTransform;
import android.os.Process;
import android.view.Window;

/**
 * @author cuiyufeng
 * @Description: MainActivity  Launcher
 * @date 2018/12/21 15:37
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{
    private ImageView iv_userheads,iv_right;
    private Button btn_publisfriends,btn_showlist;
    private ImageView iv_redpoint;
    private XiaoXunNetworkManager mNetService;
    //private DragLayout root_view;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    private boolean isFriestIn = true;//第一次加载列表弹出loading 之后不再显示
    private boolean isdataSuccess;//判断朋友圈是否有数据
    private  String Eid;
    private RelativeLayout rout_progress;
    private ImageView iv_gress;
    private static final String  ACTION_FRIEND_CIRCLE_LIKE = "com.xiaoxun.sdk.action.FRIEND_CIRCLE_LIKE";
    private BroadcastReceiver mMyReceiver;
    //联系人动态权限
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private boolean isOnCreate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        setContentView(R.layout.activity_main);
        initView();
        mNetService = myApp.getNetService();
        Eid = mNetService.getWatchEid();

        isFriestIn = true;
        getNickName(this,Eid,mNetService);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        //先读取本地缓存 没有的话再去调用接口
        //myApp.dvsinfo(this,Eid,mNetService,iv_userheads);
        initMyReceiver();

        File destFile = new File(myApp.getIconCacheDir(), Eid + ".jpg");
        if (destFile.exists()) {
            BitmapDrawable bitdrawable = new BitmapDrawable(myApp.getResources(), destFile.getAbsolutePath());
            ImageUtil.setMaskImage(iv_userheads, R.mipmap.bg_findfriends, bitdrawable);
            Log.i("cui","onCreate本地有头像缓存，直接加载"+destFile.getAbsolutePath());
        }else{
            Log.i("cui","onCreate本地没有去onResume下载");
        }
        isOnCreate = true;
    }

    //注册 和 接收广播
    private void initMyReceiver() {
        mMyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("cui","接收广播");
                if (action.equals(ACTION_FRIEND_CIRCLE_LIKE)) {
                    String json=intent.getStringExtra("json_data");
                    Log.i("cui","json=="+json);
                    if(!TextUtils.isEmpty(json)){
                        getFriendsList();
                    }
                    /*initData(lct);*/
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FRIEND_CIRCLE_LIKE);
        registerReceiver(mMyReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //头像是否有加载过 没有的话这里每次读缓存文件就行
       /*Log.i("cui","----"+BaseApplication.getInstance().isFrastLoadingHead);
        if(!BaseApplication.getInstance().isFrastLoadingHead){
            String stringHead= ListDataSave.getShareValue(MainActivity.this,"head_key","");
            Log.i("cui","stringHead="+stringHead);
            if(!TextUtils.isEmpty(stringHead)){
                Log.i("cui","stringHead 有缓存");
                byte [] head=android.util.Base64.decode(stringHead,android.util.Base64.NO_WRAP);
                Glide.with(MainActivity.this)
                        .load(head)
                        .bitmapTransform(new GlideCircleTransform(MainActivity.this))
                        .dontAnimate()
                        .into(iv_userheads);
                BaseApplication.getInstance().isFrastLoadingHead = true;
            }
        }*/

       if(isOnCreate){
           isOnCreate = false;
           myApp.getHeadDrawable(myApp.getResources(), Eid, mNetService, R.mipmap.bg_findfriends, new ImageCallBack<File>() {
               @Override
               public void onReqSuccess(File result) {
                 Log.i("cui","MainActivity onResume getAbsolutePath - = "+ result.getAbsolutePath());
                BitmapDrawable bitdrawable = new BitmapDrawable(myApp.getResources(), result.getAbsolutePath());
                ImageUtil.setMaskImage(iv_userheads, R.mipmap.bg_findfriends, bitdrawable);
               }
           },false);
       }else{
           File destFile = new File(myApp.getIconCacheDir(), Eid + ".jpg");
           if (destFile.exists()) {
               BitmapDrawable bitdrawable = new BitmapDrawable(myApp.getResources(), destFile.getAbsolutePath());
               ImageUtil.setMaskImage(iv_userheads, R.mipmap.bg_findfriends, bitdrawable);
               Log.i("cui","onRes本地有头像缓存，直接加载"+destFile.getAbsolutePath());
           }
       }

        // 朋友圈是否有更新，有更新调用朋友圈列表，无更新 读取缓存，无缓存调用朋友圈列表
       String key= ListDataSave.getStringValue(MainActivity.this,"");
       Log.i("cui","朋友圈第一条KEY：= "+key);
       if(TextUtils.isEmpty(key)){
           getFriendsList();
       }else{
           String islike= ListDataSave.getShareValue(MainActivity.this,"islike","false");
           Log.i("cui","getShareValue islike = "+islike);
           if(!TextUtils.isEmpty(islike) &&islike.equals("true") ){
               getFriendsList();
           }else{
               checkfc(key);
           }
       }
    }


    private void initlist(boolean checkUpdate){
        if(checkUpdate){
            //朋友圈是否有更新
            getFriendsList();
        }else{
            ListDataSave listData = new ListDataSave(MainActivity.this);
            if(listData!=null && listData.getDataList()!=null){
                ArrayList<ListBean> listBeans =listData.getDataList();
                if(listBeans.size()!=0){
                    Log.i("cui","读取缓存：= "+listBeans.size());
                    BaseApplication.getInstance().setFriendsList(listBeans);
                }else {
                    //没人发布朋友圈 本地缓存数据丢失的情况
                    Log.i("cui","无缓存列表--");
                    getFriendsList();
                }
            }else{
                Log.i("cui","无缓存列表");
                getFriendsList();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initView(){
        iv_userheads = (ImageView)findViewById(R.id.iv_userhead);
        btn_publisfriends = (Button) findViewById(R.id.btn_publisfriends);
        btn_showlist = (Button) findViewById(R.id.btn_showlist);
        btn_publisfriends.setOnClickListener(this);
        btn_showlist.setOnClickListener(this);
        iv_redpoint = (ImageView) findViewById(R.id.iv_redpoint);

        rout_progress = (RelativeLayout) findViewById(R.id.rout_progress);
        iv_gress = (ImageView)findViewById(R.id.iv_gress);
        Glide.with(MainActivity.this).load(R.drawable.gress).asGif().into(iv_gress);
        rout_progress.setVisibility(View.GONE);

        iv_right = (ImageView)findViewById(R.id.iv_right);
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BaseApplication.getInstance().getFriendsList().size()>0){
                    startActivity(new Intent(MainActivity.this,FriendsActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    Utils.toastShow(MainActivity.this,"333");
                }else{
                    if(isdataSuccess){
                        Utils.toastShow(MainActivity.this,getString(R.string.publis_friends));
                    }else{
                        Utils.toastShow(MainActivity.this,getString(R.string.main_data_fail));
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_publisfriends:
                Log.i("cui","isdataSuccess = "+isdataSuccess);
                if(NetUtil.checkNet(MainActivity.this)){
                    if(BaseApplication.getInstance().getFriendsList().size()>0){
                        Intent intent =new Intent(this,PublisFriendsActivity.class);
                        startActivity(intent);
                        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    }else{
                        if(isdataSuccess){
                            Intent intent =new Intent(this,PublisFriendsActivity.class);
                            startActivity(intent);
                            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        }else{
                            Utils.toastShow(MainActivity.this,getString(R.string.main_data_fail));
                            getFriendsList();
                        }
                    }
                }else{
                    Utils.toastShow(MainActivity.this,getString(R.string.no_network));
                }
                break;

            case R.id.btn_showlist:
                iv_redpoint.setVisibility(View.GONE);
                if(BaseApplication.getInstance().getFriendsList().size()>0){
                    startActivity(new Intent(MainActivity.this,FriendsActivity.class));
                }else{
                    if(isdataSuccess){
                        Utils.toastShow(MainActivity.this,getString(R.string.publis_friends));
                    }else{
                        Utils.toastShow(MainActivity.this,getString(R.string.main_data_fail));
                    }
                }
                break;
        }
    }

    private void getFriendsList(){

        if(isFriestIn){
            rout_progress.setVisibility(View.VISIBLE);
        }
        Date d = new Date();
        d.setYear(d.getYear()+1);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String timestamp = format.format(d).toString();
        String time = Utils.getReversedOrderTime(timestamp);
        String postData = "";
        String str ="EP/"+Eid+"/FCIRCLE/"+time;
        JSONObject obj =new JSONObject();
        try {
            obj.put("markKey",str);
            postData=obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(NetUtil.checkNet(MainActivity.this)){
            HttpSender.listfc(postData,mNetService,new ReqCallBack<JSONObject>() {
                @Override
                public void onReqSuccess(JSONObject result) {
                    Log.i("cui---","result = "+result.toString());
                    ListDataSave.setShareValue(MainActivity.this,"islike","false");//回执点赞
                    isFriestIn = false;
                    rout_progress.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    FriendsBean bean = (FriendsBean)gson.fromJson(result.toString(), FriendsBean.class);
                    if(bean.getCode() == 0){
                        ArrayList<ListBean> listBeans= bean.getList();
                        Log.i("cui","friendsList.size() = "+listBeans.size());
                        isdataSuccess  = true;
                        //加载成功之后添加到缓存
                        BaseApplication.getInstance().setFriendsList(listBeans);
                        ListDataSave listData=new ListDataSave(MainActivity.this);
                        listData.setDataList(listBeans);
                        if(listBeans.size()>0){
                            Log.i("cui","朋友圈存储第一条KEY"+listBeans.get(0).getKey());
                            ListDataSave.setStringValue(MainActivity.this,listBeans.get(0).getKey());
                        }

                    }else{
                        Utils.toastShow(MainActivity.this,"errorCode = "+bean.getCode());
                    }
                }
                @Override
                public void onReqFailed(String errorMsg) {
                    isdataSuccess = false;
                    rout_progress.setVisibility(View.GONE);
                    Log.e("cui","errorMsg friendsList= "+errorMsg);
                    Utils.toastShow(MainActivity.this,getString(R.string.data_error)+errorMsg);
                    MainActivity.this.finish();
                }
            });
        }else{
            Utils.toastShow(MainActivity.this,getString(R.string.no_network));
            //无网络连接 等1秒直接finish 用户体验
            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rout_progress.setVisibility(View.GONE);
                    finish();
                }
            },1000);*/
            rout_progress.setVisibility(View.GONE);
            //无网络连接 读取缓存
            ListDataSave listData=new ListDataSave(MainActivity.this);
            if(listData==null){
                Log.i("cui","listData is null ");
            }else{
                Log.i("cui","listData = "+listData);
            }
            ArrayList<ListBean> listBeans =listData.getDataList();
            if(listBeans.size()!=0){
                Log.i("cui","缓存friendsList.size() = "+listBeans.size());
                BaseApplication.getInstance().setFriendsList(listBeans);
            }else{
                //Toast.makeText(MainActivity.this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                rout_progress.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1000);
            }
        }
    }


    @Override
    protected void onDestroy() {
        Process.killProcess(Process.myPid());
        super.onDestroy();
        if(mMyReceiver!=null){
            unregisterReceiver(mMyReceiver);
        }
    }

    private  void checkfc(String newKey){
        JSONObject obj =new JSONObject();
        try {
            obj.put("newKey",newKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(NetUtil.checkNet(MainActivity.this)){
            HttpSender.checkfc(obj.toString(),mNetService,new ReqCallBack<JSONObject>() {
                @Override
                public void onReqSuccess(JSONObject result) {
                    Log.i("cui","checkfc result = "+result.toString());
                    try {
                        if(result.getInt("code") == 0){
                          boolean hasNew = result.getBoolean("hasNew");
                            initlist(hasNew);
                            if(hasNew && !isFriestIn){

                                iv_redpoint.setVisibility(View.VISIBLE);
                            }else{
                                iv_redpoint.setVisibility(View.GONE);
                            }
                        }else{
                            Utils.toastShow(MainActivity.this,"检查朋友圈是否更新---errorCode = "+result.getInt("code"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("cui","checkfc error--"+e.getMessage());
                    }
                }
                @Override
                public void onReqFailed(String errorMsg) {
                    Log.e("cui","checkfc error--"+errorMsg);
                    Utils.toastShow(MainActivity.this,"errorMsg = "+errorMsg);
                }
            });
        }else{
            Utils.toastShow(MainActivity.this,getString(R.string.no_network));
            initlist(false);
        }
    }

    private void downloadPicture(final List<ListBean> listBeans){
        if(!NetUtil.checkNet(MainActivity.this)){
            return;
        }
    }

    public void getNickName(final Activity activity, final String eid, XiaoXunNetworkManager mNetService){
        JSONObject obj =new JSONObject();
        try {
            obj.put("EID",eid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(NetUtil.checkNet(activity)){
            Log.e("cui","obj = "+obj.toString());
            HttpSender.dvsinfo(obj.toString(),mNetService,new ReqCallBack<JSONObject>() {
                @Override
                public void onReqSuccess(JSONObject result) {
                    Gson gson = new Gson();
                    UserBean bean = (UserBean)gson.fromJson(result.toString(), UserBean.class);
                    if(bean.getCode() ==0){
                        Log.i("cui","bean.getNickName() = "+bean.getNickName());
                        myApp.setNickName(bean.getNickName());
                    }else{
                        Utils.toastShow(activity,"获取头像、昵称---errorCode = "+bean.getCode());
                    }
                }
                @Override
                public void onReqFailed(String errorMsg) {
                    Utils.toastShow(activity,"获取头像、昵称---errorMsg = "+errorMsg);
                }
            });
        }else{
            Utils.toastShow(activity,activity.getString(R.string.no_network));
        }
    }

}