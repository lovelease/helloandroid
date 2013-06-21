/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid.weatherforecast.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.helloandroid.weatherforecast.activity.WFMainActivity;
import com.example.helloandroid.weatherforecast.consts.PublicConsts;
import com.example.helloandroid.weatherforecast.service.LogService;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * Descriptions
 *
 * @version 2013-6-7
 * @author PSET
 * @since JDK1.6
 *
 */
public class Utility {
	
	private static final String TAG = "Utility";
	//时间格式：hh:mm:ss
	public static final String DATE_FORMAT_HH_MM_SS = "HH:mm:ss";

	/**
	 * 
	 */
	public Utility() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 设置更新间隔
	 * @param context 上下文
	 * @param interval 时间间隔
	 */
	public static void setUpdInterval( Context context, int interval) {
		
		SharedPreferences shared = context.getSharedPreferences(PublicConsts.STORE_WEATHER, WFMainActivity.MODE_PRIVATE);
		Editor editor = shared.edit();
		//将时间间隔转化为毫秒级别
		editor.putLong( PublicConsts.WEATHER_FILE_UPD_INTERVAL, interval*60*60*1000 );
		editor.commit();
		
		//日志输出
		Long updInterval = shared.getLong( PublicConsts.WEATHER_FILE_UPD_INTERVAL, 0L );
		String outputTime = (updInterval == 0L ? "设置失败" : String.valueOf( updInterval ));
		Log.i( PublicConsts.APP_TAG, TAG + PublicConsts.MY_APP_LOG_SYMBOL + "===============set update interval to:" + outputTime + "===================" );
	}
	
	/**
	 * 获得【HH:mm:ss】格式的时间
	 * @param longDate long类型的时间
	 * @return 【HH:mm:ss】格式的时间
	 */
	public static String getTime(Long longDate) {
		if (longDate == null) {
			throw new NullPointerException();
		}
		SimpleDateFormat dateformat=new SimpleDateFormat(DATE_FORMAT_HH_MM_SS);
		return dateformat.format( longDate ).toString();
	}
	
	/**
	 * 判断service是否运行
	 * @param context 上下文context对象
	 * @param className 要判断的service的class对象
	 * @return true：运行中；false：未运行
	 */
	public static boolean isServiceRunning(Context context, Class<?> className) {
		boolean isRunning = false;
		String serviceName;
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Service.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
		if (!serviceList.isEmpty()) {
			for (int i=0; i<serviceList.size(); i++) {
				serviceName = serviceList.get(i).service.getClassName();
				if (className.getName().equals(serviceName) == true) {
					isRunning = true;
					break;
				}
			}
		}
		Log.i(TAG,"[" + className.getName() + "] is running : "+isRunning);
		
		return isRunning;
	}

}
