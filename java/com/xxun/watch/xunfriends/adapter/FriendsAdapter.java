package com.xxun.watch.xunfriends.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.activity.BigpictureActivity;
import com.xxun.watch.xunfriends.activity.FriendsActivity;
import com.xxun.watch.xunfriends.activity.MainActivity;
import com.xxun.watch.xunfriends.activity.PraiseListActivity;
import com.xxun.watch.xunfriends.activity.TextActivity;
import com.xxun.watch.xunfriends.activity.VideoActivity;
import com.xxun.watch.xunfriends.base.BaseApplication;
import com.xxun.watch.xunfriends.bean.FriendsBean;
import com.xxun.watch.xunfriends.bean.LikelistBean;
import com.xxun.watch.xunfriends.bean.ListBean;
import com.xxun.watch.xunfriends.bean.ListDataSave;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import com.xxun.watch.xunfriends.utils.AsyncImageLoader;
import com.xxun.watch.xunfriends.utils.DateUtils;
import com.xxun.watch.xunfriends.utils.ImageCallBack;
import com.xxun.watch.xunfriends.utils.ImageUtil;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xxun.watch.xunfriends.widget.GlideCircleTransform;

/**
 * @author cuiyufeng
 * @Description: FriendsAdapter
 * @date 2018/12/26 11:06
 */
public class FriendsAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<ListBean> listBeans;
    private ArrayList<ListBean> listBeansChange = new ArrayList<>(); // 点赞的时候替换原来的list 更换内存地址

    private BaseApplication myApp;
    private XiaoXunNetworkManager mNetService;
    private String Eid;
    HashMap<String,String> hashMap=new HashMap<String,String>();

    public FriendsAdapter(Activity context, ArrayList<ListBean> list) {
        this.context = context;
        this.listBeans = list;
        myApp = (BaseApplication) context.getApplication();
        mNetService = myApp.getNetService();
        Eid=mNetService.getWatchEid();

        /*HashMap<String,byte[]> hashMap=new HashMap<String,byte[]>();
        for (int i = 0; i < list.size(); i++) {
            hashMap.put(list.get(i).getEID(),null);
        }
        ListDataSave.putHashMapData(context,hashMap);*/
    }

    @Override
    public int getCount() {
        return listBeans != null && listBeans.size() > 0 ? listBeans.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listBeans != null && listBeans.size() > 0 ? listBeans.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position,View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friends_list, null);
            holder.tv_friends_nickname = (TextView) convertView.findViewById(R.id.tv_friends_nickname);
            holder.tv_friends_time = (TextView) convertView.findViewById(R.id.tv_friends_time);

            holder.iv_friends_picture= (ImageView) convertView.findViewById(R.id.iv_friends_picture);
            holder.iv_friends_locationicon= (ImageView) convertView.findViewById(R.id.iv_friends_locationicon);
            holder.tv_friends_content= (TextView) convertView.findViewById(R.id.tv_friends_content);
            holder.iv_video= (ImageView) convertView.findViewById(R.id.iv_video);
            holder.iv_item_head= (ImageView) convertView.findViewById(R.id.iv_item_head);

            holder.rout_friends_praise = (RelativeLayout) convertView.findViewById(R.id.rout_friends_praise);
            holder.tv_friends_praisenumber = (TextView) convertView.findViewById(R.id.tv_friends_praisenumber);
            holder.iv_friends_praise= (ImageView) convertView.findViewById(R.id.iv_friends_praise);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Log.i("cui","FriendsAdapter listBeans size = "+listBeans.size() +"----------"+position);
        final ListBean listBean=listBeans.get(position);
        final ArrayList<LikelistBean> likeList = listBean.getLikelist();//点赞列表
        if(likeList.size()>0){
            holder.tv_friends_praisenumber.setText(""+likeList.size());
        }else{
            holder.tv_friends_praisenumber.setText("");
        }
        //Log.i("cui","点赞列表大小：="+likeList.size());
        if(likeList.size()>0){
            //好友中是否包含自己
            for (int i = 0; i < likeList.size(); i++) {
                if (likeList.get(i).getEID().equals(Eid)){
                    //有自己说明已经点过赞了
                    holder.iv_friends_praise.setBackgroundResource(R.mipmap.praise_res);
                    break;
                }else{
                    //在判断这个item 是不是自己发送的
                    if(listBean.getEID().equals(Eid)){
                        //是自己发送的并且还在点赞列表中说明有人给你点过赞了
                        holder.iv_friends_praise.setBackgroundResource(R.mipmap.praise_res);
                        break;
                    }else{
                        //item不是自己发的，点赞列表没有自己说明还未点过赞
                        holder.iv_friends_praise.setBackgroundResource(R.mipmap.praise);
                    }
                }
            }
        }else{
            holder.iv_friends_praise.setBackgroundResource(R.mipmap.praise);
        }

        if(!TextUtils.isEmpty(listBean.getNickName())){
            holder.tv_friends_nickname.setText(""+listBean.getNickName());
        }else{
            holder.tv_friends_nickname.setText(context.getString(R.string.frieds_baby));
        }
        //时间
        Date xundata = null;
        try {
            xundata = DateUtils.getDate(listBean.getTimestamp(), DateUtils.XUN_TIME);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_friends_time.setText(""+DateUtils.showTimeText(xundata));

        //头像显示.............
        Drawable drawable= myApp.getHeadDrawable(myApp.getResources(), listBean.getEID(), mNetService, R.mipmap.bg_findfriends, new ImageCallBack<File>() {
            @Override
            public void onReqSuccess(File result) {
                //Log.i("cui","头像显示 getAbsolutePath() = = "+ result.getAbsolutePath());
                BitmapDrawable bitdrawable = new BitmapDrawable(myApp.getResources(), result.getAbsolutePath());
                ImageUtil.setMaskImage(holder.iv_item_head, R.mipmap.bg_findfriends, bitdrawable);
            }
        },true);
        //Log.i("cui","头像显示2 getAbsolutePath() = = "+ listBean.getEID());
        ImageUtil.setMaskImage(holder.iv_item_head, R.mipmap.bg_findfriends, drawable);

        String type=listBean.getType();
        //Log.i("cui","type = "+type);
        if(type.equals("image")){
            holder.iv_friends_picture.setVisibility(View.VISIBLE);
            holder.iv_friends_locationicon.setVisibility(View.GONE);
            holder.tv_friends_content.setVisibility(View.GONE);
            Glide.with(context).load(listBean.getMiniUrl()).into(holder.iv_friends_picture);
            Log.i("cui","image");
            holder.iv_video.setVisibility(View.GONE);

        }else if(type.equals("video")){
            holder.iv_friends_picture.setVisibility(View.VISIBLE);
            holder.iv_friends_locationicon.setVisibility(View.GONE);
            holder.tv_friends_content.setVisibility(View.GONE);
            Glide.with(context).load(listBean.getMiniUrl()).into(holder.iv_friends_picture);
            holder.iv_video.setVisibility(View.VISIBLE);
            Log.i("cui","video");

        }else if(type.equals("emoji") ){
            holder.iv_friends_picture.setVisibility(View.GONE);
            holder.iv_friends_locationicon.setVisibility(View.VISIBLE);
            holder.tv_friends_content.setVisibility(View.VISIBLE);
            //holder.tv_friends_content.setText(""+listBean.getContent());
            //表情显示
            setMoodDrawable(listBean.getContent(),holder.iv_friends_locationicon);
            holder.tv_friends_content.setText(listBean.getContent());
            holder.iv_video.setVisibility(View.GONE);

        }else if(type.equals("loc") ){
            holder.iv_friends_picture.setVisibility(View.GONE);
            holder.iv_friends_locationicon.setVisibility(View.VISIBLE);
            holder.tv_friends_content.setVisibility(View.VISIBLE);
            holder.iv_friends_locationicon.setBackgroundResource(R.mipmap.icon_locationlist);
            holder.tv_friends_content.setText(""+listBean.getLoc().getDesc());
            holder.iv_video.setVisibility(View.GONE);

        }else if(type.equals("text")){
            holder.iv_friends_picture.setVisibility(View.GONE);
            holder.iv_friends_locationicon.setVisibility(View.GONE);
            holder.tv_friends_content.setVisibility(View.VISIBLE);
            holder.tv_friends_content.setText(""+listBean.getContent());
            holder.iv_video.setVisibility(View.GONE);
        }

        if(type.equals("video")){
            holder.iv_friends_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(context, VideoActivity.class);
                    intent.putExtra("videourl",listBean.getSrcUrl());
                    context.startActivity(intent);
                }
            });
        }else{
            holder.iv_friends_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(context, BigpictureActivity.class);
                    intent.putExtra("bigpicture",listBean.getSrcUrl());
                    context.startActivity(intent);
                }
            });
        }
        holder.iv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, VideoActivity.class);
                intent.putExtra("videourl",listBean.getSrcUrl());
                context.startActivity(intent);
            }
        });

        holder.rout_friends_praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //自己跳转点赞列表，他人调用点赞接口。（） 受限判断这个item 是不是自己发的（防止自己发过朋友圈<本地数据>  还去点赞 会有bug）
                //Log.i("cui","Eid = "+Eid+":listBean.toString() = "+listBean.toString());
                if(listBean.getEID().equals(Eid)){
                    Log.i("cui","自己");
                    Intent intent = new Intent(context, PraiseListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("likeList", (Serializable) likeList);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }else{
                    //发布点赞 方案1 直接调用 返回-181 说明已经点过赞了  方案2、判断点赞列表是否包含自己说明已经点赞过了，反之没有点赞
                    if(likeList.size()==0){
                        Log.i("cui","还没人点赞，开始点赞");
                        String key = listBean.getKey();
                        likePublish(key,position,listBean,holder.iv_friends_praise, holder.tv_friends_praisenumber);
                    }else{
                        boolean flag = false;
                        for (int i = 0; i < likeList.size(); i++) {
                            if (likeList.get(i).getEID().equals(Eid)){
                                flag = true;
                                Log.i("cui","有自己，表示已经点赞过了");
                                Utils.toastShow(context,context.getString(R.string.dont_repeat_praise));
                                break;
                            }
                        }

                        if(!flag){
                            Log.i("cui","没自己，开始点赞");
                            String key = listBean.getKey();
                            likePublish(key,position,listBean,holder.iv_friends_praise, holder.tv_friends_praisenumber);
                        }
                    }
                }
            }
        });

        holder.iv_item_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击头像放大图
                Intent intent =new Intent(context, BigpictureActivity.class);
                intent.putExtra("eid",listBean.getEID());
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_friends_nickname;
        TextView tv_friends_time;

        ImageView iv_friends_picture;// 图片

        RelativeLayout rout_friends_praise;
        TextView tv_friends_praisenumber;
        ImageView iv_friends_praise;
        ImageView iv_friends_locationicon;
        TextView tv_friends_content;
        ImageView iv_video;
        ImageView iv_item_head;
    }

    public void notifyDataSetChanged(ArrayList<ListBean> list) {
        this.listBeans = list;
        notifyDataSetChanged();
    }

    public void likePublish(String key, final int position,final ListBean listBean, final ImageView praise, final TextView praisenumber){
        Log.i("cui","position = "+position+" key = "+key );
        //myApp.showDialog("wait");
        String postData = "";
        JSONObject obj =new JSONObject();
        try {
            obj.put("Key",key);
            postData=obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(NetUtil.checkNet(context)){
            HttpSender.likefc(postData,mNetService,new ReqCallBack<JSONObject>() {
                @Override
                public void onReqSuccess(JSONObject result) {
                    //myApp.dismissDialog();
                    Log.i("cui","result------------------ = "+result.toString());
                    LikelistBean likelistBean = new LikelistBean();
                    likelistBean.setEID(Eid);
                    Log.i("cui","getNickName = "+myApp.getNickName());

                    likelistBean.setNickName(myApp.getNickName());
                    likelistBean.setHead("");
                    likelistBean.setTimestamp("");//服务端没返回暂时保留空的
                    try {
                        Integer code=(Integer) result.get("code");
                        String msg=result.getString("msg");
                        //String timestamp=result.getString("timestamp");
                        //防止已经点赞过了有自己的eid 重复添加（点赞数会错误）
                        if(code!=null && code == 0){
                            ArrayList<LikelistBean> likelist=listBean.getLikelist();
                            likelist.add(0,likelistBean);
                            listBean.setLikelist(likelist);
                            listBeans.set(position,listBean);
                            //Log.i("cui","listBeans .size = "+listBeans.size());
                            Log.i("cui","本地点赞列表：listBean.tostring = "+listBean.toString());
                            //listBeansChange.clear();
                            //listBeansChange.addAll(listBeans);
                            notifyDataSetChanged(listBeans);
                            //通知外面 要刷新接口了
                            Log.i("cui","setShareValue islike = true");
                            ListDataSave.setShareValue(context,"islike","true");
                        } else if(code == -181){
                            Log.e("cui",""+result.toString());
                            Utils.toastShow(context, context.getString(R.string.dont_repeat_praise));
                        }else{
                            Log.e("cui",""+result.toString());
                            Utils.toastShow(context, "errorCode=" + code);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("cui",""+e.getMessage());
                    }
                }
                @Override
                public void onReqFailed(String errorMsg) {
                    Utils.toastShow(context,"errorMsg = "+errorMsg);
                }
            });
        }else{
            Utils.toastShow(context,context.getString(R.string.no_network));
        }
    }



    private void setMoodDrawable(String content,ImageView view){
        if(content.equals("what")){
            view.setBackgroundResource(R.mipmap.ic_what);
        }else if(content.equals("爱你")){
            view.setBackgroundResource(R.mipmap.ic_love);
        }else if(content.equals("不行")){
            view.setBackgroundResource(R.mipmap.ic_no);
        }else if(content.equals("害羞")){
            view.setBackgroundResource(R.mipmap.ic_shy);
        }else if(content.equals("加班")){
            view.setBackgroundResource(R.mipmap.ic_work);
        }else if(content.equals("贱笑")){
            view.setBackgroundResource(R.mipmap.ic_laugh);
        }else if(content.equals("沮丧")){
            view.setBackgroundResource(R.mipmap.ic_depressed);
        }else if(content.equals("开心")){
            view.setBackgroundResource(R.mipmap.ic_happy);
        }else if(content.equals("哭泣")){
            view.setBackgroundResource(R.mipmap.ic_cry);
        }else if(content.equals("厉害了")){
            view.setBackgroundResource(R.mipmap.ic_amazing);
        }else if(content.equals("哦")){
            view.setBackgroundResource(R.mipmap.ic_o);
        }else if(content.equals("皮一下")){
            view.setBackgroundResource(R.mipmap.ic_play);
        }else if(content.equals("嫌弃")){
            view.setBackgroundResource(R.mipmap.ic_dislike);
        }else if(content.equals("谢谢")){
            view.setBackgroundResource(R.mipmap.ic_thankyout);
        }else if(content.equals("阴险")){
            view.setBackgroundResource(R.mipmap.ic_insidious);
        }else{
            view.setBackgroundResource(R.mipmap.ic_what);
        }

    }
}
