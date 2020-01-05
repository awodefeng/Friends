package com.xxun.watch.xunfriends.net;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.xxun.watch.xunfriends.utils.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.smart.uploadfile.BASE64Encoder;
import com.xiaoxun.smart.uploadfile.AESUtil;
/**
 * @author cuiyufeng
 * @Description:  okhttp网络请求类
 * @date 2018/4/2 10:28
 */
public class RequestManager {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");//mdiatype  这个类型一般是键值对 这个需要和服务端保持一致
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");//mdiatype 这个类型一般是字符串（加密时用的多） 这个需要和服务端保持一致
    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream"); //默认的一般都是二进制对象流

    private static final String TAG = RequestManager.class.getSimpleName();
    private static volatile RequestManager mInstance;//单利引用
    public static final int TYPE_GET = 0;//get请求
    public static final int TYPE_POST_JSON = 1;//post请求参数为json
    public static final int TYPE_POST_FORM = 2;//post请求参数为表单
    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private Handler okHttpHandler;//全局处理子线程和M主线程通信
    private Context context;
    /**
     * 初始化RequestManager
     */
    public RequestManager(Context context) {
        this.context=context;
        //初始化OkHttpClient
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS)//设置写入超时时间
                //.sslSocketFactory(ArApplication.getSSLContext(context).getSocketFactory())
                .build();
        //初始化Handler
        okHttpHandler = new Handler(context.getMainLooper());
    }

    /**
     * 获取单例引用
     * @return
     */
    public static RequestManager getInstance(Context context) {
        RequestManager inst = mInstance;
        if (inst == null) {
            synchronized (RequestManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new RequestManager(context.getApplicationContext());
                    mInstance = inst;
                }
            }
        }
        return inst;
    }


    /**
     * okHttp异步请求统一入口
     * @param actionUrl   接口地址
     * @param requestType 请求类型
     * @param jsonObject   请求参数
     * @param  sid  sid不加密提供给服务解密用（有就传没有就空）
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @param  isEncryption 是否加密
     **/
    public <T> Call requestAsyn(String actionUrl, int requestType, String jsonObject, XiaoXunNetworkManager mNetService, ReqCallBack<T> callBack , boolean isEncryption) {
        Call call = null;
        switch (requestType) {
            case TYPE_GET:
                //call = requestGetByAsyn(actionUrl, jsonObject, callBack);
                break;
            case TYPE_POST_JSON:
               call = requestPostByAsyn(actionUrl, jsonObject, mNetService,callBack,isEncryption);
                break;
            case TYPE_POST_FORM:
                // call = requestPostByAsynWithForm(actionUrl, paramsMap, callBack);
                break;
        }
        return call;
    }


    /**
     * okHttp post异步请求
     * @param actionUrl 接口地址
     * @param jsonObject 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @param sid sid加密，给服务器解密用除登陆接口
     * @param isEncryption 是否加密
     * @return
     */
    private <T> Call requestPostByAsyn(String actionUrl, String jsonObject,XiaoXunNetworkManager mNetService, final ReqCallBack<T> callBack , final boolean isEncryption) {
        try {
            String params = "" ;
            if(isEncryption){
                if(mNetService!=null){
                    //Log.i(TAG, "----aesKey-----" + mNetService.getAESKey());
                    //Log.i(TAG, "----sid-----" + mNetService.getSID());
                    params = BASE64Encoder.encode(AESUtil.encryptAESCBC(jsonObject.toString(), mNetService.getAESKey(), mNetService.getAESKey()) )+mNetService.getSID();
                }else{
//                    params = Base64.encodeBytes(RSACoder.encryptByPublicKey(aes_Key.getBytes(), CloudBridgeUtil.RSA_PUBLIC_KEY))
//                            + "#" + Base64.encodeBytes(AESCBC.encryptAESCBC(jsonObject.toString(), aes_Key, aes_Key));
                }
            }else{
                params=jsonObject.toString();
            }
            RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, params);
            String requestUrl = String.format("%s",actionUrl);
            //Log.e(TAG, "CUI---------" + requestUrl+"-----body:"+params);
            Log.e(TAG, "CUI---------requestUrl:" + requestUrl+"-----jsonObJect:"+jsonObject);
            Request request = addHeaders().url(requestUrl).post(body).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("Access failed", callBack);
                    Log.e(TAG, e.toString());
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i(TAG,"CUI:onResponse="+response.toString());
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        //Log.i(TAG,"response.body="+result);
                        if(!TextUtils.isEmpty(result) && result !=null && result.length()>4){
                            String inInfo = "";
                            if(isEncryption){
                                //Log.e(TAG, "result ==== :" + result);
                                inInfo = new String(AESUtil.decryptAESCBC(android.util.Base64.decode(result,android.util.Base64.NO_WRAP), mNetService.getAESKey(), mNetService.getAESKey()));
                            }else{
                                inInfo = result;
                            }
                            Log.e(TAG, "inInfo = :" + inInfo);
                            JSONObject json = null;
                            try {
                                json = new JSONObject(inInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            successCallBack((T) json, callBack);
                        }else {
                            //Log.i(TAG,"CUI:isEmpty="+result);
                            //int errcode= Integer.parseInt(result);
                            failedCallBack(result, callBack);

                        }
                    } else {
                        failedCallBack("Server error", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }



    /**
     * okHttp get异步请求
     * @param actionUrl 接口地址
     * @param jsonObject 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @return
     */
    private <T> Call requestGetByAsyn(String actionUrl, JSONObject jsonObject, final ReqCallBack<T> callBack) {
        try {
        String params = jsonObject.toString();
//        String data = Base64.encodeBytes(RSACoder.encryptByPublicKey(ArApplication.getAESKey().getBytes(), CloudBridgeUtil.RSA_PUBLIC_KEY))
//                + "#" + Base64.encodeBytes(AESCBC.encryptAESCBC(params.toString(), ArApplication.getAESKey(), ArApplication.getAESKey()));

            String requestUrl = String.format("%s?%s",actionUrl, params);
            Request request = addHeaders().url(requestUrl).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("Upload failure", callBack);
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        // Log.e(TAG, "response ----->" + string);
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack("Server error", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

//    *
//     * 5.)不带进度文件下载
//     * 下载文件
//     * @param fileUrl 文件url
//     * @param destFileDir 存储目标目录

    public <T> void downLoadFile(String fileUrl, final String destFileDir, final ReqCallBack<T> callBack) {

        final File file = new File(destFileDir);
        if (file.exists()) {
            file.delete();
        }
        final Request request = new Request.Builder().url(fileUrl).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
                failedCallBack("Download failed", callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.e(TAG, "current------>" + current);
                    }
                    fos.flush();
                    successCallBack((T) file, callBack);
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    failedCallBack("Download failed", callBack);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }

    /**
     * 6.）带进度文件下载
     * 下载文件
     * @param fileUrl 文件url
     * @param destFileDir 存储目标目录
     */
    public <T> void downLoadFile(String fileUrl, final String destFileDir, final ReqProgressCallBack<T> callBack) {
        final File file = new File(destFileDir);
        if (file.exists()) {
            file.delete();
        }
        final Request request = new Request.Builder().url(fileUrl).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
                failedCallBack("dowanload error", callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        progressCallBack(total, current, callBack);
                    }
                    fos.flush();
                    successCallBack((T) file, callBack);
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    failedCallBack("dowanload error", callBack);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }


    /**
     * 上传文件 不带参数上传文件
     * @param uploadUrl 接口地址
     * @param file  本地文件地址
     */
    public <T> void upLoadFile(String uploadUrl, File file, final ReqCallBack<T> callBack) {
        //创建RequestBody
        RequestBody body = RequestBody.create(MEDIA_OBJECT_STREAM, file);
        //创建Request
        final Request request = new Request.Builder().url(uploadUrl).post(body).build();

        final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
                failedCallBack("Upload failure", callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Log.e(TAG, "response ----->" + string);
                    successCallBack((T) string, callBack);
                } else {
                    failedCallBack("Upload failure", callBack);
                }
            }
        });
    }

    /**
     * 统一处理进度信息
     * @param total    总计大小
     * @param current  当前进度
     * @param callBack
     * @param <T>
     */
    private <T> void progressCallBack(final long total, final long current, final ReqProgressCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onProgress(total, current);
                }
            }
        });
    }

    /**
     * 统一为请求添加头信息
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Charset", "UTF-8");
        return builder;
    }

    /**
     * 统一同意处理成功信息
     * @param result
     * @param callBack
     * @param <T>
     */
    private <T> void successCallBack(final T result, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

    /**
     * 统一处理失败信息
     * @param errorMsg
     * @param callBack
     * @param <T>
     */
    private <T> void failedCallBack(final String errorMsg, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorMsg);
                }
            }
        });
    }


}
