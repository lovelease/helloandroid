package com.example.helloandroid.weatherforecast.activity;

import java.io.File;

import com.example.helloandroid.R;
import com.example.helloandroid.weatherforecast.consts.PublicConsts;
import com.example.helloandroid.weatherforecast.utils.WeatherUtility;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


public class WFMainActivity extends Activity {

	//当前Activity的根布局
	private LinearLayout rootLayout;
	//用于存储数据的应用程序私有目录(data/data/包名 /shared_prefs/)
	private static final String PATH = "/data/data/com.example.helloandroid.weatherforecast.activity/shared_prefs/";
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		//设置窗口特征,为不显示标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView( R.layout.activity_wfmain );
		
        // 从android 4.0开始，主程序中不再能访问网络，增加以下设置以解决此问题
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
        	editor.putInt("wellpaper", R.drawable.app_bg02);
        	editor.commit();
        	
        	isFirstRun = true;
        } else {
        	//设置壁纸为文件中保存的
        	SharedPreferences sp= getSharedPreferences(PublicConsts.WALLPAPER_FILE, MODE_PRIVATE);
        	rootLayout.setBackgroundResource(sp.getInt("wellpaper", R.drawable.app_bg02));
        }
        
        //得到保存的城市天气
        SharedPreferences sp = getSharedPreferences(SetCityActivity.CITY_CODE_FILE ,MODE_PRIVATE);
    	String cityCode= sp.getString("code", "");
//    	cityCode = "101070201"; //大连的citycode，用于测试if分支的运行
    	if( cityCode!= null && cityCode.trim().length()!=0) {
    		SharedPreferences shared = getSharedPreferences(PublicConsts.STORE_WEATHER, MODE_PRIVATE);
    		long currentTime = System.currentTimeMillis();
    		WeatherUtility wu = new WeatherUtility();
    		//得到天气缓冲文件中的有效期
    		long vaildTime = shared.getLong("validTime", currentTime);
    		//比较天气缓存文件中的有效期，如果超时了，则访问网络更新天气
    		if(vaildTime > currentTime) {
    			//读取缓存文件中的天气
    			wu.setWeatherSituation(shared);
    		} else {
    			//从网上更新新的天气
    			wu.setWeatherSituation(cityCode);
    		}
    	} else {
    		//跳转到设置城市的Activity
    		Intent intent = new Intent(this, SetCityActivity.class);
    		intent.putExtra("isFirstRun", isFirstRun);
    		startActivityForResult(intent, 0);
    	}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.wfmain, menu );
		return true;
	}

}
