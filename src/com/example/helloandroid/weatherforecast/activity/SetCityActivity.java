package com.example.helloandroid.weatherforecast.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.example.helloandroid.R;
import com.example.helloandroid.weatherforecast.adapter.GPSListAdapter;
import com.example.helloandroid.weatherforecast.adapter.MyListAdapter;
import com.example.helloandroid.weatherforecast.consts.PublicConsts;
import com.example.helloandroid.weatherforecast.dao.DBHelper;
import com.example.helloandroid.weatherforecast.utils.LocationXMLParser;
import com.example.helloandroid.weatherforecast.utils.LogUtil;
import com.example.helloandroid.weatherforecast.utils.WebAccessTools;
import com.example.helloandroid.weatherforecast.widget.WeatherWidget;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

public class SetCityActivity extends Activity {

	private static final String TAG = "SetCityActivity";
	//定义的一个自动定位的列表
	private ListView gpsView;
	//定义的一个省份可伸缩性的列表
	private ExpandableListView provinceList;
	//定义的用于过滤的文本输入框
	private TextView filterText;
	//自定义的伸缩列表适配器
    private MyListAdapter adapter;
    //定位textwiew
    private TextView localeCity;
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
        gpsView.setAdapter(new GPSListAdapter(SetCityActivity.this));
		
		//==============================GPS=================================
        //当单击自动定位时
        gpsView.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View view,
					int position, long id ) {
				localeCity = (TextView)view.findViewById(R.id.locateCityText);
				localeCity.setText("正在定位...");
				
				//开启异步线程，开始定位当前城市
				String[] params = null;
	        	new LocationNetWork().execute( params );
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
            	SharedPreferences sp=getSharedPreferences(PublicConsts.CITY_CODE_FILE, MODE_PRIVATE);
            	if(sp.getString(PublicConsts.SP_CITYCODE, null)==null) {
            		//如果不存在城市码，则说明为第一次运行
            		isFirstRun = true;
            	} else {   		
            		//如存在则直接跳回
            		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(SetCityActivity.this);
    				RemoteViews views = new RemoteViews(SetCityActivity.this.getPackageName(),
    						R.layout.wf_widget_layout);
    				//得到城市码
    				String cityCode= sp.getString(PublicConsts.SP_CITYCODE, "");
    				if(cityCode!=null&&cityCode.trim().length() > 0) {
    					Log.i( PublicConsts.APP_TAG, TAG + PublicConsts.MY_APP_LOG_SYMBOL + "===================update  weather===========================");
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
    	//数据库的目录(/data/data/工程路径（不是该类的包路径com.example.helloandroid.weatherforecast.activity）/databases)
    	String dirPath="/data/data/com.example.helloandroid/databases";
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
     * 用于处理用户的定位信息
     */
    private class LocateHandler extends Handler {
    	//记录定位的文本视图组件
    	private TextView textView;
    	
    	public LocateHandler(TextView textView) {
    		this.textView = textView;
    	}
    	
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			int provinceIndex = data.getInt("provinceIndex");
			int cityIndex = data.getInt("cityIndex");
			//判断定位匹配是否成功
			if(provinceIndex >=0 && provinceIndex < groups.length && 
					cityIndex >=0 && cityIndex < childs[provinceIndex].length) {
				//显示定位的城市
				textView.setText(childs[provinceIndex][cityIndex]);
				
				//自动跳至天气的显示界面MainActivity
				Dialog dialog = getProgressDialog("", "正在加载天气...");
				dialog.show();
				GoToWFMainActivity thread = new GoToWFMainActivity(cityCodes[provinceIndex][cityIndex], dialog);
				thread.start();
			} else {
				textView.setText("定位失败！");
			}
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
			SharedPreferences.Editor edit = getSharedPreferences(PublicConsts.CITY_CODE_FILE, MODE_PRIVATE).edit();
			//将城市码保存
			edit.putString(PublicConsts.SP_CITYCODE, cityCode);
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
				Log.i( PublicConsts.APP_TAG, TAG + PublicConsts.MY_APP_LOG_SYMBOL + "===================update  weather===========================");
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
    
    /**
     * 定位当前城市
     *
     * @version 2013-6-21
     * @author PSET
     * @since JDK1.6
     *
     */
    private class LocationNetWork extends AsyncTask<String, Integer, String> {

		/* 
		 * 
		 * 根据经纬度得到详细的地址信息
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground( String... params ) {
			LogUtil.i( TAG, "LocationNetWork/doInBackground method called" );
			
			LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    	
	    	//设置一个Criteria标准用于过滤LocationProvider
	    	Criteria criteria = new Criteria();
	    	//设置不需要高度信息
	    	criteria.setAltitudeRequired(false);
	    	//设置不需要方位信息
	    	criteria.setBearingRequired(false);
	    	//设置得到的为免费
	    	//criteria.setCostAllowed(false);
	    	
	    	//得到最好的可用的Provider
	    	String provider = locationManager.getBestProvider(criteria, true);
	    	Log.i( TAG, "BestProvider : " + provider );
	    	
	    	// For test
//	    	provider = locationManager.NETWORK_PROVIDER;
//	    	provider = locationManager.GPS_PROVIDER;
	    	
	    	//得到当前的位置对象
	    	Location location = locationManager.getLastKnownLocation(provider);
	    	
	    	String addressContext = null;
	    	if (location != null) {
	    		double latitude = location.getLatitude();  //得到经度
	        	double longitude = location.getLongitude(); //得到纬度
	        	WebAccessTools webTools = new WebAccessTools(SetCityActivity.this);
	        	addressContext = webTools.getWebContent("http://maps.google.cn/maps/geo?output=xml&q="+latitude+","+longitude);
	        	
	    	}
        	
        	return addressContext;
		}
		
		/** 
		 * onPostExecute方法用于在doInBackground执行完后台任务后更新UI,显示结果
		 * @param result addressContext
		 * @return String 网络数据
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override  
        protected void onPostExecute(String result) {
			LogUtil.i( TAG, "LocationNetWork/onPostExecute method called" );
			if (result != null ) {
				//城市列表
				Map<Integer, String> cityMap = new HashMap<Integer, String>();
				
				//解析地址信息
	        	SAXParserFactory spf = SAXParserFactory.newInstance();
	        	try {
	    			SAXParser parser = spf.newSAXParser();
	    			XMLReader reader = parser.getXMLReader();
	    			LocationXMLParser handler = new LocationXMLParser();
	    			reader.setContentHandler(handler);
	    			
	    			StringReader read = new StringReader(result);
	    			// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
	    			InputSource source = new InputSource(read);
	    			
	    			//开始解析
	    			reader.parse(source);
	    			//判断是否存在地址
	    			if(handler.hasAddress()) {
	    				cityMap = handler.getDetailAddress();
	    			}
	    		} catch (ParserConfigurationException e) {
	    			e.printStackTrace();
	    		} catch (SAXException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    		
				
				//记录匹配的城市的索引
				int provinceIndex = -1;
				int cityIndex = -1;
				//传给处理类的数据封装对象
				Bundle bundle = new Bundle();
				if(cityMap!=null) {
					//得到图家名
					String country = cityMap.get(LocationXMLParser.COUNTRYNAME);
					//只匹配中国地区的天气
					if(country != null && country.equals("中国")){
						//得到省
						String province = cityMap.get(LocationXMLParser.ADMINISTRATIVEAREANAME);
						//得到市
						String city = cityMap.get(LocationXMLParser.LOCALITYNAME);
						//得到区县
						String towns = cityMap.get(LocationXMLParser.DEPENDENTLOCALITYNAME);
						
						Log.i( PublicConsts.APP_TAG, TAG + PublicConsts.MY_APP_LOG_SYMBOL + "============"+province+"."+city+"."+towns+"==============");
						//将GPS定位的城市与提供能查天气的城市进行匹配
						StringBuilder matchCity = new StringBuilder(city);
						matchCity.append(".");
						matchCity.append(towns);
						//找到省份
						for(int i=0; i<groups.length; i++) {
							if(groups[i].equals(province)) {
								provinceIndex = i;
								break;
							}
						}
						//先从区县开始查找匹配的地区
						for(int j=0; j<childs[provinceIndex].length; j++) {
							if(childs[provinceIndex][j].equals(matchCity.toString())) {
								cityIndex = j;
								break;
							}
						}
						//如果未匹配成功,则换为从城市中查找
						if(cityIndex == -1) {
							for(int j=0; j<childs[provinceIndex].length; j++) {
								if(childs[provinceIndex][j].equals(city)) {
									cityIndex = j;
									//匹配成功，则退出循环
									break;
								}
							}
						}
					}
				}
				//将其用bundle封装，用于传给Handler
				bundle.putInt("provinceIndex", provinceIndex);
				bundle.putInt("cityIndex", cityIndex);
				
				Message msg = new Message();
				msg.setData(bundle);
				//正式交由handler处理
				new LocateHandler(localeCity).sendMessage(msg);
			} else {
				LogUtil.i( TAG, "location = null,定位失败" );
				localeCity.setText("定位失败");
			}
			
		}
    	
    }

}
