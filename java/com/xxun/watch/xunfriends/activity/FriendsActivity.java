package com.xxun.watch.xunfriends.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.adapter.FriendsAdapter;
import com.xxun.watch.xunfriends.adapter.MoodAdapter;
import com.xxun.watch.xunfriends.base.BaseActivity;
import com.xxun.watch.xunfriends.base.BaseApplication;
import com.xxun.watch.xunfriends.bean.FriendsBean;
import com.xxun.watch.xunfriends.bean.ListBean;
import com.xxun.watch.xunfriends.bean.ListDataSave;
import com.xxun.watch.xunfriends.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.xiaoxun.sdk.XiaoXunNetworkManager;
import org.json.JSONException;
import org.json.JSONObject;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import android.view.Window;
/**
 * @author cuiyufeng
 * @Description: Friends 朋友圈
 * @date 2018/12/21 15:37
 */
public class FriendsActivity extends BaseActivity {
    private PullToRefreshListView listView;
    private ArrayList<ListBean> lisdata;
    FriendsAdapter adapter;
    ListBean listBean;
    private XiaoXunNetworkManager mNetService;
    private  String Eid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        setContentView(R.layout.activity_friends);
        Intent intent = this.getIntent();
        listBean= (ListBean) intent.getSerializableExtra("listBean");
        initView();
        mNetService = myApp.getNetService();
        Eid = mNetService.getWatchEid();
        initData();
        initReceiver();

    }

    private void initView(){
        listView = (PullToRefreshListView)findViewById(R.id.pull_refresh_friendslist);
        lisdata= new ArrayList<ListBean>();
        adapter = new FriendsAdapter(FriendsActivity.this,lisdata);
        listView.setAdapter(adapter);

        /*listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });*/

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        //设置监听事件
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                //刷新回调
                if(NetUtil.checkNet(FriendsActivity.this)){
                    lisdata.clear();
                    getFriendsList(true);
                }else{
                    listView.onRefreshComplete();
                    Utils.toastShow(FriendsActivity.this,getString(R.string.no_network));
                }
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                //上拉加载更多
                if(lisdata.size()>49){
                    getFriendsList(false);
                }else{
                    Log.i("cui","总数据还没到50条");
                    listView.onRefreshComplete();
                }
            }
        });



        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.toastShow(FriendsActivity.this,""+position);
            }
        });*/
    }

    private void initData(){
        ArrayList<ListBean> list= BaseApplication.getInstance().getFriendsList();
        if(listBean!=null && list!=null){
            list.add(0,listBean);
            Log.i("cui","list.size1="+list.size());
        }

        if(list!=null&&list.size()>0){
            lisdata.addAll(list);
            adapter.notifyDataSetChanged(list);
        }else{
            Utils.toastShow(FriendsActivity.this,getString(R.string.publis_friends));
        }
    }


    private void getFriendsList(final boolean isSaveLoac){
        //加载更多 需要传入朋友圈list最后一条的markKey
        String key ="";
        int size=lisdata.size();
        if(size == 0){
            Date d = new Date();
            d.setYear(d.getYear()+1);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            String timestamp = format.format(d).toString();
            String time = Utils.getReversedOrderTime(timestamp);
            key ="EP/"+Eid+"/FCIRCLE/"+time;
        }else{
            ListBean bean=lisdata.get(size-1);
            key=bean.getKey();
        }
        Log.i("cui","朋友圈size=="+size+": key = "+key );
        String postData = "";
        JSONObject obj =new JSONObject();
        try {
            obj.put("markKey",key);
            postData=obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(NetUtil.checkNet(FriendsActivity.this)){
            HttpSender.listfc(postData,mNetService,new ReqCallBack<JSONObject>() {
                @Override
                public void onReqSuccess(JSONObject result) {
                    listView.onRefreshComplete();
                    if(result !=null){
                        Gson gson = new Gson();
                        FriendsBean bean = (FriendsBean)gson.fromJson(result.toString(), FriendsBean.class);
                        if(bean.getCode() == 0){
                            ArrayList<ListBean> listBeans= bean.getList();
                            if(listBeans.size() == 0){
                                Utils.toastShow(FriendsActivity.this,getString(R.string.last_data));
                            }else{
                                //下拉刷新时刷新数据用
                                if(isSaveLoac){
                                    BaseApplication.getInstance().setFriendsList(listBeans);
                                    //加载成功之后添加到缓存
                                    ListDataSave listData=new ListDataSave(FriendsActivity.this);
                                    listData.setDataList(listBeans);
                                }
                                lisdata.addAll(listBeans);
                                adapter.notifyDataSetChanged(lisdata);
                            }
                        }else{
                            Utils.toastShow(FriendsActivity.this,"errorCode = "+bean.getCode());
                        }
                    }else{
                        Utils.toastShow(FriendsActivity.this,getString(R.string.last_data));
                    }
                }
                @Override
                public void onReqFailed(String errorMsg) {
                    Log.e("cui","errorMsg = "+errorMsg);
                    listView.onRefreshComplete();
                }
            });
        }else{
            Utils.toastShow(FriendsActivity.this,getString(R.string.no_network));
        }
    }

    private BroadcastReceiver mReceiver;
    public static final String ACTION_DOWNLOAD_HEADDATA_FINISH = "xiaoxun.download.headdata.finish";
    private void initReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(ACTION_DOWNLOAD_HEADDATA_FINISH.equals(intent.getAction())){
                    adapter.notifyDataSetChanged();
                    //adapter.notifyDataSetChanged(lisdata);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DOWNLOAD_HEADDATA_FINISH);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

}