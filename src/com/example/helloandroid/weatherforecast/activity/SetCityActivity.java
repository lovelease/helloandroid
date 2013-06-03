package com.example.helloandroid.weatherforecast.activity;

import com.example.helloandroid.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SetCityActivity extends Activity {

	//定义的一个记录城市码的SharedPreferences文件名
	public static final String CITY_CODE_FILE="city_code";
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_set_city );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.set_city, menu );
		return true;
	}

}
