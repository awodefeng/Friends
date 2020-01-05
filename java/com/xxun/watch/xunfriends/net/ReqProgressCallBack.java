package com.xxun.watch.xunfriends.net;

/**
 * @author cuiyufeng
 * @Description: ReqProgressCallBack
 * @date 2018/4/2 10:28
 */
public interface ReqProgressCallBack<T>  extends ReqCallBack<T>{
    /**
     * 响应进度更新
     */
    void onProgress(long total, long current);
}
