/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid.listeners;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;

/**
 * Descriptions
 *
 * @version 2013-5-20
 * @author PSET
 * @since JDK1.6
 *
 */
public class NotifyListener extends BDNotifyListener {
	Vibrator mVibrator01 = null;
	public void onNotify(BDLocation mlocation, float distance){
		Activity activity = new Activity();
		mVibrator01 = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		mVibrator01.vibrate(1000);//振动提醒已到设定位置附近
	}

}
