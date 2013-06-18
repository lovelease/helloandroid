/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid.weatherforecast.utils;


import com.example.helloandroid.weatherforecast.adapter.MyListAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

/**
 * Descriptions
 *
 * @version 2013-6-18
 * @author PSET
 * @since JDK1.6
 *
 */
public class CityDBMaker {

//	private Context context;
//	private ExpandableListView provinceList;
	/**
	 * 
	 */
	public CityDBMaker() {
	}
	
	/**
	 * 
	 */
//	public CityDBMaker(Context context, ExpandableListView provinceList) {
//		this.context = context;
//		this.provinceList = provinceList;
//	}
	
	public void makeDB() {
    	//==================================for test==========================================
    	WebAccessTools webTools = new WebAccessTools();
        //得到访问网络的内容
        String webContent=webTools.getWebContent("http://m.weather.com.cn/data5/city.xml");
        
        //第一次解析得到的为省份或一级直辖市
        String[][] provinces = WeaterInfoParser.parseCity(webContent);
        String[] groups = new String[provinces.length];
        String[][] childs = new String[provinces.length][];
        String[][] cityCode = new String[provinces.length][];
        for(int i=0; i< provinces.length; i++) {
        	groups[i] = provinces[i][1];
        	//由省份码来得到城市码
        	StringBuffer urlBuilder= new StringBuffer("http://m.weather.com.cn/data5/city");
        	urlBuilder.append(provinces[i][0]);
        	urlBuilder.append(".xml");
        	webContent = webTools.getWebContent(urlBuilder.toString());
        	String[][] citys = WeaterInfoParser.parseCity(webContent);
        	//用于保存所的有towns
        	String[][][] towns = new String[citys.length][][];
        	//计算总的城镇数
        	int sum=0;
        	for(int j=0; j<citys.length; j++) {
        		//由城市码来得到地方码
        		urlBuilder= new StringBuffer("http://m.weather.com.cn/data5/city");
        		urlBuilder.append(citys[j][0]);
        		urlBuilder.append(".xml");
        		webContent = webTools.getWebContent(urlBuilder.toString());
        		towns[j] = WeaterInfoParser.parseCity(webContent);
        		sum = sum + towns[j].length;
        	}
        	
        	childs[i] = new String[sum];
        	cityCode[i] = new String[sum];
        	
        	sum=0;
        	for(int j=0; j<citys.length; j++) {
        		for(int n=0; n<towns[j].length; n++) {
        			if(n==0)
        				childs[i][sum] = towns[j][n][1];
        			else
        				childs[i][sum] = towns[j][0][1] + "." + towns[j][n][1];
        			
        			urlBuilder= new StringBuffer("http://m.weather.com.cn/data5/city");
        			urlBuilder.append(towns[j][n][0]);
        			urlBuilder.append(".xml");
        			
        			webContent = webTools.getWebContent(urlBuilder.toString());
        			String[][] code=WeaterInfoParser.parseCity(webContent);
        			cityCode[i][sum] = code[0][1];
        			sum = sum + 1;
        		}
        	}
        	urlBuilder=null;
        }
        
//        BaseExpandableListAdapter adapter=new MyListAdapter(context, provinceList, groups, childs);
//        provinceList.setAdapter(adapter);
        
        //============================Create Database================================
        //打开或创建一个数据库
        String path="/data"+ Environment.getDataDirectory().getAbsolutePath() + "/com.weather.app/db_weather.db";
        
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase
                                  (path, null);
        //创建一个省份表
        String sql="create table provinces (_id integer primary key autoincrement, name text)";
        database.execSQL(sql);
        
        //创建城市表
        sql = "create table citys (_id integer primary key autoincrement, province_id integer, name text, city_num text)";
        database.execSQL(sql);
        
        //插入省份数据
        ContentValues cv = null;
        for(int i=0; i<provinces.length; i++) {
        	cv = new ContentValues();
        	cv.put("name", provinces[i][1]);
        	database.insert("provinces", null, cv);
        }
        //插入城市数据
        for(int i=0; i<childs.length; i++) {
        	for(int j=0; j<childs[i].length; j++) {
        		cv = new ContentValues();
        		cv.put("province_id", i);
        		cv.put("name", childs[i][j]);
        		cv.put("city_num", cityCode[i][j]);
        		database.insert("citys", null, cv);
        	}
        }
        cv = null;
        database.close();
    }
	
	public static void main(String[] args) {
		CityDBMaker dbMaker = new CityDBMaker();
		dbMaker.makeDB();
	}

}
