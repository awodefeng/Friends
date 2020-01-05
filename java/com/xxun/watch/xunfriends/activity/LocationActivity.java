package com.xxun.watch.xunfriends.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.base.BaseActivity;
import com.xxun.watch.xunfriends.bean.ListBean;
import com.xxun.watch.xunfriends.bean.LocationBean;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;

import org.json.JSONObject;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import android.view.Window;
/**
 * @author cuiyufeng
 * @Description: LocationActivity
 * @date 2018/12/21 16:47
 */
public class LocationActivity extends BaseActivity{
    private Button btn_second_location,btn_confirm_location;
    private ImageView iv_location_icon;
    private TextView tv_location_text;
    private String SendAction = "com.xxun.watch.xunfriends.action.resend.location";
    private String ReceiveAction = "com.xxun.watch.xunfriends.action.onReceive.location";
    private BroadcastReceiver mMyReceiver;
    private  LocationBean.LocationPL locationPLbean;
    private XiaoXunNetworkManager mNetService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);

        setContentView(R.layout.activity_location);
        mNetService=myApp.getNetService();
        initView();
        initListener();
        initMyReceiver();
        sendLoactionBroadcast();
    }

    private void initView(){
        btn_second_location=(Button) findViewById(R.id.btn_second_location);
        btn_confirm_location=(Button) findViewById(R.id.btn_confirm_location);
        iv_location_icon=(ImageView) findViewById(R.id.iv_location_icon);
        tv_location_text=(TextView) findViewById(R.id.tv_location_text);
    }

    //发送广播
    public void sendLoactionBroadcast(){
        Intent intent = new Intent();
        intent.setAction(SendAction);
        intent.setPackage("com.xxun.watch.location");
        intent.putExtra("info", "broadcast");
        sendBroadcast(intent);
        Log.i("cui","发送广播"+intent.toString());
        showDialog("LocationActivity wait 发送广播");
    }

    //注册 和 接收广播
    private void initMyReceiver() {
        mMyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("cui","接收广播");
                if (action.equals(ReceiveAction)) {
                    String lct=intent.getStringExtra("lct");
                    Log.i("cui","lct=="+lct);
                    initData(lct);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiveAction);
        registerReceiver(mMyReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMyReceiver!=null){
            unregisterReceiver(mMyReceiver);
        }
    }

    private void initData(String lct){
        LocationBean locationBean=new Gson().fromJson(lct,LocationBean.class);
        if(locationBean.getRC() == 1){
            locationPLbean=locationBean.getPL();
            tv_location_text.setText(""+locationPLbean.getDesc());
        }else{
            Utils.toastShow(LocationActivity.this,getString(R.string.data_error));
        }
        dismissDialog();
        Log.i("cui","接收广播 dismissDialog");
    }

    private void initListener(){
        btn_second_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/12/27 重新获取地理消息
                sendLoactionBroadcast();

            }
        });

        btn_confirm_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/12/27 <调用> 发布地理位置接口
                if(locationPLbean != null){
                    publisLocation(locationPLbean);
                }else {
                    Utils.toastShow(LocationActivity.this,getString(R.string.loaction_get));
                }
            }
        });
    }

    private void publisLocation(LocationBean.LocationPL locationBean){
        if(NetUtil.checkNet(LocationActivity.this)){
            showDialog("");

            final ListBean bean =new ListBean();
            bean.setType("loc");
            bean.setContent("this is Location");
            //LocationBean locationBean =new LocationBean();
            bean.setLoc(locationBean);
            Log.i("cui","getName = "+myApp.getNickName());
            bean.setNickName(""+myApp.getNickName());
            String Eid=mNetService.getWatchEid();
            bean.setEID(Eid);

            String postData=new Gson().toJson(bean);
            HttpSender.sendPushfc(postData, mNetService, new ReqCallBack<JSONObject>() {
                @Override
                public void onReqSuccess(JSONObject result) {
                    Log.i("cui---","result = "+result.toString());
                    dismissDialog();
                    try {
                        Integer code=(Integer) result.get("code");
                        String msg=result.getString("msg");
                        String timestamp=result.getString("timestamp");
                        bean.setTimestamp(timestamp);
                        if(code!=null && code == 0){
                            //发送成功之后自己先插入朋友圈 显示朋友圈内容
                            Intent intent =new Intent(LocationActivity.this,FriendsActivity.class);
                            intent.putExtra("listBean",bean);
                            startActivity(intent);
                            finish();
                        }else{
                            Utils.toastShow(LocationActivity.this,"errorCode="+code);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("cui",""+e.getMessage());
                        dismissDialog();
                    }
                }
                @Override
                public void onReqFailed(String errorMsg) {
                    dismissDialog();
                    Utils.toastShow(LocationActivity.this,"errorMsg = "+errorMsg);
                }
            });
        }else{
            Utils.toastShow(LocationActivity.this,getString(R.string.no_network));
        }
    }

}