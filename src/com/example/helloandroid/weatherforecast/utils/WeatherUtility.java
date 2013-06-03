/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid.weatherforecast.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.helloandroid.R;
import com.example.helloandroid.weatherforecast.consts.PublicConsts;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Descriptions
 *
 * @version 2013-6-3
 * @author PSET
 * @since JDK1.6
 *
 */
public class WeatherUtility extends Activity{

	/**
	 * 
	 */
	public WeatherUtility() {
		super();
		// TODO Auto-generated constructor stub
	}

	//根据已定的缓存文件来得到天气情况
    public void setWeatherSituation(SharedPreferences shared) {
    	String info = null;
    	TextView tempText = null;
		ImageView imageView=null;
    	
    	//得到城市
		info = shared.getString("city", "");
		tempText=(TextView)findViewById(R.id.cityField);
		tempText.setText(info);
		
		//得到阳历日期
		info= shared.getString("date_y", "");
		tempText=(TextView)findViewById(R.id.date_y);
		tempText.setText(info);
		//得到农历
		info= shared.getString("date", "");
		tempText=(TextView)findViewById(R.id.date);
		tempText.setText(info);
		//得到温度
		info= shared.getString("temp1", "");
		tempText=(TextView)findViewById(R.id.currentTemp);
		tempText.setText(info);
		//得到天气
		info= shared.getString("weather1", "");
		tempText=(TextView)findViewById(R.id.currentWeather);
		tempText.setText(info);
		//天气图标
		imageView=(ImageView)findViewById(R.id.weather_icon01);
		imageView.setImageResource(shared.getInt("img_title1", 0));
		//得到风向
		info= shared.getString("wind1", "");
		tempText=(TextView)findViewById(R.id.currentWind);
		tempText.setText(info);
		//得到建议
		info= shared.getString("index_d", "");
		tempText=(TextView)findViewById(R.id.index_d);
		tempText.setText(info);
		
		//得到明天的天气
		info= shared.getString("weather2", "");
		tempText=(TextView)findViewById(R.id.weather02);
		tempText.setText(info);
		//明天的图标
		imageView=(ImageView)findViewById(R.id.weather_icon02);
		imageView.setImageResource(shared.getInt("img_title2", 0));
		//明天的气温
		info= shared.getString("temp2", "");
		tempText=(TextView)findViewById(R.id.temp02);
		tempText.setText(info);
		//明天的风力
		info= shared.getString("wind2", "");
		tempText=(TextView)findViewById(R.id.wind02);
		
		//后天的天气
		info= shared.getString("weather3", "");
		tempText=(TextView)findViewById(R.id.weather03);
		tempText.setText(info);
		//后天天气图标
		imageView=(ImageView)findViewById(R.id.weather_icon03);
		imageView.setImageResource(shared.getInt("img_title3", 0));
		//后天的气温
		info= shared.getString("temp3", "");
		tempText=(TextView)findViewById(R.id.temp03);
		tempText.setText(info);
		//后天的风力
		info= shared.getString("wind3", "");
		tempText=(TextView)findViewById(R.id.wind03);
		tempText.setText(info);
    }
    
    //由城市码设置天气情况,并将得到的信息保存在文件中
    public void setWeatherSituation(String cityCode) {
      String url = "http://m.weather.com.cn/data/"+cityCode+".html";
      String info = new WebAccessTools(this).getWebContent(url);
      if (info == null || 0 == info.length()) {
    	  Log.e( "neterr", "从网络获取天气情报失败" );
    	  return ;
      }
      try {
    	    //==========================解析JSON得到天气===========================
			JSONObject json=new JSONObject(info).getJSONObject("weatherinfo");
			TextView tempText = null;
			ImageView imageView=null;
			int weather_icon = 0;
			
			//建立一个缓存天气的文件
			SharedPreferences.Editor editor = getSharedPreferences(PublicConsts.STORE_WEATHER, MODE_PRIVATE).edit();
			
			//得到城市
			info=json.getString("city");
			tempText=(TextView)findViewById(R.id.cityField);
			tempText.setText(info);
			editor.putString("city", info);
			
			//得到阳历日期
			info= json.getString("date_y") ;
			info= info+"("+json.getString("week")+")";
			tempText=(TextView)findViewById(R.id.date_y);
			tempText.setText(info);
			editor.putString("date_y", info);
			//得到农历
			info= json.getString("date");
			tempText=(TextView)findViewById(R.id.date);
			tempText.setText(info);
			editor.putString("date", info);
			//得到温度
			info= json.getString("temp1");
			tempText=(TextView)findViewById(R.id.currentTemp);
			tempText.setText(info);
			editor.putString("temp1", info);
			//得到天气
			info= json.getString("weather1");
			tempText=(TextView)findViewById(R.id.currentWeather);
			tempText.setText(info);
			editor.putString("weather1", info);
			//天气图标
			info= json.getString("img_title1");
			imageView=(ImageView)findViewById(R.id.weather_icon01);
			weather_icon = getWeatherBitMapResource(info);
			imageView.setImageResource(weather_icon);
			editor.putInt("img_title1", weather_icon);
			//得到风向
			info= json.getString("wind1");
			tempText=(TextView)findViewById(R.id.currentWind);
			tempText.setText(info);
			editor.putString("wind1", info);
			//得到建议
			info= json.getString("index_d");
			tempText=(TextView)findViewById(R.id.index_d);
			tempText.setText(info);
			editor.putString("index_d", info);
			
			//得到明天的天气
			info= json.getString("weather2");
			tempText=(TextView)findViewById(R.id.weather02);
			tempText.setText(info);
			editor.putString("weather2", info);
			//明天的图标
			info= json.getString("img_title2");
			imageView=(ImageView)findViewById(R.id.weather_icon02);
			weather_icon = getWeatherBitMapResource(info);
			imageView.setImageResource(weather_icon);
			editor.putInt("img_title2", weather_icon);
			//明天的气温
			info= json.getString("temp2");
			tempText=(TextView)findViewById(R.id.temp02);
			tempText.setText(info);
			editor.putString("temp2", info);
			//明天的风力
			info= json.getString("wind2");
			tempText=(TextView)findViewById(R.id.wind02);
			tempText.setText(info);
			editor.putString("wind2", info);
			
			//后天的天气
			info= json.getString("weather3");
			tempText=(TextView)findViewById(R.id.weather03);
			tempText.setText(info);
			editor.putString("weather3", info);
			//后天天气图标
			info= json.getString("img_title3");
			imageView=(ImageView)findViewById(R.id.weather_icon03);
			weather_icon = getWeatherBitMapResource(info);
			imageView.setImageResource(weather_icon);
			editor.putInt("img_title3", weather_icon);
			//后天的气温
			info= json.getString("temp3");
			tempText=(TextView)findViewById(R.id.temp03);
			tempText.setText(info);
			editor.putString("temp3", info);
			//后天的风力
			info= json.getString("wind3");
			tempText=(TextView)findViewById(R.id.wind03);
			tempText.setText(info);
			editor.putString("wind3", info);
			
			//设置一个有效日期为5小时
			long validTime = System.currentTimeMillis();
			validTime = validTime + 5*60*60*1000;
			editor.putLong("validTime", validTime);
			
			//保存
			editor.commit();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
  //由天气情况得到图片
    public static int getWeatherBitMapResource(String weather) {
    	Log.i("weather_info", "============="+weather+"===============");
    	if(weather.equals("晴")) {
    		return R.drawable.weathericon_condition_01;
    	} else if(weather.equals("多云")) {
    		return R.drawable.weathericon_condition_02;
    	} else if(weather.equals("阴")) {
    		return R.drawable.weathericon_condition_04;
    	} else if(weather.equals("雾")) {
    		return R.drawable.weathericon_condition_05;
    	} else if(weather.equals("沙尘暴")) {
    		return R.drawable.weathericon_condition_06;
    	} else if(weather.equals("阵雨")) {
    		return R.drawable.weathericon_condition_07;
    	} else if(weather.equals("小雨")||weather.equals("小到中雨")) {
    		return R.drawable.weathericon_condition_08;
    	} else if(weather.equals("大雨")) {
    		return R.drawable.weathericon_condition_09;
    	} else if(weather.equals("雷阵雨")) {
    		return R.drawable.weathericon_condition_10;
    	} else if(weather.equals("小雪")) {
    		return R.drawable.weathericon_condition_11;
    	} else if(weather.equals("大雪")) {
    		return R.drawable.weathericon_condition_12;
    	} else if(weather.equals("雨夹雪")) {
    		return R.drawable.weathericon_condition_13;
    	} else {
    		return R.drawable.weathericon_condition_17;
    	}
    }
}
