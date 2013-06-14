/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid.weatherforecast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Descriptions 开机启动UpdateWidgetService服务的接收器
 * Android系统启动后会发送一个android.intent.action.BOOT_COMPLETED广播
 * 通过在Manifest.xml里面配置该接收器，来指定开机启动的服务
 *
 * @version 2013-6-13
 * @author PSET
 * @since JDK1.6
 *
 */
public class UpdateServiceBootReceiver extends BroadcastReceiver {

	@Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent();
        myIntent.setAction("com.example.helloandroid.weatherforecast.service.UpdateWidgetService");
        context.startService(myIntent);
    }
}
