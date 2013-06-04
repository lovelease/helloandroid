package com.example.helloandroid.weatherforecast.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.example.helloandroid.R;
import com.example.helloandroid.weatherforecast.adapter.MyListAdapter;
import com.example.helloandroid.weatherforecast.consts.PublicConsts;
import com.example.helloandroid.weatherforecast.dao.DBHelper;
import com.example.helloandroid.weatherforecast.widget.WeatherWidget;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

public class SetCityActivity extends Activity {

	//定义的一个记录城市码的SharedPreferences文件名
	public static final String CITY_CODE_FILE="city_code";
	//定义的一个自动定位的列表
	private ListView gpsView;
	//定义的一个省份可伸缩性的列表
	private ExpandableListView provinceList;
	//定义的用于过滤的文本输入框
	private TextView filterText;
	//自定义的伸缩列表适配器
    private MyListAdapter adapter;
    //记录应用程序widget的ID
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    //省份
	private String[] groups;
	//对应的城市
    private String[][] childs;
    //城市的编码
	private String[][] cityCodes;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_set_city );
		
		gpsView = (ListView)findViewById(R.id.gps_view);
		provinceList= (ExpandableListView)findViewById(R.id.provinceList);
		
		//设置自动定位的适配器
//        gpsView.setAdapter(new GPSListAdapter(SetCityActivity.this));
		
		//==============================GPS=================================
        //当单击自动定位时
        gpsView.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> arg0, View arg1, int arg2, long arg3 ) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        //为过滤输入文本框添加事件
        filterText = (TextView) findViewById(R.id.filterField);
        filterText.addTextChangedListener( new TextWatcher() {

			@Override
			public void afterTextChanged( Editable s ) {
				CharSequence filterContent = filterText.getText();
				//设置列表数据过滤结果显示
				adapter.getFilter().filter(filterContent);
				
			}

			@Override
			public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged( CharSequence s, int start, int before, int count ) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        //得到MainActivity或Widget传过来的intent
        Intent intent =getIntent();
        //通过判断MainActivity传过来的isFirstRun来确定是否为第一次运行
        boolean isFirstRun = intent.getBooleanExtra("isFirstRun", false);
        //通过接收Bundle来判断Widget中传递过来的WidgetId
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 
            		AppWidgetManager.INVALID_APPWIDGET_ID);
            //如果WidgetId有效
            if(mAppWidgetId!=AppWidgetManager.INVALID_APPWIDGET_ID) {
            	//判断它是否是第一次运行
            	SharedPreferences sp=getSharedPreferences(CITY_CODE_FILE, MODE_PRIVATE);
            	if(sp.getString("code", null)==null) {
            		//如果不存在城市码，则说明为第一次运行
            		isFirstRun = true;
            	} else {   		
            		//如存在则直接跳回
            		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(SetCityActivity.this);
    				RemoteViews views = new RemoteViews(SetCityActivity.this.getPackageName(),
    						R.layout.wf_widget_layout);
    				//得到城市码
    				String cityCode= sp.getString("code", "");
    				if(cityCode!=null&&cityCode.trim().length() > 0) {
    					Log.i("widget", "===================update  weather===========================");
    					//更新widget
    					WeatherWidget.updateAppWidget(views, SetCityActivity.this, appWidgetManager, cityCode);
    				}
    				
    				appWidgetManager.updateAppWidget(mAppWidgetId, views);
    				//设置成功，返回
    				Intent resultValue = new Intent();
    				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
    				setResult(RESULT_OK, resultValue);
    				//结束当前的Activity
    				finish();
    				return;
            	}
            }
        }
        
        //如果为true说明是第一次运行
        if(isFirstRun) {
        	//导入城市编码数据库
        	importInitDatabase();
        	
        	//显示一个对话框说明为第一次运行
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("由于本程序是第一次运行，请选择您需要了解天气的城市").setPositiveButton("确定", null);
        	AlertDialog dialog = builder.create();
        	dialog.show();
        }
        
        //增强用户体验，在加载城市列表时显示进度对话框
        final ProgressDialog dialog = getProgressDialog("", "正在加载城市列表...");
        dialog.show();
        //伸缩性列表的加载处理类
        final MyHandler mHandler = new MyHandler();
        new Thread(new Runnable() {
        	public void run() {
        		//查询处理数据库,装载伸展列表
                DBHelper dbHelper = new DBHelper(SetCityActivity.this, "db_weather.db");
                groups = dbHelper.getAllProvinces();
                List<String[][]> result = dbHelper.getAllCityAndCode(groups);
                childs = result.get(0);
                cityCodes = result.get(1);
                //交给Handler对象加载列表
                Message msg = new Message();
                mHandler.sendMessage(msg);
                dialog.cancel();
                dialog.dismiss();
        	}
        }).start();
	}
	
	//将res/raw中的城市数据库导入到安装的程序中的database目录下
    public void importInitDatabase() {
    	//数据库的目录
    	String dirPath="/data/data/com.example.helloandroid.weatherforecast.activity/databases";
    	File dir = new File(dirPath);
    	if(!dir.exists()) {
    		dir.mkdir();
    	}
    	//数据库文件
    	File dbfile = new File(dir, "db_weather.db");
    	try {
    		if(!dbfile.exists()) {
    			dbfile.createNewFile();
    		}
    		//加载欲导入的数据库
    		InputStream is = this.getApplicationContext().getResources().openRawResource(R.raw.db_weather);
    		FileOutputStream fos = new FileOutputStream(dbfile);
    		byte[] buffere=new byte[is.available()];
    		is.read(buffere);
    		fos.write(buffere);
    		is.close();
    		fos.close();

    	}catch(FileNotFoundException  e){
    		e.printStackTrace();
    	}catch(IOException e) {
    		e.printStackTrace();
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
     * 用于处理装载伸缩性列表的处理类
     */
    private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			//在伸缩性的列表中显示数据库中的省份与城市
			adapter=new MyListAdapter(SetCityActivity.this, provinceList, groups, childs);
	        provinceList.setAdapter(adapter);
	        
	        //为其子列表选项添加单击事件
	        provinceList.setOnChildClickListener(new OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					//自动跳至天气的显示界面WFMainActivity
					
					//========得到单击的城市码=======
					//得到城市名
					String cityName = (String)adapter.getChild(groupPosition, childPosition);
					//从数据库中得到城市码
					DBHelper dbHelper = new DBHelper(SetCityActivity.this, "db_weather.db");
					String cityCode = dbHelper.getCityCodeByName(cityName);
					
					Dialog dialog = getProgressDialog("", "正在加载天气...");
					dialog.show();
					//这里为什么要用线程来实现？？
					GoToWFMainActivity thread = new GoToWFMainActivity(cityCode, dialog);
					thread.start();
					
					return false;
				}
	        	
	        });
		}
    }
    
    /**
     * 处理用户选择好城市后的跳转到WFMainActivity
     */
    private class GoToWFMainActivity extends Thread {
    	
    	//保证跳转的城市码
    	private String cityCode;
    	//跳转后显示的进度对话框
    	private Dialog dialog;
    	
    	public GoToWFMainActivity(String cityCode, Dialog dialog) {
    		this.cityCode = cityCode;
    		this.dialog = dialog;
    	}
    	
    	public void run() {
    		//得到一个私有的SharedPreferences文件编辑对象
			SharedPreferences.Editor edit = getSharedPreferences(CITY_CODE_FILE, MODE_PRIVATE).edit();
			//将城市码保存
			edit.putString("code", cityCode);
			edit.commit();

			//通过判断得到的widgetId是否有效来判断是跳转到WFMainActivity或Widget
			if(mAppWidgetId==AppWidgetManager.INVALID_APPWIDGET_ID) {
				//设置成功回退到天气情况显示Activity
				Intent intent = getIntent();
				//当用户单击了城市返回，传入一个变量用于区分，是读存储文件天气，还是更新
				intent.putExtra("updateWeather", true);
				SetCityActivity.this.setResult(PublicConsts.ORIGIN_SETCITY, intent);
			} else {
				//当有效则跳至widget
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(SetCityActivity.this);
				//获得widget的视图对象
				RemoteViews views = new RemoteViews(SetCityActivity.this.getPackageName(),
						R.layout.wf_widget_layout);
				//更新widget
				Log.i("widget", "===================update  weather===========================");
				//更新widget
				WeatherWidget.updateAppWidget(views, SetCityActivity.this, appWidgetManager, cityCode);
				
				appWidgetManager.updateAppWidget(mAppWidgetId, views);
				//设置成功，返回
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
				setResult(RESULT_OK, resultValue);
			}
			SetCityActivity.this.finish();
			dialog.cancel();
			dialog.dismiss();
    	}
    }

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.set_city, menu );
		return true;
	}

}
