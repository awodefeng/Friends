package com.xxun.watch.xunfriends.net;

import com.xxun.watch.xunfriends.base.BaseApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

/**
 * @author cuiyufeng
 * @Description: HttpSender
 * @date 2018/4/2 10:28
 */
public class HttpSender {
	/**
	 * 描述 发布朋友圈
	 * @param jsonObject
	 * @param callBack
	 */
	public static void  sendPushfc(String jsonObject, XiaoXunNetworkManager mNetService , ReqCallBack<JSONObject> callBack){
		BaseApplication.req.requestAsyn(WbApi.pushfc, RequestManager.TYPE_POST_JSON, jsonObject,mNetService, callBack,true);
	}

	/**
	 * 描述 拉取朋友圈：
	 * @param jsonObject
	 * @param callBack
	 */
	public static void listfc(String jsonObject,XiaoXunNetworkManager mNetService , ReqCallBack<JSONObject> callBack){
		BaseApplication.req.requestAsyn(WbApi.listfc, RequestManager.TYPE_POST_JSON, jsonObject, mNetService,callBack,true);
	}

	/**
	 * 描述 朋友圈点赞：
	 * @param jsonObject
	 * @param sid
	 * @param callBack
	 */
	public static void likefc(String jsonObject , XiaoXunNetworkManager mNetService , ReqCallBack<JSONObject> callBack){
		BaseApplication.req.requestAsyn(WbApi.likefc, RequestManager.TYPE_POST_JSON, jsonObject, mNetService ,callBack,true);
	}

	/**
	 * 描述 朋友圈点赞：
	 * @param jsonObject
	 * @param sid
	 * @param callBack
	 */
	public static void dvsinfo(String jsonObject , XiaoXunNetworkManager mNetService , ReqCallBack<JSONObject> callBack){
		BaseApplication.req.requestAsyn(WbApi.dvsinfo, RequestManager.TYPE_POST_JSON, jsonObject, mNetService ,callBack,true);
	}

	/**
	 * 描述 检查朋友圈是否有更新：
	 * @param jsonObject
	 * @param callBack
	 */
	public static void checkfc(String jsonObject,XiaoXunNetworkManager mNetService , ReqCallBack<JSONObject> callBack){
		BaseApplication.req.requestAsyn(WbApi.checkfc, RequestManager.TYPE_POST_JSON, jsonObject, mNetService,callBack,true);
	}

/*     RequestManager.getInstance(activity).upLoadFile(uploadUrl, file, new ReqCallBack<String>() {
		@Override
		public void onReqSuccess (String result){
		}
		@Override
		public void onReqFailed (String errorMsg){
		}
	});*/

	/**
	 * 预留AR项目中的第二种方式
	 * @param roomCode
     * @param callBack
     */
	public static void putReport(String roomCode, ReqCallBack<String> callBack){
		String urlPath = new String(WbApi.pushfc);
		String param= null;
		JSONObject jObject=new JSONObject();
		String jsonstring = "";
		try {
			jObject.put("roomCode",  roomCode);
			jsonstring = jObject.toString();
			System.out.println("######"+jsonstring);
//			param = URLEncoder.encode(jsonstring,"UTF-8");
			//建立连接
			URL url= null;
			url = new URL(urlPath);
			System.out.println("######"+url);
			HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();

			//设置参数
			httpConn.setDoOutput(true);   //需要输出
			httpConn.setDoInput(true);   //需要输入
			httpConn.setUseCaches(false);  //不允许缓存
			httpConn.setRequestMethod("POST");   //设置POST方式连接
			//设置请求属性
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpConn.setRequestProperty("Charset", "UTF-8");
			//连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
			httpConn.connect();
			//建立输入流，向指向的URL传入参数
			DataOutputStream dos=new DataOutputStream(httpConn.getOutputStream());
			dos.writeBytes(jsonstring);
			dos.flush();
			dos.close();
			int resultCode=httpConn.getResponseCode();
			if(HttpURLConnection.HTTP_OK==resultCode){
				StringBuffer sb=new StringBuffer();
				String readLine=new String();
				BufferedReader responseReader=new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"UTF-8"));
				while((readLine=responseReader.readLine())!=null){
					sb.append(readLine).append("\n");
				}
				responseReader.close();
				callBack.onReqSuccess(sb.toString());
				System.out.println(sb.toString());
			}else{
				callBack.onReqFailed("upload error");
				System.out.println("######error*****************************************####");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
