package com.xxun.watch.xunfriends.base;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.bean.HeadBean;
import com.xxun.watch.xunfriends.bean.ListDataSave;
import com.xxun.watch.xunfriends.bean.UserBean;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import com.xxun.watch.xunfriends.net.RequestManager;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import com.xxun.watch.xunfriends.bean.ListBean;
import com.xxun.watch.xunfriends.utils.AsyncImageLoader;
import com.xxun.watch.xunfriends.utils.ImageCallBack;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;
import com.xxun.watch.xunfriends.widget.GlideCircleTransform;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author cuiyufeng
 * @Description: BaseApplication
 * @date 2018/12/21 15:46
 */
public class BaseApplication extends Application {
    public static RequestManager req;
    public static Context _context;
    public static Resources _resources;
    private static BaseApplication baseApplication = null;
    private ArrayList<ListBean> friendsList;
    private XiaoXunNetworkManager mNetService;
    private UserBean userBean;
    public static boolean isFrastLoadingHead = false; //头像是否已经填充 true 已经被填充 false 还没有被填充
    //public boolean isLike =false; // 是否点赞 true 已点赞需要刷新列表 false 未点赞 读取缓存
    // file
    public static final String XUNFRIENDS_BASE_DIR = "FRIENDS";
    public static final String XUNFRIENDS_ICON_DIR = "icon2";
    private String nickName;

    @Override
    public void onCreate() {
        super.onCreate();
        _context=getApplicationContext();
        _resources=_context.getResources();
        baseApplication = this;
        req=  RequestManager.getInstance(this);
        mNetService = (XiaoXunNetworkManager) getSystemService("xun.network.Service");
        initFile();
    }

    public XiaoXunNetworkManager getNetService() {
        return mNetService;
    }

    public static BaseApplication getInstance() {
        return baseApplication;
    }

    public ArrayList<ListBean> getFriendsList() {
        if(friendsList!=null){
            return friendsList;
        }else{
            return new ArrayList<ListBean>();
        }
    }
    public void setFriendsList(ArrayList<ListBean> friendsList) {
        this.friendsList = friendsList;
    }

    public UserBean getUserBean() {
        if(userBean!=null){
            return userBean;
        }
        return new UserBean();
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public static void dvsinfo(final Activity activity, final String eid, XiaoXunNetworkManager mNetService, final ImageView view){
        JSONObject obj =new JSONObject();
        try {
            obj.put("EID",eid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(NetUtil.checkNet(activity)){
            HttpSender.dvsinfo(obj.toString(),mNetService,new ReqCallBack<JSONObject>() {
                @Override
                public void onReqSuccess(JSONObject result) {
                    Gson gson = new Gson();
                    UserBean bean = (UserBean)gson.fromJson(result.toString(), UserBean.class);

                    if(bean.getCode() ==0){
                        if(!TextUtils.isEmpty(bean.getHead())){
                            HeadBean headBean = (HeadBean)gson.fromJson(bean.getHead(), HeadBean.class);
                            if(!TextUtils.isEmpty(headBean.getHead_image_date())){
                                //本地头像和接口获取头像不一致
                               String  head_key =ListDataSave.getShareValue(activity,"head_key","");
                               if(TextUtils.isEmpty(head_key) || !head_key.equals(headBean.getHead_image_date())){
                                    Log.i("cui","头像改变并存储起来");
                                    ListDataSave.setShareValue(activity,"head_key",headBean.getHead_image_date());
                                    byte [] head=android.util.Base64.decode(headBean.getHead_image_date(),android.util.Base64.NO_WRAP);
                                    bean.setHeadbyte(head);
                                    Glide.with(activity)
                                            .load(head)
                                            .centerCrop()
                                            .placeholder(R.mipmap.bg_findfriends)
                                            .bitmapTransform(new GlideCircleTransform(activity))
                                            .dontAnimate()
                                            .into(view);
                                    isFrastLoadingHead =true;//表示头像已经被填充加载
                                   try {
                                       File headfile = new File(getIconCacheDir(), eid + ".jpg");
                                       FileOutputStream out = new FileOutputStream(headfile);
                                       out.write(head);
                                       out.close();
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                                }else{
                                    Log.i("cui","头像一样读缓存去了");
                                }
                            }
                        }
                    }else{
                        Utils.toastShow(activity,"获取头像、昵称---errorCode = "+bean.getCode());
                    }
                    BaseApplication.getInstance().setUserBean(bean);
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

    private static File baseDir;
    private static File iconFileDir;
    public static File getIconCacheDir() {
        if (!iconFileDir.isDirectory()) {
            iconFileDir.delete();
            iconFileDir.mkdirs();
        }
        return iconFileDir;
    }

    private void initFile() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            if (baseDir == null)
                baseDir = new File(Environment.getExternalStorageDirectory(), XUNFRIENDS_BASE_DIR);
            else
                baseDir = new File(baseDir.getPath());

            iconFileDir = new File(baseDir, XUNFRIENDS_ICON_DIR);
            if (iconFileDir.exists() && !iconFileDir.isDirectory()) {
                iconFileDir.delete();
            }
            if (!iconFileDir.exists()) {
                iconFileDir.mkdir();
            }
        }
    }

    public Drawable getHeadDrawable(Resources res, String eid, XiaoXunNetworkManager mNetService, int defaultId,ImageCallBack callBack,boolean isUpdateHead) {
        Log.i("cui","eid = "+eid+": isUpdateHead= "+isUpdateHead);
        AsyncImageLoader imgLoader = AsyncImageLoader.getInstance(this);
        Drawable drawable = imgLoader.load(eid, mNetService, callBack,isUpdateHead);
        if (drawable == null) {
            drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(res, defaultId));
        }
        return drawable;
    }

}