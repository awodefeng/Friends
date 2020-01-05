package com.xxun.watch.xunfriends.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.base.BaseApplication;
import com.xxun.watch.xunfriends.bean.HeadBean;
import com.xxun.watch.xunfriends.bean.LikelistBean;
import com.xxun.watch.xunfriends.bean.ListBean;
import com.xxun.watch.xunfriends.bean.ListDataSave;
import com.xxun.watch.xunfriends.bean.UserBean;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import com.xxun.watch.xunfriends.utils.DateUtils;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;
import com.xxun.watch.xunfriends.widget.CircleTransform;
import com.xxun.watch.xunfriends.widget.GlideCircleTransform;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

/**
 * @author cuiyufeng
 * @Description: LikeAdapter
 * @date 2019/1/2 10:01
 */
public class LikeAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<LikelistBean> likelist;
    private BaseApplication myApp;
    private XiaoXunNetworkManager mNetService;
    private String Eid;

    public LikeAdapter(Activity context, ArrayList<LikelistBean> list, BaseApplication myApp) {
        this.context = context;
        this.likelist = list;
        this.myApp=myApp;
        mNetService = myApp.getNetService();
        Eid=mNetService.getWatchEid();
    }

    @Override
    public int getCount() {
        return likelist != null && likelist.size() > 0 ? likelist.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return likelist != null && likelist.size() > 0 ? likelist.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_like, null);

            holder.iv_likehead= (ImageView) convertView.findViewById(R.id.iv_likehead);
            holder.tv_likename = (TextView) convertView.findViewById(R.id.tv_likename);
            holder.tv_liketime = (TextView) convertView.findViewById(R.id.tv_liketime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LikelistBean likelistBean= likelist.get(position);
        //holder.tv_likename.setText(""+likelistBean.getNickName());
        if(!TextUtils.isEmpty(likelist.get(position).getTimestamp())){
            //时间
            Date xundata = null;
            try {
                xundata = DateUtils.getDate(likelist.get(position).getTimestamp(), DateUtils.XUN_TIME);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tv_liketime.setText(""+DateUtils.showTimeText(xundata));
        }

        String eid=likelistBean.getEID();
        //通过eid获取头像 异步请求 然后显示 （要存本地 eid key  byte value  检索eid 没有就添加到缓存 有的话直接使用byte）
        dvsinfoGetHead(context,eid,myApp.getNetService(),holder.iv_likehead,holder.tv_likename);
        return convertView;
    }
    class ViewHolder {
        ImageView iv_likehead;
        TextView tv_likename;
        TextView tv_liketime;
    }

    private void dvsinfoGetHead(final Activity activity, final String eid, XiaoXunNetworkManager mNetService, final ImageView view,final TextView likename){
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
                    //Log.i("cui","result = "+result.toString());
                    Gson gson = new Gson();
                    UserBean bean = (UserBean)gson.fromJson(result.toString(), UserBean.class);

                    if(bean.getCode() ==0){
                        String name= bean.getNickName();
                        Log.i("cui","name="+name);
                        if(!TextUtils.isEmpty(bean.getNickName())){
                            likename.setText(""+bean.getNickName());
                        }

                        if(!TextUtils.isEmpty(bean.getHead())){
                            HeadBean headBean = (HeadBean)gson.fromJson(bean.getHead(), HeadBean.class);
                            if(!TextUtils.isEmpty(headBean.getHead_image_date())){
                                byte [] head=android.util.Base64.decode(headBean.getHead_image_date(),android.util.Base64.NO_WRAP);
                                Glide.with(activity)
                                        .load(head)
                                        .centerCrop()
                                        .placeholder(R.mipmap.bg_findfriends)
                                        .bitmapTransform(new GlideCircleTransform(activity))
                                        .dontAnimate()
                                        .into(view);
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

}