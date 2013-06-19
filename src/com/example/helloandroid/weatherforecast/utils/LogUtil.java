/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid.weatherforecast.utils;

import com.example.helloandroid.weatherforecast.consts.PublicConsts;

import android.util.Log;

/**
 * Descriptions
 *
 * @version 2013-6-19
 * @author PSET
 * @since JDK1.6
 *
 */
public class LogUtil {

	public static void i(String tag, String msg) {
		Log.i( PublicConsts.APP_TAG, "(" + tag + ")" + PublicConsts.MY_APP_LOG_SYMBOL + msg );
	}
	
	public static void e(String tag, String msg) {
		Log.e( PublicConsts.APP_TAG, "(" + tag + ")" + PublicConsts.MY_APP_LOG_SYMBOL + msg );
	}
	
	public static void d(String tag, String msg) {
		Log.d( PublicConsts.APP_TAG, "(" + tag + ")" + PublicConsts.MY_APP_LOG_SYMBOL + msg );
	}
	
	public static void v(String tag, String msg) {
		Log.v( PublicConsts.APP_TAG, "(" + tag + ")" + PublicConsts.MY_APP_LOG_SYMBOL + msg );
	}
	
	public static void w(String tag, String msg) {
		Log.w( PublicConsts.APP_TAG, "(" + tag + ")" + PublicConsts.MY_APP_LOG_SYMBOL + msg );
	}
}
