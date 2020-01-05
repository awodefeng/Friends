package com.xxun.watch.xunfriends.net;
/**
 * @Description:用于回调
 */
public abstract class CallBack {
	public interface ReturnCallback<T>{
		public void backSuccess(T obj);
		public void onFailure(Throwable t, String msg);
		public void onLoading(long total, long current);
	}
	
}
