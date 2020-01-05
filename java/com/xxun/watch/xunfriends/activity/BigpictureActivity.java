package com.xxun.watch.xunfriends.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.base.BaseActivity;
import com.xxun.watch.xunfriends.base.BaseApplication;
import com.xxun.watch.xunfriends.utils.ImageCallBack;
import com.xxun.watch.xunfriends.utils.ImageUtil;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

import java.io.File;
import android.view.Window;
import com.xxun.watch.xunfriends.widget.GlideCircleTransform;
import com.bumptech.glide.Glide;
/**
 * @author cuiyufeng
 * @Description: BigpictureActivity
 * @date 2019/1/21 10:44
 */
public class BigpictureActivity extends BaseActivity{
    private ImageView iv_bigpicture;
    private String bigpicture;
    private String eid;
    private XiaoXunNetworkManager mNetService;
    private BaseApplication myApp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);

        setContentView(R.layout.activity_bigpicture);
        bigpicture=getIntent().getStringExtra("bigpicture");
        eid=getIntent().getStringExtra("eid");
        myApp = (BaseApplication)getApplication();
        mNetService = myApp.getNetService();
        initView();
    }

    private void initView(){
        iv_bigpicture = (ImageView)findViewById(R.id.iv_bigpicture);
        if (!TextUtils.isEmpty(bigpicture)){
            //Glide.with(BigpictureActivity.this).load(bigpicture).into(iv_bigpicture);
            showDialog("加载大图片");
            Glide.with(BigpictureActivity.this)
                    .load(bigpicture)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Log.i("cui","onException");
                            dismissDialog();
                            Utils.toastShow(BigpictureActivity.this,getString(R.string.no_network));
                            finish();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Log.i("cui","onResourceReady");
                            dismissDialog();
                            return false;
                        }
                    })
                    .into(iv_bigpicture);
        }else if(!TextUtils.isEmpty(eid)){
            if(NetUtil.checkNet(BigpictureActivity.this)){
                Drawable drawable= myApp.getHeadDrawable(myApp.getResources(), eid, mNetService, R.mipmap.bg_findfriends, new ImageCallBack<File>() {
                    @Override
                    public void onReqSuccess(File result) {
                        if(result==null){
                            Glide.with(BigpictureActivity.this)
                                    .load(R.mipmap.bg_findfriends)
                                    .centerCrop()
                                    .bitmapTransform(new GlideCircleTransform(BigpictureActivity.this))
                                    .dontAnimate()
                                    .into(iv_bigpicture);

                        }else{
                            Log.i("cui","BigpictureActivity getAbsolutePath - = "+ result.getAbsolutePath());
                            BitmapDrawable bitdrawable = new BitmapDrawable(myApp.getResources(), result.getAbsolutePath());
                            ImageUtil.setMaskImage(iv_bigpicture, R.mipmap.bg_findfriends, bitdrawable);
                            myApp.sendBroadcast(new Intent(FriendsActivity.ACTION_DOWNLOAD_HEADDATA_FINISH));
                        }
                    }
                },false);

            }else{
                Utils.toastShow(BigpictureActivity.this,getString(R.string.no_network));
                finish();
            }
        }
    }

}