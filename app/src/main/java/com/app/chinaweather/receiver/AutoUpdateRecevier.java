package com.app.chinaweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.chinaweather.service.AutoUpdateService;

/**
 * Created by 14501_000 on 2016/5/29.
 */
public class AutoUpdateRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, AutoUpdateService.class);
        context.startActivity(i);
    }
}
