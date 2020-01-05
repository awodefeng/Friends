package com.xxun.watch.xunfriends.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.adapter.LikeAdapter;
import com.xxun.watch.xunfriends.adapter.MoodAdapter;
import com.xxun.watch.xunfriends.base.BaseActivity;
import com.xxun.watch.xunfriends.bean.LikelistBean;
import com.xxun.watch.xunfriends.bean.ListBean;

import java.util.ArrayList;
import java.util.List;
import com.xxun.watch.xunfriends.utils.Utils;
import android.content.pm.PackageManager;
import android.view.Window;

/**
 * @author cuiyufeng
 * @Description: PraiseListActivity
 * @date 2018/12/26 15:30
 */
public class PraiseListActivity extends BaseActivity{
    private PullToRefreshListView listView;
    private LikeAdapter adapter;
    private RelativeLayout rout_blank_page;
    private static ArrayList<LikelistBean> likelist = new ArrayList<>();
    private ImageView iv_blank_page;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);

        setContentView(R.layout.activity_mood); // 复用心情activity lout
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        likelist = (ArrayList<LikelistBean>) bundle.getSerializable("likeList");
        if(likelist !=null){
            Log.i("cui","likelist.size = "+likelist.size());
        }
        initView();
        initData();
    }
    private void initView(){
        listView =(PullToRefreshListView)findViewById(R.id.pull_refresh_moodlist);
        rout_blank_page=(RelativeLayout)findViewById(R.id.rout_blank_page);
        iv_blank_page = (ImageView)findViewById(R.id.iv_blank_page);
        iv_blank_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.xxun.xunaddnew", "com.xxun.xunaddnew.MainActivity");
                intent.setComponent(componentName);
                startActivity(intent);
                /*try {
                    PackageManager packageManager = getPackageManager();
                    Intent intent = new Intent();
                    intent = packageManager.getLaunchIntentForPackage("com.xxun.xunaddnew");
                    if (intent == null) {
                        Log.i("cui", "未安装");
                    } else {
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.i("cui", "e==" + e);
                }*/
            }
        });
    }

    private void initData(){
        if(likelist!=null && likelist.size()==0){
            rout_blank_page.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else{
            rout_blank_page.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            adapter = new LikeAdapter(PraiseListActivity.this,likelist,myApp);
            listView.setAdapter(adapter);

            listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                    listView.onRefreshComplete();
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(PraiseListActivity.this,""+position,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}