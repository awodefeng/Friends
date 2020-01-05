package com.xxun.watch.xunfriends.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.base.BaseActivity;
import com.xxun.watch.xunfriends.bean.ListBean;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import com.xxun.watch.xunfriends.utils.CheckAudioPermission;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;

import org.json.JSONObject;
import android.widget.RelativeLayout;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xxun.watch.xunfriends.widget.DragLayout;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author cuiyufeng
 * @Description: TextActivity
 * @date 2018/12/21 16:49
 */
public class TextActivity extends BaseActivity  implements RecognitionListener {
    private Button btn_frist_recording,btn_second_recording,btn_confirm;
    private LinearLayout lout_recording_confirm;
    private TextView tv_recording;

    private static final String TAG = "cui";
    private SpeechRecognizer mSpeechRecognizer;
    private Intent recognizerIntent;
    private String asrResult;//录音结果
    private XiaoXunNetworkManager mNetService;
    private RelativeLayout rout_recording_confirm;
    //private DragLayout root_view;
    private ScrollView sv_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("cui","TextActivity-onCreate");
        //getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        setContentView(R.layout.activity_text);
        mNetService = myApp.getNetService();
        initView();
        initRecognizer();
        initListener();
        setGestureListener();
    }

    private void initView(){
        rout_recording_confirm =(RelativeLayout) findViewById(R.id.rout_recording_confirm);
        btn_frist_recording =(Button) findViewById(R.id.btn_frist_recording);
        lout_recording_confirm =(LinearLayout) findViewById(R.id.lout_recording_confirm);
        btn_second_recording =(Button) findViewById(R.id.btn_second_recording);
        btn_confirm =(Button) findViewById(R.id.btn_confirm);
        tv_recording =(TextView) findViewById(R.id.tv_recording);
        sv_text = (ScrollView) findViewById(R.id.sv_text);

    }

    private void initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mSpeechRecognizer.setRecognitionListener(this);
        } else {
            Toast.makeText(this, "Recognition UnAvailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void initListener(){
        btn_frist_recording.setOnLongClickListener(new startRecordListener());
        btn_frist_recording.setOnClickListener(new stopRecordListener());

        btn_second_recording.setOnLongClickListener(new startRecordListener());
        btn_second_recording.setOnClickListener(new stopRecordListener());

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(asrResult)){
                    Utils.toastShow(TextActivity.this,TextActivity.this.getString(R.string.say_something));
                    //finish();
                    return;
                }
                // TODO: 2018/12/27 <调用> 发布text语音接口
                publisText();
            }
        });
    }

    private void publisText(){
        if(NetUtil.checkNet(TextActivity.this)){
            showDialog("");
            final ListBean bean =new ListBean();
            bean.setType("text");
            bean.setContent(""+asrResult);
            Log.i("cui","getName = "+myApp.getNickName());
            bean.setNickName(""+myApp.getNickName());
            String Eid=mNetService.getWatchEid();
            bean.setEID(Eid);
            String postData=new Gson().toJson(bean);

            HttpSender.sendPushfc(postData,mNetService,new ReqCallBack<JSONObject>() {
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
                            Intent intent = new Intent(TextActivity.this, FriendsActivity.class);
                            intent.putExtra("listBean", bean);
                            startActivity(intent);
                            finish();

                        } else {
                            Utils.toastShow(TextActivity.this, "errorCode=" + code);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("cui",""+e.getMessage());
                    }
                }
                @Override
                public void onReqFailed(String errorMsg) {
                    Utils.toastShow(TextActivity.this,"errorMsg = "+errorMsg);
                    dismissDialog();
                }
            });
        }else{
            Utils.toastShow(TextActivity.this,getString(R.string.no_network));
        }
    }

   /* @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }*/

    //长按录音,松开后自动执行短按操作
    private int isLongClick=0;
    class startRecordListener implements View.OnLongClickListener {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onLongClick(View v) {
            isLongClick = 1;
            if (mSpeechRecognizer == null) {
                Toast.makeText(TextActivity.this, "Recognition UnAvailable", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED) {
                tv_recording.setText("正在录音");
                Log.e("cui","startRecordListener");
                startRecognize();
                //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                Log.e("cui","没权限");
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
            return false; //KeyPoint:setOnLongClickListener中return的值决定是否在长按后再加一个短按动作,true为不加短按,false为加入短按
        }
    }

    //短按停止录音,直接点击短按无效
    class stopRecordListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e("cui","stopRecordListener");
            if(isLongClick==1){
                if (mSpeechRecognizer == null) {
                    Toast.makeText(TextActivity.this, "Recognition UnAvailable", Toast.LENGTH_SHORT).show();
                    return;
                }
                mSpeechRecognizer.stopListening();
                isLongClick=0;
                //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }else{
                Utils.toastShow(TextActivity.this,TextActivity.this.getString(R.string.long_press_recording));
            }
        }
    }

    private void startRecognize() {
        initRecognizerIntent();
        mSpeechRecognizer.startListening(recognizerIntent);
    }

    private void initRecognizerIntent() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    @Override
    protected void onDestroy() {
        mSpeechRecognizer.destroy();
        super.onDestroy();
        Log.i("cui","TextActivity-onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("cui","TextActivity-onStop");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(TAG, " onReadyForSpeech params=" + params.toString());
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, " onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
//        Log.i(TAG, " onRmsChanged rmsdB=" + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
//        Log.i(TAG, " onBufferReceived buffer.length=" + buffer.length);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, " onEndOfSpeech ");
    }

    @Override
    public void onError(int error) {
        Log.i(TAG, " onError error=" + error);
        //错误提示Utils.toastShow(TextActivity.this,"onError = "+error);
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(TAG, " onResults results=" + results.toString());
        List<String> asrResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (asrResults != null) {
           Log.i("cui","asrResults.size():"+asrResults.size());
           for (String asrResult : asrResults) {
                Log.i(TAG, "onResults asrResult=" + asrResult);
                this.asrResult=asrResult;
                    if(TextUtils.isEmpty(asrResult)){
                        tv_recording.setText("正在录音");
                    }else{
                        tv_recording.setText("" + asrResult);
                        btn_frist_recording.setVisibility(View.GONE);
                        lout_recording_confirm.setVisibility(View.VISIBLE);
                    }
           }
        }

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
//        Log.i(TAG, " onPartialResults partialResults=" + partialResults.toString());
        List<String> asrPartialResults = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (asrPartialResults != null) {
            for (String asrPartialResult : asrPartialResults) {
                Log.i(TAG, "onPartialResults asrPartialResults=" + asrPartialResult);
                if (TextUtils.isEmpty(asrPartialResult)) {
                    tv_recording.setText("正在录音");
                } else {
                    tv_recording.setText("" + asrPartialResult);
                }
            }
        }
    }


    @Override
    public void onEvent(int eventType, Bundle params) {

    }



    /**
     * 设置上下滑动作监听器
     * @author jczmdeveloper
     */
     private float startX;
    private void setGestureListener(){
        sv_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startX = event.getX();
                    } else if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
                        float endX = event.getX();
                        float disX = endX - startX;
                        if (Math.abs(disX) > 80) {
                            if (disX > 80 )
                                finish();
                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }


}
