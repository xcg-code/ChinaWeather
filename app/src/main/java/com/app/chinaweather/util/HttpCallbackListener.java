package com.app.chinaweather.util;

/**
 * Created by 14501_000 on 2016/5/25.
 * 使用HttpCallbackListener接口来回调服务返回的结果
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
