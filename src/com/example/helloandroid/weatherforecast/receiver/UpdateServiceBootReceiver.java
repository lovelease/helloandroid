/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid.weatherforecast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Descriptions
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
