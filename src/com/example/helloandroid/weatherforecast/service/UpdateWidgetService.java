package com.example.helloandroid.weatherforecast.service;

import java.util.Date;

import com.example.helloandroid.R;
import com.example.helloandroid.weatherforecast.activity.SetCityActivity;
import com.example.helloandroid.weatherforecast.consts.PublicConsts;
import com.example.helloandroid.weatherforecast.widget.WeatherWidget;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * 
 * @author i-zqluo
 * 创建一个用于更新天气widget小组件的后台服务
 */
public class UpdateWidgetService extends Service {

	//记录定时管理器
	AlarmManager alarm;
	PendingIntent pintent;
	private static final String TAG = "UpdateWidgetService";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// This is the new method that instead of the old onStart method on the pre-2.0 platform.
	@Override //开始服务，执行更新widget组件的操作
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.i(TAG, "===================update  widget===========================");
		//得到widget的布局对象
		RemoteViews views = WeatherWidget.getWeatherView(this);
		//得到AppWidgetManager widget管理器
		AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(this);
		
		int[] appids=appWidgetManager.getAppWidgetIds(new ComponentName(this, WeatherWidget.class));
		
		//======================================================
		//得到城市码
		SharedPreferences sp=getSharedPreferences(PublicConsts.CITY_CODE_FILE, SetCityActivity.MODE_PRIVATE);
		String cityCode= sp.getString(PublicConsts.SP_CITYCODE, "");
		if(cityCode!=null&&cityCode.trim().length() > 0) {
			Log.i(TAG, "===================update  weather===========================");
			WeatherWidget.updateAppWidget(views, this, appWidgetManager, cityCode);
		}
		//======================================================
		
		
		appWidgetManager.updateAppWidget(appids, views);
		
		//获取当前时间
		Date date = new Date();
		long now =date.getTime();
		long unit=60000;//间隔一分钟
		int s=date.getSeconds();  //得到秒数
		unit=60000-s*1000;        //将时间精确到秒
		
		Notification notification = new Notification(R.drawable.logo,
                "wf update service is running",
                System.currentTimeMillis());
		pintent=PendingIntent.getService(this, 0, intent, 0);
		notification.setLatestEventInfo(this, "WF Update Service",
		        "wf update service is running！", pintent);
		
		//让该service前台运行，避免手机休眠时系统自动杀掉该服务
		//如果 id 为 0 ，那么状态栏的 notification 将不会显示。
		startForeground(0, notification);
		
		//计时器
		alarm=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
		//AlarmManager.RTC_WAKEUP设置服务在系统休眠时同样会运行
		//第二个参数是下一次启动service时间
		alarm.set(AlarmManager.RTC_WAKEUP, now+unit, pintent);
		
		return Service.START_REDELIVER_INTENT;
	}

	@Override 
	public void onDestroy() {
		
		//取消定时管理
		if(alarm!=null) {
			alarm.cancel(pintent);
		}
		
		//对于通过startForeground启动的service，需要通过stopForeground来取消前台运行状态
		stopForeground(true);
//		super.onDestroy();
	}
}
