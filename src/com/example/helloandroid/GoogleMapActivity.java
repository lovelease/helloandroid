package com.example.helloandroid;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class GoogleMapActivity extends MapActivity {

	MapView mapView;
	
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

}
