package com.xxun.watch.xunfriends.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.xiaoxun.smart.uploadfile.OnUploadResult;
import com.xiaoxun.smart.uploadfile.ProgressListener;
import com.xiaoxun.smart.uploadfile.UploadFile;
import com.xxun.watch.xunfriends.R;
import com.xxun.watch.xunfriends.base.BaseActivity;
import com.xxun.watch.xunfriends.bean.ListBean;
import com.xxun.watch.xunfriends.net.HttpSender;
import com.xxun.watch.xunfriends.net.ReqCallBack;
import com.xxun.watch.xunfriends.utils.ImageFactory;
import com.xxun.watch.xunfriends.utils.NetUtil;
import com.xxun.watch.xunfriends.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import android.view.Window;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import com.vincent.videocompressor.VideoCompress;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * @author cuiyufeng
 * @Description: PhotoActivity 相册
 * @date 2018/12/21 16:44
 */
public class PhotoActivity extends BaseActivity implements View.OnClickListener{
    private RelativeLayout rout_picture,rout_takepicture;
    private XiaoXunNetworkManager mNetService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        setContentView(R.layout.activity_photo);
        mNetService = myApp.getNetService();
        initView();
    }

    private void initView(){
        rout_picture=(RelativeLayout)findViewById(R.id.rout_picture);
        rout_takepicture=(RelativeLayout)findViewById(R.id.rout_takepicture);
        rout_picture.setOnClickListener(this);
        rout_takepicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rout_picture:
                selectPhoto();
                break;
            case R.id.rout_takepicture:
                selectTakePicture();
                break;
        }
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
            Log.i("cui","删除文件成功");
        }
    }

    private void selectPhoto() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.xxun.xungallery", "com.xxun.xungallery.MainPhotoActivity");
        intent.putExtra("select_photo", 1);
        intent.setComponent(componentName);
        startActivityForResult(intent, 1);
    }

    private void selectTakePicture() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.xxun.xuncamera", "com.xxun.xuncamera.CameraMainActivity");
        intent.putExtra("tag_take_photo", 1);
        intent.setComponent(componentName);
        startActivityForResult(intent, 1);
    }

    /*调用相册start*/



    String VIDEO_TYPE = "video";
    String IMAGE_TYPE = "photo";
    String mEID = "";
    String mGID = "";
    String mToken = "";
    String mAES_KEY = "";
    String mImgOriginalPath ="";
    String previewFilePath ="";
    File file2 = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("cui","requestCode ============================== "+requestCode);
        switch (requestCode) {
            case 1:
                if(data!=null){
                    showDialog("");
                    mImgOriginalPath=  data.getStringExtra("select_photo");
                    if(TextUtils.isEmpty(mImgOriginalPath)){
                        mImgOriginalPath=  data.getStringExtra("result_take_photo");
                        Log.i("cui","相机回调数据正常"+mImgOriginalPath);
                    }else{
                        Log.i("cui","相册回调数据正常 start Time");
                    }
                    Log.d("cui ", "mImgOriginalPath 数据源 = " + mImgOriginalPath);
                    mGID = mNetService.getWatchGid();
                    mEID = mNetService.getWatchEid();
                    mToken = mNetService.getSID();
                    mAES_KEY = mNetService.getAESKey();
                    //try {
                    //在手表中创建一个文件
                    previewFilePath = Environment.getExternalStoragePublicDirectory("") + "/Friends/";
                    File file1 = new File(previewFilePath);
                    if (!file1.exists()) {
                        file1.mkdir();
                    }
                    String name = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance()) + ".jpg";
                    file2 = new File(previewFilePath,name);

                    String type = mImgOriginalPath.substring(mImgOriginalPath.length()-3);
                    Bitmap bitmap = null;
                    if(type.equals("jpg") || type.equals("png")){
                        //图片压缩保存到文件
                        bitmap = ImageFactory.compressByResolution(mImgOriginalPath,240,240);
                        try {
                            if(bitmap!=null){
                                FileOutputStream fileout = new FileOutputStream(file2);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileout);
                                fileout.flush();
                                fileout.close();
                            }
                            Log.i("cui", "压缩图片已经保存");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            dismissDialog();
                        }
                    }else{
                        try {
                            Bitmap bitmapvideo = null ;
                            MediaMetadataRetriever mmr=new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
                            String pathvideo_substring = "";
                            if(mImgOriginalPath.contains("file:///")){
                                pathvideo_substring=mImgOriginalPath.substring(mImgOriginalPath.indexOf("///"));
                                Log.i("cui","pathvideo_substring = "+pathvideo_substring);
                            }else{
                                pathvideo_substring = mImgOriginalPath;
                            }
                            Log.i("cui","pathvideo_substring = "+pathvideo_substring);
                            File file=new File(pathvideo_substring);//实例化File对象，文件路径为/storage/sdcard/Movies/music1.mp4
                            if(file.exists()) {
                                mmr.setDataSource(file.getAbsolutePath());//设置数据源为该文件对象指定的绝对路径
                                bitmapvideo = mmr.getFrameAtTime();//获得视频第一帧的Bitmap对象
                            }else {
                                Log.i("cui","视频文件不存在");
                            }
                            if(bitmapvideo!=null){
                                Log.i("cui", "file2*** = "+file2);
                                FileOutputStream fileout = new FileOutputStream(file2);
                                bitmapvideo.compress(Bitmap.CompressFormat.JPEG, 20, fileout);
                                fileout.flush();
                                fileout.close();
                            }else{
                                Log.i("cui", "bitmapvideo is null");
                                dismissDialog();
                            }
                            Log.i("cui", "视频第一帧压缩图片已经保存");
                        } catch (Exception e) {
                            Log.e("cui","mp4e = "+e.getMessage()+"**********"+e.toString());
                            e.printStackTrace();
                            dismissDialog();
                        } catch (Throwable throwable) {
                            Log.i("cui", "--"+throwable);
                            throwable.printStackTrace();
                            dismissDialog();
                        }
                    }
                    Log.i("cui","file2.getAbsolutePath() = "+file2.getAbsolutePath());
                    if(mImgOriginalPath.contains("file://")){
                        String orig= mImgOriginalPath.substring(mImgOriginalPath.indexOf("///"));
                        //uploadFilesLocal(mToken, IMAGE_TYPE, mEID, mGID, orig, file2.getAbsolutePath(),mAES_KEY);
                        Log.i("cui","orig=========="+orig);

                        Compress_Video(orig);
                    }else if(mImgOriginalPath.contains(".mp4")){
                        Log.i("cui","Compress_Video---orig=========="+mImgOriginalPath);
                        Compress_Video(mImgOriginalPath);
                    }else{
                        Log.i("cui","mImgOriginalPath=========="+mImgOriginalPath);
                        uploadFilesLocal(mToken, IMAGE_TYPE, mEID, mGID, mImgOriginalPath, file2.getAbsolutePath(),mAES_KEY);
                    }
                    Log.i("cui","相册回调 end Time");
                }else{
                    Log.i("cui","相册回调数据是空的");
                }
        }
    }


    /**
     * [uploadFilesLocal 上传文件]
     * @param token           [标志位]
     * @param type            [文件类型]
     * @param eid             [EID]
     * @param gid             [GID]
     * @param filePath        [原文件路径]
     * @param previewFilePath [预览文件路径]
     *
     *  上传结果通过OnUploadResult返回
     *  文件上传成功只是表示服务器收到了文件，最终需要服务器发送给家长APP端才是真正的分享成功
     *  服务器发送文件给家长APP端通过uploadNotice完成，结果在UploadFileCallback回调
     */
    private void uploadFilesLocal(final String token, String type, final String eid, final String gid, final String filePath, final String previewFilePath,final String mAES_KEY) {
        UploadFile uploadFile = new UploadFile(PhotoActivity.this, token, mAES_KEY);

        Log.d("cui", "uploadFilesLocal start Time >> filePath " + filePath + " " + previewFilePath);
        uploadFile.uploadFile(token, type, eid, gid, filePath, previewFilePath,
                new ProgressListener() {
                    @Override
                    public void transferred(long l) {
                        //Log.d("xxx", "[transferred] >> l : " + l);
                    }
                },
                new OnUploadResult() {
                    @Override
                    public void onResult(String s) {
                        Log.d("cui", "[OnUploadResult] >> onResult : " + s);
                        //dismissDialog();
                        if (s.contains("success")) {
                            // upload success
                            //每次选择照片的时候把 原来保存的删除
                            File file = new File(previewFilePath);
                            deleteFile(file);

                            String urlall=s.substring(8,s.length());
                            Log.i("cui","urlall="+urlall);
                            String[] url= urlall.split("_");
                            String original = url[0];
                            String thumbnail = url[1];
                            //Log.i("cui","original="+original + ": thumbnail = "+thumbnail);
                            Log.i("cui","uploadFilesLocal end Time");

                            //发布朋友圈
                            if(original.contains("mp4")){
                                publisImage(original,thumbnail,"video");

                                //视频上传成功之后 需要删除压缩的视屏
                                File file_video = new File(filePath);
                                deleteFile(file_video);
                            }else{
                                publisImage(original,thumbnail,"image");
                            }

                        }else{
                            dismissDialog();
                            Utils.toastShow(PhotoActivity.this,s);
                        }
                    }
                });
    }


    private void publisImage(String srcUrl,String miniUrl,String type){
        Log.i("cui","publishimage start Time");
        if(NetUtil.checkNet(PhotoActivity.this)){
            final ListBean bean =new ListBean();
            bean.setType(type);
            bean.setSrcUrl(srcUrl);
            bean.setMiniUrl(miniUrl);
            bean.setContent("this is image");
            Log.i("cui","getName = "+myApp.getNickName());

            bean.setNickName(""+myApp.getNickName());
            String Eid=mNetService.getWatchEid();
            bean.setEID(Eid);

            String postData=new Gson().toJson(bean);
            HttpSender.sendPushfc(postData,mNetService , new ReqCallBack<JSONObject>() {
                @Override
                public void onReqSuccess(JSONObject result) {
                    dismissDialog();
                    Log.i("cui","publishimage end Time");
                    try {
                        Integer code=(Integer) result.get("code");
                        String msg=result.getString("msg");
                        String timestamp=result.getString("timestamp");
                        bean.setTimestamp(timestamp);
                        if(code!=null && code == 0){
                            //发送成功之后自己先插入朋友圈 显示朋友圈内容
                            Intent intent =new Intent(PhotoActivity.this,FriendsActivity.class);
                            intent.putExtra("listBean",bean);
                            startActivity(intent);
                        }else{
                            Utils.toastShow(PhotoActivity.this,"errorCode="+code);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("cui",""+e.getMessage());
                        dismissDialog();
                    }
                }
                @Override
                public void onReqFailed(String errorMsg) {
                    Utils.toastShow(PhotoActivity.this,"errorMsg = "+errorMsg);
                    dismissDialog();
                }
            });
        }else{
            Utils.toastShow(PhotoActivity.this,getString(R.string.no_network));
        }
    }

    /*调用相册end*/

    private String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    private String Compress_Video(String inputpath){
       final String destPath = outputDir + File.separator + "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss", getLocale()).format(new Date()) + ".mp4";
        Log.i("cui","output= "+outputDir+"---destPath = "+destPath);
        //String inputpath ="/storage/emulated/0/DCIM/Camera/VID_20190514_105040.mp4";
        VideoCompress.compressVideoLow(inputpath, destPath, new VideoCompress.CompressListener() {
            @Override
            public void onStart() {
                Log.i("cui","Compress"+"Start at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()));
            }

            @Override
            public void onSuccess() {
                uploadFilesLocal(mToken, IMAGE_TYPE, mEID, mGID, destPath, file2.getAbsolutePath(),mAES_KEY);
                Log.i("cui","Compress Success!"+ "End at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()));
            }

            @Override
            public void onFail() {
                dismissDialog();
                Log.i("cui","Compress Failed!!!"+ new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()));
            }

            @Override
            public void onProgress(float percent) {
                //Log.i("cui","Compress"+String.valueOf(percent) + "%");
            }
        });
        return destPath;
    }
    private Locale getLocale() {
        Configuration config = getResources().getConfiguration();
        Locale sysLocale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = getSystemLocale(config);
        } else {
            sysLocale = getSystemLocaleLegacy(config);
        }

        return sysLocale;
    }

    @SuppressWarnings("deprecation")
    public static Locale getSystemLocaleLegacy(Configuration config){
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config){
        return config.getLocales().get(0);
    }

}
