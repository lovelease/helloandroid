package com.example.helloandroid;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;

public class BaiduMapActivity extends Activity {

	BMapManager mBMapMan = null;
	MapView mMapView = null;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
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
