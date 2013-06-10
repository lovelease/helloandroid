/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid.weatherforecast.consts;

/**
 * Descriptions
 *
 * @version 2013-6-3
 * @author PSET
 * @since JDK1.6
 *
 */
public class PublicConsts {
	
	/** =======================General consts====================================== */
	//space
	public static final String STR_SPACE = " ";

	//记录壁纸的文件
	public static final String WALLPAPER_FILE="wallpaper_file";
	//缓存天气的文件
	public static final String STORE_WEATHER="store_weather";
	//记录城市码的文件
	public static final String CITY_CODE_FILE="city_code";
	
	//标志SetCityActivity intent的original actibity
	public static final int ORIGIN_SETCITY = 0;
	
	//SharedPreferences中自定义的WALLPAPER_FILE文件内的数据标识名
	public static final String SP_WALLPAPER = "wallpaper";
	//SharedPreferences中自定义的CITY_CODE_FILE文件内的数据标识名
	public static final String SP_CITYCODE = "code";
	//默认更新间隔:3h
	public static Long DEFAULT_UPD_INTERVAL = 3*60*60*1000L;
	
	/** =========================store_weather文件项目============================== */
	//store_weather文件中的记录更新间隔的项目
	public static final String WEATHER_FILE_UPD_INTERVAL = "updInterval";
	//store_weather文件中的记录最近更新时间的项目
	public static final String WEATHER_FILE_LAST_UPDATE = "lastUpdated";
	
}
