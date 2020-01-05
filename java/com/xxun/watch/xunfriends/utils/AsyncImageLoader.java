package com.xxun.watch.xunfriends.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.xxun.watch.xunfriends.activity.FriendsActivity;
import com.xxun.watch.xunfriends.base.BaseApplication;
import com.xxun.watch.xunfriends.bean.HeadBean;
import com.xxun.watch.xunfriends.bean.UserBean;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class AsyncImageLoader {

    private static final String TAG = "AsyncImageLoader ";

    private static final int IMAGE_CACHE_MIN_SIZE = 1024 * 1024 * 2;

    private BaseApplication mApp;
    private LruCache<String, Drawable> lruImageCache;

    private AsyncImageLoader(BaseApplication app) {

        mApp = app;
        int cacheSize = IMAGE_CACHE_MIN_SIZE;

        ActivityManager manager = (ActivityManager) mApp.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        int memClass = manager.getMemoryClass();
        int suitTableMemorySize = (memClass * 1024 * 1024 / 8);

        if (cacheSize < suitTableMemorySize)
            cacheSize = suitTableMemorySize;

        lruImageCache = new LruCache<String, Drawable>(cacheSize) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Drawable value) {
                int size = 0;
                BitmapDrawable drawable = (BitmapDrawable) value;
                Bitmap bitmap = drawable.getBitmap();
                if (bitmap != null)
                    size = bitmap.getWidth() * bitmap.getHeight() * 4;
                return size;
            }
        };
    }

    private static AsyncImageLoader instance;
    public synchronized static AsyncImageLoader getInstance(BaseApplication app){
        if(instance==null)
            instance=new AsyncImageLoader(app);
        return instance;
    }

    /**
     * isUpdateHead true 读取缓存 false 不读缓存直接去网络下载(必然)
     */
    public Drawable load(final String Eid, final XiaoXunNetworkManager mNetService,final ImageCallBack callBack,boolean isUpdateHead) {
        if (TextUtils.isEmpty(Eid))
            return null;
        Drawable drawable;
        //内存中获取
        drawable = lruImageCache.get(Eid);
        if (drawable != null && isUpdateHead) {
            return drawable;
        }else{
            Log.i("cui","内存没有这个图片 eid"+Eid);
        }

        // 从本地存储获取
        File destFile = new File(mApp.getIconCacheDir(), Eid + ".jpg");
        if (destFile.exists() && isUpdateHead) {
            Log.i("cui","本地有这个图片 destFile.getPath() = "+destFile.getPath());

            BitmapDrawable bitdrawable = new BitmapDrawable(mApp.getResources(), destFile.getPath());
            Bitmap original = bitdrawable.getBitmap();
            if (original != null) {
                drawable = bitdrawable;
                put(Eid, drawable);
            }
            callBack.onReqSuccess(destFile);
        } else {
            Log.i("cui","本地没有这个图片 eid"+Eid);
            // 从服务器获取
            JSONObject obj =new JSONObject();
            try {
                obj.put("EID",Eid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpSender.dvsinfo(obj.toString(),mNetService,new ReqCallBack<JSONObject>() {
                @Override
                public void onReqSuccess(JSONObject result) {
                    Log.i("cui","dvsinfo = "+result.toString());
                    try {
                    Gson gson = new Gson();
                    UserBean bean = (UserBean)gson.fromJson(result.toString(), UserBean.class);
                    if(bean.getCode() ==0){
                        if(!TextUtils.isEmpty(bean.getHead())){
                            HeadBean headBean = (HeadBean)gson.fromJson(bean.getHead(), HeadBean.class);
                            if(!TextUtils.isEmpty(headBean.getHead_image_date())){
                               byte[] bitmapArray =android.util.Base64.decode(headBean.getHead_image_date(),android.util.Base64.NO_WRAP);

                               //先清除内存eid这个图片
                                lruImageCache.remove(Eid);

                                File headfile = new File(mApp.getIconCacheDir(), Eid + ".jpg");
                                FileOutputStream out = new FileOutputStream(headfile);
                                out.write(bitmapArray);
                                out.close();
                                Log.i("cui","下载图片保存本地成功");
                                callBack.onReqSuccess(headfile);
                            }
                        }else{
                            callBack.onReqSuccess(null);
                        }
                    }else{
                        Log.i("cui","获取头像、昵称---errorCode = "+bean.getCode());
                    }
                        BaseApplication.getInstance().setUserBean(bean);
                    } catch (Exception e) {
                        Log.i("cui","e= "+e);
                        e.printStackTrace();
                    }
                }
                @Override
                public void onReqFailed(String errorMsg) {
                    Log.i("cui","获取头像、昵称---errorMsg = "+errorMsg);
                }
            });
        }
        return drawable;
    }

    private void put(String headKey, Drawable drawable) {
        if (TextUtils.isEmpty(headKey) || drawable == null)
            return;
        lruImageCache.put(headKey, drawable);
    }

}
