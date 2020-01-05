package com.xxun.watch.xunfriends.net;

/**
 * @author cuiyufeng
 * @Description: ReqCallBack
 * @date 2018/4/2 10:28
 */
public interface ReqCallBack<T> {
    /**
     * 响应成功
     */
    void onReqSuccess(T result);

    /**
     * 响应失败
     */
    void onReqFailed(String errorMsg);
}
