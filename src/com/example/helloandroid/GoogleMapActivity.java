package com.example.helloandroid;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class GoogleMapActivity extends MapActivity {

	MapView mapView;
	private LocationManager locationManager = null;
	private String bestProvider = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_map);
		mapView = (MapView) findViewById(R.id.mapView);
		
		// 设置地图显示类型：卫星，地图，街道（默认设置为街道模式）
		mapView.setStreetView(true);
		// 卫星模式
//		mapView.setSatellite(true);
		// 是否显示交通信息
//		mapView.setTraffic(true);
		
		
		// 添加地图缩放控件
		mapView.setBuiltInZoomControls( true );
		// 控制缩放比例
		MapController mapController = mapView.getController();
		// 设置 16 档为当前缩放大小(google map共21档）（具体缩放效果见：http://greg-koppel.site88.net/maps/InfoDisplay.html）
		mapController.setZoom( 16 );
		
		// 获取 Android 系统中最后一次更新的地理位置信息
		Context context = getApplicationContext();
		getLastKnowLocation(context);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.google_map, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 获得最优的 LocationProvider
	 * @param context
	 * @return 最优 LocationProvider
	 */
	private String getBestProvider(Context context) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE); // 设置精度
		criteria.setAltitudeRequired(false); // 设置是否需要提供海拔信息
		criteria.setBearingRequired(false); // 是否需要方向信息
		criteria.setCostAllowed(false); // 设置找到的 Provider 是否允许产生费用
		criteria.setPowerRequirement(Criteria.POWER_LOW); // 设置耗电
		
		locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		String provider=locationManager.getBestProvider(criteria, true);
		// 这里可能返回 null, 在这里需要提示用户当前地理位置信息服务未开启，否则将会有"java.lang.IllegalArgumentException: provider==null"异常抛出
		return provider;
	}
	
	/**
	 * 获取 Android 系统中最后一次更新的地理位置信息
	 * @param context
	 * @return 最后一次更新的地理位置信息
	 */
	public Location getLastKnowLocation(Context context) {
		Location ret = null;
		this.bestProvider = getBestProvider(context);
		
		if(this.bestProvider != null) {
			ret = locationManager.getLastKnownLocation(this.bestProvider);
		}
		// 这里可能会返回 null, 表示按照当前的查询条件无法获取系统最后一次更新的地理位置信息
		return ret;
	}

}
