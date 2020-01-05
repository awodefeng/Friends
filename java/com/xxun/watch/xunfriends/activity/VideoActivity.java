package com.xxun.watch.xunfriends.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.base.BaseActivity;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;
import android.view.Window;

/**
 * @author cuiyufeng
 * @Description: VideoActivity
 * @date 2019/3/4 9:42
 */
public class VideoActivity extends BaseActivity{
    private String videourl;
    private VideoView mVideoView;
    private ImageView iv_revideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);

        setContentView(R.layout.activity_video);
        videourl=getIntent().getStringExtra("videourl");
        Log.i("cui","videourl = "+videourl);
        initView();
    }

    public void initView(){
        mVideoView = (VideoView)this.findViewById(R.id.videoView );
        iv_revideo = (ImageView) this.findViewById(R.id.iv_revideo);
        if(NetUtil.checkNet(VideoActivity.this)){
            showDialog("播放视频");
            if(!TextUtils.isEmpty(videourl)){
                try {
                    //设置视频控制器
                    mVideoView.setMediaController(new MediaController(this));
                    Uri uri = Uri.parse( videourl );
                    mVideoView.setVideoURI(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mVideoView.start();
            }else{
                Utils.toastShow(VideoActivity.this,"url is null");
                finish();
            }
            setupVideo();
        }else{
            Utils.toastShow(VideoActivity.this,getString(R.string.no_network));
            finish();
        }
    }

    private void setupVideo() {
        iv_revideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("cui","mVideoView.isPlaying()="+mVideoView.isPlaying());
                if(!mVideoView.isPlaying()){
                    mVideoView.resume();
                    showDialog("播放视频");
                    iv_revideo.setVisibility(View.GONE);
                }
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                dismissDialog();
                mVideoView.start();
                iv_revideo.setVisibility(View.GONE);
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaybackVideo();
                //播放完毕增加一个按钮从新播放
                iv_revideo.setVisibility(View.VISIBLE);
                Utils.toastShow(VideoActivity.this,"播放完了！");
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                dismissDialog();
                stopPlaybackVideo();
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mVideoView.isPlaying()) {
            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
        Log.i("cui","onPause");
        if (mVideoView.canPause()) {
            mVideoView.pause();
            Log.i("cui","canPause");
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaybackVideo();
    }

    private void stopPlaybackVideo() {
        try {
            mVideoView.stopPlayback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
