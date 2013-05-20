package com.example.helloandroid;

import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.helloandroid.listeners.MyLocationListener;
import com.example.helloandroid.listeners.MySearchListener;
import com.example.helloandroid.listeners.NotifyListener;

import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class BaiduMapActivity extends Activity {

	BMapManager mBMapMan = null;
	MapView mMapView = null;
	// 声明搜索类
	MKSearch mMKSearch = null;
	// 声明定位功能所需的LocationClient类
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	// 声明位置提醒所需的BDNotifyListener类
	BDNotifyListener mNotifyer = null;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		/** 基础地图显示相关代码 **/
		//注意：请在使用setContentView前初始化BMapManager对象，否则会报错
		mBMapMan=new BMapManager(getApplication());
		mBMapMan.init("510AC356B06AAFD8D4D95FB560DBFEB6621EE9CD", null);
		
		setContentView( R.layout.activity_baidu_map );
		
		// open GPS
		Context context = getApplicationContext();
		if ( !isOPen( context ) ) {
			openGPS(context);
		}
		
		mMapView=(MapView)findViewById(R.id.bmapsView);
		// 添加地图缩放控件
		mMapView.setBuiltInZoomControls(true);
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		MapController mMapController=mMapView.getController();
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
		// 设置地图中心点
		mMapController.setCenter(point);
		// 设置地图zoom级别（当前缩放大小）
		mMapController.setZoom(12);
		// 地图图层：实时交通信息图
//		mMapView.setTraffic(true);
		// 地图图层：卫星图
//		mMapView.setSatellite(true);
		
		// 初始化搜索服务
		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapMan, new MySearchListener());//注意，MKSearchListener只支持一个，以最后一次设置为准
		
		/** 定位相关代码 **/
		// 实例化LocationClient类
		mLocationClient = new LocationClient(context);
		// 注册监听函数
		mLocationClient.registerLocationListener( myListener );
		// 设置定位参数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);//禁止启用缓存定位
		option.setPoiNumber(5);	//最多返回POI个数	
		option.setPoiDistance(1000); //poi查询距离		
		option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息		
		mLocationClient.setLocOption(option);
		// 发起定位请求。请求过程是异步的，定位结果监听函数onReceiveLocation中获取。
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else 
			Log.d("LocSDK3", "locClient is null or not started");
		// 发起POI查询请求。请求过程是异步的，定位结果在监听函数onReceivePoi中获取。
//		if (mLocationClient != null && mLocationClient.isStarted())
//			mLocationClient.requestPoi();
		// 发起离线定位请求。请求过程是异步的，定位结果在监听函数onReceiveLocation中获取。
//		if (mLocationClient != null && mLocationClient.isStarted())
//			mLocationClient.requestOfflineLocation();
		
		/** 位置提醒相关代码 **/
		mNotifyer = new NotifyListener();
		mNotifyer.SetNotifyLocation(42.03249652949337,113.3129895882556,3000,"gps");//4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)
		//注册位置提醒监听事件后，可以通过SetNotifyLocation 来修改位置提醒设置，修改后立刻生效。
		mLocationClient.registerNotify(mNotifyer);
		//取消位置提醒
//		mLocationClient.removeNotifyEvent(mNotifyer);
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.baidu_map, menu );
		return true;
	}
	
	@Override
	protected void onDestroy() {
		mMapView.destroy();
		if ( mBMapMan != null ) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		if ( mBMapMan != null ) {
			mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		if ( mBMapMan != null ) {
			mBMapMan.start();
		}
		super.onResume();
	}
	
	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * @param context
	 * @return
	 */
	public static final boolean isOPen(final Context context) {
		LocationManager locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}
		return false;
	}
	
	/**
	 * to open GPS forcedly
	 * @param context
	 */
	public static final void openGPS(Context context) {
		Intent GPSIntent = new Intent();
		GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		GPSIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
		}
		catch ( CanceledException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
