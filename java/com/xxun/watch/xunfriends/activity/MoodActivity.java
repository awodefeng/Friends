package com.xxun.watch.xunfriends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.adapter.MoodAdapter;
import com.xxun.watch.xunfriends.base.BaseActivity;
import com.xxun.watch.xunfriends.bean.ListBean;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import android.view.Window;

/**
 * @author cuiyufeng
 * @Description: MoodActivity
 * @date 2018/12/21 16:49
 */
public class MoodActivity extends BaseActivity{
    private ArrayList<ListBean> lisdata ;
    private PullToRefreshListView listView;
    private MoodAdapter adapter;
    private XiaoXunNetworkManager mNetService;

    private int[] intdrawable;
    private String[] content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        setContentView(R.layout.activity_mood);
        mNetService=myApp.getNetService();
        initView();
        initData();
    }

    private void initView(){
        listView =(PullToRefreshListView)findViewById(R.id.pull_refresh_moodlist);
    }

    private void initData(){
        lisdata = new ArrayList<ListBean>();//本地心情列表
        intdrawable = new int[]{R.mipmap.ic_amazing_big,R.mipmap.ic_cry_big,R.mipmap.ic_depressed_big,R.mipmap.ic_dislike_big,R.mipmap.ic_happy_big,R.mipmap.ic_insidious_big,R.mipmap.ic_laugh_big,R.mipmap.ic_love_big,R.mipmap.ic_no_big,R.mipmap.ic_o_big,
                                    R.mipmap.ic_play_big,R.mipmap.ic_shy_big,R.mipmap.ic_thankyout_big,R.mipmap.ic_what_big,R.mipmap.ic_work_big};
        content = new String[]{"厉害了","哭泣","沮丧","嫌弃","开心","阴险","贱笑","爱你","不行","哦",
                "皮一下","害羞","谢谢","what","加班"};

        for (int i = 0; i < intdrawable.length; i++) {
            lisdata.add(new ListBean(intdrawable[i],content[i]));
        }
        adapter = new MoodAdapter(MoodActivity.this,lisdata);
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                //loadMore();
                listView.onRefreshComplete();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 2018/12/27 <调用> 点击列表心情发布心情
                publisMood(lisdata.get(position).getContent());

            }
        });
    }

    private void publisMood(String content){
        if(NetUtil.checkNet(MoodActivity.this)){
            showDialog("");

            final ListBean bean =new ListBean();
            bean.setType("emoji");
            bean.setContent(content);
            Log.i("cui","getName = "+myApp.getNickName());
            bean.setNickName(""+myApp.getNickName());
            String Eid=mNetService.getWatchEid();
            bean.setEID(Eid);

            String postData=new Gson().toJson(bean);
            HttpSender.sendPushfc(postData,mNetService , new ReqCallBack<JSONObject>() {
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
                            Intent intent = new Intent(MoodActivity.this, FriendsActivity.class);
                            intent.putExtra("listBean", bean);
                            startActivity(intent);
                            finish();
                        } else {
                            Utils.toastShow(MoodActivity.this, "errorCode=" + code);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("cui",""+e.getMessage());
                        dismissDialog();
                    }
                }
                @Override
                public void onReqFailed(String errorMsg) {
                    Utils.toastShow(MoodActivity.this,"errorMsg = "+errorMsg);
                    dismissDialog();
                }
            });
        }else{
            Utils.toastShow(MoodActivity.this,getString(R.string.no_network));
        }
    }

    private void loadMore() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lisdata.add(new ListBean());
                lisdata.add(new ListBean());
                lisdata.add(new ListBean());
                lisdata.add(new ListBean());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        listView.onRefreshComplete();
                    }
                });
            }
        }.start();
    }

}