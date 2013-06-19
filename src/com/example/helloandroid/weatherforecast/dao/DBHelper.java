package com.example.helloandroid.weatherforecast.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.helloandroid.weatherforecast.activity.SetCityActivity;
import com.example.helloandroid.weatherforecast.consts.PublicConsts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * @author i-zqluo
 * 访问数据库得到省份与城市
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String TAG = "DBHelper";
	//app中访问城市数据的db文件
	private static final String DBFILE = "/data/data/com.example.helloandroid/databases/db_weather.db";
	
	/**
	 * 构建一个数据库操作对象
	 * @param context 当前程序的上下文对象
	 * @param dataname 数据库名
	 */
	public DBHelper(Context context, String dataname){
		super(context, dataname, null, 2);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	/**
	 * 得到所有支持的省份或直辖市名称的String类型数组
	 * 表名和列名都是在db文件（db_weather.db）中定义的
	 * @return 支持的省份或直辖市数组
	 */
	public String[] getAllProvinces() {
		String[] columns={"name"};
		
		SQLiteDatabase db = getReadableDatabase();
		
		//为避免查询出错，在执行查询语句之前，需要先判断db是否存在，不存在的情况下，显式地生成db，方法有以下两种：
		//1.将查询语句执行两遍，第一遍用于检查db存在性
//		try {
//			db.query("provinces", columns, null, null, null, null, null);
//		} catch (Exception e) {
//			SetCityActivity sca = new SetCityActivity();
//			sca.importInitDatabase();
//		}
		//2.直接判断db文件的存在性，不存在则生成db文件
		File file= new File(DBFILE);
		if ( !file.exists() ) {
			SetCityActivity sca = new SetCityActivity();
			sca.importInitDatabase();
		}
		//查询获得游标
		Log.d( PublicConsts.APP_TAG, TAG + PublicConsts.MY_APP_LOG_SYMBOL + "========search provinces start=======" );
		Cursor cursor = db.query("provinces", columns, null, null, null, null, null);
		columns = null;
		int count= cursor.getCount();
		Log.d( PublicConsts.APP_TAG, TAG + PublicConsts.MY_APP_LOG_SYMBOL + "========search provinces end=======" );
		Log.d( PublicConsts.APP_TAG, TAG + PublicConsts.MY_APP_LOG_SYMBOL + "count=" + count );
		String[] provinces = new String[count];
		count=0;
		while(!cursor.isLast()) {
			cursor.moveToNext();
			provinces[count] = cursor.getString(0);
			count=count+1;
		}
		cursor.close();
		db.close();
		return provinces;
	}
	
	/**
	 * 根据省份数组来得到对应装有对应的城市名和城市编码的列表对象
	 * 表名和列名都是在db文件（db_weather.db）中定义的
	 * @param provinces 省份数组
	 * @return 索引0为对应的城市名的二维数组和索引1为对应城市名的二维数组
	 */
	public List<String[][]> getAllCityAndCode(String[] provinces) {
		int length= provinces.length;
		String[][] city = new String[length][];
		String[][] code = new String[length][];
		int count = 0;
		SQLiteDatabase db = getReadableDatabase();
		for(int i=0; i<length; i++) {
			Cursor cursor = db.query("citys", new String[]{"name", "city_num"},
					"province_id = ? ", new String[]{String.valueOf(i)}, null, null, null);
			count = cursor.getCount();
			city[i] = new String[count];
			code[i] = new String[count];
			count = 0;
			while(!cursor.isLast()) {
				cursor.moveToNext();
				city[i][count] = cursor.getString(0);
				code[i][count] = cursor.getString(1);
				count = count + 1;
			}
		    cursor.close();
		}
		db.close();
		List<String[][]> result = new ArrayList<String[][]>();
		result.add(city);
		result.add(code);
		return result;
	}
	
	/**
	 * 由城市名查询数据库来得到城市码
	 * @param cityName 城市名
	 * @return 城市码
	 */
	public String getCityCodeByName(String cityName) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("citys", new String[]{"city_num"},
				"name = ? ", new String[]{cityName}, null, null, null);
		String cityCode = null;
		if(!cursor.isLast()){
			cursor.moveToNext();
			cityCode = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return cityCode;
	}
}
