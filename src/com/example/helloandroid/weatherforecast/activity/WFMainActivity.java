package com.example.helloandroid.weatherforecast.activity;

import java.io.File;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.helloandroid.R;
import com.example.helloandroid.weatherforecast.consts.PublicConsts;
import com.example.helloandroid.weatherforecast.service.LogService;
import com.example.helloandroid.weatherforecast.utils.LogUtil;
import com.example.helloandroid.weatherforecast.utils.Utility;
import com.example.helloandroid.weatherforecast.utils.WebAccessTools;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class WFMainActivity extends Activity {

	private static final String TAG = "WFMainActivity";
	//当前Activity的根布局
	private LinearLayout rootLayout;
	//用于存储数据的应用程序私有目录(data/data/工程路径（而非该类的包路径）/shared_prefs/)
	private static final String PATH = "/data/data/com.example.helloandroid/shared_prefs/";
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		//设置窗口特征,为不显示标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView( R.layout.activity_wfmain );
		
		//启动后台写日志服务，将日志记录到SDCard中，方便实机测试
		Intent startService =  new Intent (this,LogService.class);
		startService( startService );
		
		/**
		 * 从android 4.0开始，主程序中不再能访问网络，增加以下设置可以允许主线程访问网络，但是建议主线程中不要访问网络
		 */
		/*************************************************************************
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        ***************************************************************************/
		
		rootLayout = (LinearLayout)findViewById(R.id.rootLayout);
		
		//通过检查程序中的缓存文件判断程序是否是第一次运行
        File file= new File(PATH);
        boolean isFirstRun = false;
        //如果文件不存在说明是第一次运行
        if (!file.exists()) {
        	//设置默认的壁纸
        	//getSharedPreferences是android提供的移动存储技术之一，是一种轻量级数据存储方式
        	//详细内容可以参考http://express.ruanko.com/ruanko-express_29/technologyexchange6.html
        	SharedPreferences.Editor editor = getSharedPreferences(PublicConsts.WALLPAPER_FILE, MODE_PRIVATE).edit();
        	editor.putInt(PublicConsts.SP_WALLPAPER, R.drawable.app_bg04);
        	editor.commit();
        	
        	isFirstRun = true;
        } else {
        	//设置壁纸为文件中保存的
        	SharedPreferences sp= getSharedPreferences(PublicConsts.WALLPAPER_FILE, MODE_PRIVATE);
        	rootLayout.setBackgroundResource(sp.getInt(PublicConsts.SP_WALLPAPER, R.drawable.app_bg04));
        }
        
        //得到保存的城市天气
        SharedPreferences sp = getSharedPreferences(PublicConsts.CITY_CODE_FILE ,MODE_PRIVATE);
    	String cityCode= sp.getString(PublicConsts.SP_CITYCODE, "");
//    	cityCode = "101070201"; //大连的citycode，用于测试if分支的运行
    	if( cityCode!= null && cityCode.trim().length()!=0) {
    		SharedPreferences shared = getSharedPreferences(PublicConsts.STORE_WEATHER, MODE_PRIVATE);
    		long currentTime = System.currentTimeMillis();
    		//得到天气缓冲文件中的最近更新时间
    		long lastUpdated = shared.getLong(PublicConsts.WEATHER_FILE_LAST_UPDATE, currentTime);
    		//得到天气缓冲文件中的更新间隔
    		long updInterval = shared.getLong(PublicConsts.WEATHER_FILE_UPD_INTERVAL, PublicConsts.DEFAULT_UPD_INTERVAL);
    		//计算天气缓存文件的有效期
    		long vaildTime = lastUpdated + updInterval;
    		LogUtil.i( TAG, "WFMainActivity/onCreate update : vaildTime=" + Utility.getTime( vaildTime ) );
    		LogUtil.i( TAG, "WFMainActivity/onCreate update : currentTime=" + Utility.getTime( currentTime ) );
    		//比较天气缓存文件的有效期，如果超时了，则访问网络更新天气
    		if(vaildTime > currentTime) {
    			LogUtil.i( TAG, "WFMainActivity/onCreate update : vaildTime > currentTime,从缓存文件更新天气" );
    			//读取缓存文件中的天气
    			setWeatherSituation(shared);
    		} else {
    			LogUtil.i( TAG, "WFMainActivity/onCreate update : vaildTime <= currentTime,从网络更新天气" );
    			//从网上更新新的天气
    			setWeatherSituation(cityCode);
    		}
    	} else {
    		//跳转到设置城市的Activity
    		Intent intent = new Intent(this, SetCityActivity.class);
    		intent.putExtra("isFirstRun", isFirstRun);
    		startActivityForResult(intent, PublicConsts.ORIGIN_SETCITY);
    	}
	}
	
	@Override //得到设置页面的回退
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	//得到城市的编码
    	SharedPreferences sp = getSharedPreferences(PublicConsts.CITY_CODE_FILE, MODE_PRIVATE);
		String cityCode = sp.getString(PublicConsts.SP_CITYCODE, "");
    	if(cityCode!=null&&cityCode.trim().length()!=0) {
    		if(data!=null&&data.getBooleanExtra("updateWeather", false)) {
    			LogUtil.i( TAG, "WFMainActivity/onActivityResult update : 从网络更新天气" );
    			//从网上更新新的天气
    			setWeatherSituation(cityCode);
    		} else {
    			LogUtil.i( TAG, "WFMainActivity/onActivityResult update : 从缓存文件更新天气" );
    			//读取缓存文件中的天气
    			SharedPreferences shared = getSharedPreferences(PublicConsts.STORE_WEATHER, MODE_PRIVATE);
    			setWeatherSituation(shared);
    		}
    	} else {
    		//如果是没有城市码的回退，则退出程序
    		this.finish();
    	}
    }

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.wfmain, menu );
		//得到保存的壁纸
    	SharedPreferences sp= getSharedPreferences(PublicConsts.WALLPAPER_FILE, MODE_PRIVATE);
    	SubMenu subMenu = menu.getItem(2).getSubMenu();
    	MenuItem item = null;
    	switch(sp.getInt(PublicConsts.SP_WALLPAPER, R.drawable.app_bg01)) {
    	case R.drawable.app_bg01:
    		item = subMenu.getItem(0);
    		item.setChecked(true);
    		break;
    	case R.drawable.app_bg02:
    		item = subMenu.getItem(1);
    		item.setChecked(true);
    		break;
    	case R.drawable.app_bg03:
    		item = subMenu.getItem(2);
    		item.setChecked(true);
    		break;
    	case R.drawable.app_bg04:
    		item = subMenu.getItem(3);
    		item.setChecked(true);
    		break;
    	}
		return true;
	}
	
	//单击菜单方法
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	//得到SharedPreferences操作对象更改壁纸
    	SharedPreferences.Editor editor = getSharedPreferences(PublicConsts.WALLPAPER_FILE, MODE_PRIVATE).edit();
    	//得到储存城市编码的文件
    	SharedPreferences sp = getSharedPreferences(PublicConsts.CITY_CODE_FILE, MODE_PRIVATE);
    	//判断单击菜单的ID
    	switch(menuItem.getItemId()) {
    	
    	/**选择城市*/
    	case R.id.menu_changeCity:
    		//跳转到设置城市的Activity
    		Intent intent = new Intent(this, SetCityActivity.class);
    		startActivityForResult(intent, PublicConsts.ORIGIN_SETCITY);
    		break;
    	
    	/**更新天气*/
    	case R.id.menu_update:
    		//得到设置的城市码
            String cityCode = sp.getString(PublicConsts.SP_CITYCODE, "");
			
            //从网上更新天气
            if( cityCode!= null && cityCode.trim().length()!=0) {
            	setWeatherSituation(cityCode);
            }
            
			Toast.makeText( this, "天气更新成功", Toast.LENGTH_SHORT ).show();
    		break;
    	case R.id.menu_setting:
    		//跳转到app设置Activity
    		intent = new Intent(this, AppSettingActivity.class);
    		startActivity(intent);
    		break;
    		
    	/**更换壁纸*/
    	case R.id.wallpaper01:
    		rootLayout.setBackgroundResource(R.drawable.app_bg01);
        	editor.putInt(PublicConsts.SP_WALLPAPER, R.drawable.app_bg01);
        	editor.commit();
        	menuItem.setChecked(true);
    		break;
    	case R.id.wallpaper02:
    		rootLayout.setBackgroundResource(R.drawable.app_bg02);
        	editor.putInt(PublicConsts.SP_WALLPAPER, R.drawable.app_bg02);
        	editor.commit();
        	menuItem.setChecked(true);
    		break;
    	case R.id.wallpaper03:
    		rootLayout.setBackgroundResource(R.drawable.app_bg03);
        	editor.putInt(PublicConsts.SP_WALLPAPER, R.drawable.app_bg03);
        	editor.commit();
        	menuItem.setChecked(true);
    		break;
    	case R.id.wallpaper04:
    		rootLayout.setBackgroundResource(R.drawable.app_bg04);
        	editor.putInt(PublicConsts.SP_WALLPAPER, R.drawable.app_bg04);
        	editor.commit();
        	menuItem.setChecked(true);
    		break;
		default:
			break;
    	}
    	return true;
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
		//最近更新时间
		long updTime = shared.getLong( PublicConsts.WEATHER_FILE_LAST_UPDATE, System.currentTimeMillis() );
		String time = Utility.getTime( updTime );
		tempText=(TextView)findViewById(R.id.lastUpd);
		tempText.setText(String.valueOf( time ));
    }
    
    //由城市码从网上更新天气情况,并将得到的信息保存在文件中
    public void setWeatherSituation(String cityCode) {
    	//执行异步线程，访问网络，因为从android 4.0开始，主程序中不再能访问网络
    	UpdWeatherNetwork uwn = new UpdWeatherNetwork();
    	uwn.execute( cityCode );
    }
    
    //由天气情况得到图片
    public static int getWeatherBitMapResource(String weather) {
    	Log.i(TAG, PublicConsts.MY_APP_LOG_SYMBOL + "============="+weather+"===============");
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
    	} else if(weather.equals("大雨")||weather.equals("暴雨")) {
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
    
    //得到一个进度对话框
    public ProgressDialog getProgressDialog(String title, String content) {
    	//实例化进度条对话框ProgressDialog
    	ProgressDialog dialog=new ProgressDialog(this);
    	
    	//可以不显示标题
    	dialog.setTitle(title);
    	dialog.setIndeterminate(true);
    	dialog.setMessage(content);
    	dialog.setCancelable(true);
    	return dialog;
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
    public class UpdWeatherNetwork extends AsyncTask<String, Integer, String> {

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
			String cityCode = params[0];
			String url = "http://m.weather.com.cn/data/"+cityCode+".html";
	    	String info = new WebAccessTools(WFMainActivity.this).getWebContent(url);
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
				JSONObject json=new JSONObject(result).getJSONObject("weatherinfo");
				TextView tempText = null;
				ImageView imageView=null;
				int weather_icon = 0;
				
				//建立一个缓存天气的文件
				SharedPreferences.Editor editor = getSharedPreferences(PublicConsts.STORE_WEATHER, MODE_PRIVATE).edit();
				
				//得到城市
				result=json.getString("city");
				tempText=(TextView)findViewById(R.id.cityField);
				tempText.setText(result);
				editor.putString("city", result);
				
				//得到阳历日期
				result= json.getString("date_y") ;
				result= result+"("+json.getString("week")+")";
				tempText=(TextView)findViewById(R.id.date_y);
				tempText.setText(result);
				editor.putString("date_y", result);
				//得到农历
				result= json.getString("date");
				tempText=(TextView)findViewById(R.id.date);
				tempText.setText(result);
				editor.putString("date", result);
				//得到温度
				result= json.getString("temp1");
				tempText=(TextView)findViewById(R.id.currentTemp);
				tempText.setText(result);
				editor.putString("temp1", result);
				//得到天气
				result= json.getString("weather1");
				tempText=(TextView)findViewById(R.id.currentWeather);
				tempText.setText(result);
				editor.putString("weather1", result);
				//天气图标
				result= json.getString("img_title1");
				imageView=(ImageView)findViewById(R.id.weather_icon01);
				weather_icon = getWeatherBitMapResource(result);
				imageView.setImageResource(weather_icon);
				editor.putInt("img_title1", weather_icon);
				//得到风向
				result= json.getString("wind1");
				tempText=(TextView)findViewById(R.id.currentWind);
				tempText.setText(result);
				editor.putString("wind1", result);
				//得到建议
				result= json.getString("index_d");
				tempText=(TextView)findViewById(R.id.index_d);
				tempText.setText(result);
				editor.putString("index_d", result);
				
				//得到明天的天气
				result= json.getString("weather2");
				tempText=(TextView)findViewById(R.id.weather02);
				tempText.setText(result);
				editor.putString("weather2", result);
				//明天的图标
				result= json.getString("img_title2");
				imageView=(ImageView)findViewById(R.id.weather_icon02);
				weather_icon = getWeatherBitMapResource(result);
				imageView.setImageResource(weather_icon);
				editor.putInt("img_title2", weather_icon);
				//明天的气温
				result= json.getString("temp2");
				tempText=(TextView)findViewById(R.id.temp02);
				tempText.setText(result);
				editor.putString("temp2", result);
				//明天的风力
				result= json.getString("wind2");
				tempText=(TextView)findViewById(R.id.wind02);
				tempText.setText(result);
				editor.putString("wind2", result);
				
				//后天的天气
				result= json.getString("weather3");
				tempText=(TextView)findViewById(R.id.weather03);
				tempText.setText(result);
				editor.putString("weather3", result);
				//后天天气图标
				result= json.getString("img_title3");
				imageView=(ImageView)findViewById(R.id.weather_icon03);
				weather_icon = getWeatherBitMapResource(result);
				imageView.setImageResource(weather_icon);
				editor.putInt("img_title3", weather_icon);
				//后天的气温
				result= json.getString("temp3");
				tempText=(TextView)findViewById(R.id.temp03);
				tempText.setText(result);
				editor.putString("temp3", result);
				//后天的风力
				result= json.getString("wind3");
				tempText=(TextView)findViewById(R.id.wind03);
				tempText.setText(result);
				editor.putString("wind3", result);
				
				//最近更新时间
				long updTime = System.currentTimeMillis();
				String updTimeStr = Utility.getTime( updTime );
				tempText=(TextView)findViewById(R.id.lastUpd);
				tempText.setText(String.valueOf( updTimeStr ));
				editor.putLong(PublicConsts.WEATHER_FILE_LAST_UPDATE, updTime);
				
				//保存
				editor.commit();
			} catch (JSONException e) {
				LogUtil.e( TAG, e.getStackTrace().toString() );
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
