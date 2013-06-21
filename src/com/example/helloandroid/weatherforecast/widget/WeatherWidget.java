package com.example.helloandroid.weatherforecast.widget;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloandroid.R;
import com.example.helloandroid.weatherforecast.activity.WFMainActivity;
import com.example.helloandroid.weatherforecast.consts.PublicConsts;
import com.example.helloandroid.weatherforecast.service.UpdateWidgetService;
import com.example.helloandroid.weatherforecast.utils.LogUtil;
import com.example.helloandroid.weatherforecast.utils.Utility;
import com.example.helloandroid.weatherforecast.utils.WebAccessTools;

public class WeatherWidget extends AppWidgetProvider {
	
	private static final String TAG = "WeatherWidget";
	private static RemoteViews views;
	private static Context context;
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		//这样在第一次运行时也能响应用户的单击事件
		getWeatherView(context);
		
		//启动一个自定义更新widget的后台服务
		context.startService(new Intent(context,UpdateWidgetService.class));
	}
	
	@Override //当删除最后一个Widget组件后调用
	public void onDisabled(Context context) {
		super.onDisabled(context);
		//关闭后台服务
		context.stopService(new Intent(context,UpdateWidgetService.class));
	}

	//返回widget中的布局视图对象
	public static RemoteViews getWeatherView(Context context){
		RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.wf_widget_layout);
		
		//单击widget的主体来启动WFMainActivity返回到天气精灵的天气显示界面
		Intent intent = new Intent(context, WFMainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.weather_rootLayout, pendingIntent);
		
		return views;
	}
	
	public static void updateAppWidget(RemoteViews views, Context context, 
			AppWidgetManager appWidgetManager, String cityCode) {
		
		WeatherWidget.views = views;
		WeatherWidget.context = context;
		
		SharedPreferences shared = context.getSharedPreferences(PublicConsts.STORE_WEATHER, WFMainActivity.MODE_PRIVATE);
		long currentTime = System.currentTimeMillis();
		//得到天气缓冲文件中的最近更新时间
		long lastUpdated = shared.getLong(PublicConsts.WEATHER_FILE_LAST_UPDATE, currentTime);
		//得到天气缓冲文件中的更新间隔
		long updInterval = shared.getLong(PublicConsts.WEATHER_FILE_UPD_INTERVAL, PublicConsts.DEFAULT_UPD_INTERVAL);
		//计算天气缓存文件的有效期
		long vaildTime = lastUpdated + updInterval;
		
		LogUtil.i( TAG, "widget update : vaildTime=" + Utility.getTime( vaildTime ) );
		LogUtil.i( TAG, "widget update : currentTime=" + Utility.getTime( currentTime ) );
		//比较天气缓存文件中的有效期，如果超时了，则访问网络更新天气
		if(vaildTime <= currentTime) {
			LogUtil.i( TAG, "widget update : vaildTime <= currentTime,从网络更新天气" );
			updateWeather(views, context, cityCode);
		}
		else {
			LogUtil.i( TAG, "widget update : vaildTime > currentTime,从缓存文件更新天气" );
			updateWeather(views, context);
		}
		//更新时间
		Date date = new Date();
		SimpleDateFormat foramt = new SimpleDateFormat("HH:mm");
		String timeText = foramt.format(date);
		Log.i( PublicConsts.APP_TAG, TAG + PublicConsts.MY_APP_LOG_SYMBOL + "===================update  time======"+timeText+"=====================");
		views.setTextViewText(R.id.widget_time , timeText);
	}

	//由缓存文件来得到天气信息
	public static void updateWeather(RemoteViews views, Context context) {
		SharedPreferences sp = context.getSharedPreferences(PublicConsts.STORE_WEATHER,WFMainActivity.MODE_PRIVATE);
		
		String info=sp.getString("city", "");
		views.setTextViewText(R.id.widget_city, info);
		
		info=sp.getString("date_y", "");
		views.setTextViewText(R.id.widget_data01, info);
		
		info= sp.getString("temp1", "");
		views.setTextViewText(R.id.widget_temp, info);
		
		info= sp.getString("weather1", "");
		views.setTextViewText(R.id.widget_weather, info);
		
		//最近更新时间
		long updTime = sp.getLong( PublicConsts.WEATHER_FILE_LAST_UPDATE, System.currentTimeMillis() );
		String time = Utility.getTime( updTime );
		views.setTextViewText(R.id.widget_lastUpd, time);
		
		views.setImageViewResource(R.id.widget_icon, sp.getInt("img_title1", R.drawable.weathericon_condition_17));
	}
	
	//从网络中更新天气文件和views中的显示数据
	public static void updateWeather(RemoteViews views, Context context, String cityCode) {
		//由城市码更新天气
		StringBuffer str = new StringBuffer("http://m.weather.com.cn/data/");
		str.append(cityCode);
		str.append(".html");
		//实例化异步线程类，进行网络访问
		UpdWeatherNetwork uwn = new WeatherWidget().new UpdWeatherNetwork();
		uwn.execute( str.toString() );
	}
	
	/**
     * 将通过网络获取天气的部分写成异步线程，因为主线程内不应该有网络访问，否则可能造成主线程响应时间过长,
     * 甚至出现background ANR（application not responsed),导致MainActivity一直卡在生成画面的地方，人机无法继续交互，用户体验差
     *
     * @version 2013-6-19
     * @author PSET
     * @since JDK1.6
     *
     */
	private class UpdWeatherNetwork extends AsyncTask<String, Integer, String> {

		/** 
		 * onPreExecute方法用于在执行后台任务前做一些UI操作 
		 */
        @Override  
        protected void onPreExecute() {  
             
        }
        
		/** 
		 * doInBackground方法用于执行后台任务,不可在此方法内修改UI
		 * @param params citycode
		 * @return String 网络数据
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground( String... params ) {
			LogUtil.i( TAG, "doInBackground method called" );
			String url = (String)params[0];
	    	String info = new WebAccessTools(context).getWebContent(url);
			return info;
		}
		
		/** 
		 * onProgressUpdate方法用于更新进度信息  
		 */
        @Override  
        protected void onProgressUpdate(Integer... progresses) {
        	
        }
		
		/** 
		 * onPostExecute方法用于在doInBackground执行完后台任务后更新UI,显示结果
		 * @param result citycode
		 * @return String 网络数据
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override  
        protected void onPostExecute(String result) {
			LogUtil.i( TAG, "onPostExecute method called" );
			//==========================解析JSON得到天气===========================
			try {
				if (result == null || PublicConsts.STR_BLANK.equals( result )) {
					LogUtil.i( TAG, "天气更新失败，网络错误（WeatherWidget/onPostExecute：result=null)" );
				} else {
					LogUtil.i( TAG, "解析JSON得到天气 START" );
					JSONObject json=new JSONObject(result).getJSONObject(PublicConsts.WEATHER_DATA_NAME);
					int weather_icon = 0;
					
					//建立一个缓存天气的文件
					SharedPreferences.Editor editor = context.getSharedPreferences(PublicConsts.STORE_WEATHER,
							WFMainActivity.MODE_PRIVATE).edit();
					
					//得到城市
					result=json.getString("city");
					editor.putString("city", result);
					
					views.setTextViewText(R.id.widget_city, result);
					
					//得到阳历日期
					result= json.getString("date_y") ;
					result= result+"("+json.getString("week")+")";
					editor.putString("date_y", result);
					
					views.setTextViewText(R.id.widget_data01, result);
					
					//得到农历
					result= json.getString("date");
					editor.putString("date", result);
					//得到温度
					result= json.getString("temp1");
					editor.putString("temp1", result);
					
					views.setTextViewText(R.id.widget_temp, result);
					//得到天气
					result= json.getString("weather1");
					editor.putString("weather1", result);
					
					views.setTextViewText(R.id.widget_weather, result);
					//天气图标
					result= json.getString("img_title1");
					weather_icon = WFMainActivity.getWeatherBitMapResource(result);
					editor.putInt("img_title1", weather_icon);
					
					views.setImageViewResource(R.id.widget_icon, weather_icon);
					//得到风向
					result= json.getString("wind1");
					editor.putString("wind1", result);
					//得到建议
					result= json.getString("index_d");
					editor.putString("index_d", result);
					
					//得到明天的天气
					result= json.getString("weather2");
					editor.putString("weather2", result);
					//明天的图标
					result= json.getString("img_title2");
					weather_icon = WFMainActivity.getWeatherBitMapResource(result);
					editor.putInt("img_title2", weather_icon);
					//明天的气温
					result= json.getString("temp2");
					editor.putString("temp2", result);
					//明天的风力
					result= json.getString("wind2");
					editor.putString("wind2", result);
					
					//后天的天气
					result= json.getString("weather3");
					editor.putString("weather3", result);
					//后天天气图标
					result= json.getString("img_title3");
					weather_icon = WFMainActivity.getWeatherBitMapResource(result);
					editor.putInt("img_title3", weather_icon);
					//后天的气温
					result= json.getString("temp3");
					editor.putString("temp3", result);
					//后天的风力
					result= json.getString("wind3");
					editor.putString("wind3", result);
					
					LogUtil.i( TAG, "解析JSON得到天气 END SUCCESSFULLY" );
					
					//更新时间
					long updTime = System.currentTimeMillis();
					editor.putLong(PublicConsts.WEATHER_FILE_LAST_UPDATE, updTime);
					
					String time = Utility.getTime( updTime );
					views.setTextViewText(R.id.widget_lastUpd, time);
					
					//保存
					editor.commit();
				}
				
			} catch (JSONException e) {
				LogUtil.i( TAG, "解析JSON得到天气 THROW EXCEPTION" );
				LogUtil.e( TAG, "EXCEPTION MSG : " + e.getMessage() );
			}
			
		}
		
		/** 
		 * onCancelled方法用于在取消执行中的任务时更改UI
		 */
        @Override  
        protected void onCancelled() {  
            
        }
		
	}
}
