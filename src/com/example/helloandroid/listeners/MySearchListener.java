/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid.listeners;

import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

/**
 * Descriptions
 *
 * @version 2013-5-20
 * @author PSET
 * @since JDK1.6
 *
 */
public class MySearchListener implements MKSearchListener {

	/* (non-Javadoc)
	 * 返回地址信息搜索结果
	 * @see com.baidu.mapapi.search.MKSearchListener#onGetAddrResult(com.baidu.mapapi.search.MKAddrInfo, int)
	 */
	@Override
	public void onGetAddrResult( MKAddrInfo arg0, int arg1 ) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * 返回公交车详情信息搜索结果
	 * @see com.baidu.mapapi.search.MKSearchListener#onGetBusDetailResult(com.baidu.mapapi.search.MKBusLineResult, int)
	 */
	@Override
	public void onGetBusDetailResult( MKBusLineResult arg0, int arg1 ) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * 返回驾乘路线搜索结果
	 * @see com.baidu.mapapi.search.MKSearchListener#onGetDrivingRouteResult(com.baidu.mapapi.search.MKDrivingRouteResult, int)
	 */
	@Override
	public void onGetDrivingRouteResult( MKDrivingRouteResult arg0, int arg1 ) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * 返回poi详细搜索结果
	 * @see com.baidu.mapapi.search.MKSearchListener#onGetPoiDetailSearchResult(int, int)
	 */
	@Override
	public void onGetPoiDetailSearchResult( int arg0, int arg1 ) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * 返回poi搜索结果
	 * @see com.baidu.mapapi.search.MKSearchListener#onGetPoiResult(com.baidu.mapapi.search.MKPoiResult, int, int)
	 */
	@Override
	public void onGetPoiResult( MKPoiResult arg0, int arg1, int arg2 ) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * 返回联想词信息搜索结果
	 * @see com.baidu.mapapi.search.MKSearchListener#onGetSuggestionResult(com.baidu.mapapi.search.MKSuggestionResult, int)
	 */
	@Override
	public void onGetSuggestionResult( MKSuggestionResult arg0, int arg1 ) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * 返回公交搜索结果
	 * @see com.baidu.mapapi.search.MKSearchListener#onGetTransitRouteResult(com.baidu.mapapi.search.MKTransitRouteResult, int)
	 */
	@Override
	public void onGetTransitRouteResult( MKTransitRouteResult arg0, int arg1 ) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * 返回步行路线搜索结果
	 * @see com.baidu.mapapi.search.MKSearchListener#onGetWalkingRouteResult(com.baidu.mapapi.search.MKWalkingRouteResult, int)
	 */
	@Override
	public void onGetWalkingRouteResult( MKWalkingRouteResult arg0, int arg1 ) {
		// TODO Auto-generated method stub
		
	}

}
