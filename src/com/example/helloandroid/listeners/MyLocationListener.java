/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid.listeners;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

/**
 * Descriptions
 *
 * @version 2013-5-20
 * @author PSET
 * @since JDK1.6
 *
 */
public class MyLocationListener implements BDLocationListener {

	/* (non-Javadoc)
	 * 接收异步返回的定位结果
	 * @see com.baidu.location.BDLocationListener#onReceiveLocation(com.baidu.location.BDLocation)
	 */
	@Override
	public void onReceiveLocation( BDLocation location ) {
		if (location == null)
			return ;
		StringBuffer sb = new StringBuffer(256);
		sb.append("time : ");
		sb.append(location.getTime());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		if (location.getLocType() == BDLocation.TypeGpsLocation){
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
		} 
		Log.d( "定位结果:", sb.toString() );
//		logMsg(sb.toString());
		
	}

	/* (non-Javadoc)
	 * 接收异步返回的POI查询结果
	 * @see com.baidu.location.BDLocationListener#onReceivePoi(com.baidu.location.BDLocation)
	 */
	@Override
	public void onReceivePoi( BDLocation poiLocation ) {
		if (poiLocation == null){
			return ;
		}
		StringBuffer sb = new StringBuffer(256);
		sb.append("Poi time : ");
		sb.append(poiLocation.getTime());
		sb.append("\nerror code : ");
		sb.append(poiLocation.getLocType());
		sb.append("\nlatitude : ");
		sb.append(poiLocation.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(poiLocation.getLongitude());
		sb.append("\nradius : ");
		sb.append(poiLocation.getRadius());
		if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
			sb.append("\naddr : ");
			sb.append(poiLocation.getAddrStr());
		} 
		if(poiLocation.hasPoi()){
			sb.append("\nPoi:");
			sb.append(poiLocation.getPoi());
		}else{				
			sb.append("noPoi information");
		}
		Log.d( "POI查询结果:", sb.toString() );
//		logMsg(sb.toString());
		
	}

}
