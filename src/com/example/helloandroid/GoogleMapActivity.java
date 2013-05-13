package com.example.helloandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class GoogleMapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_map);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.google_map, menu);
		return true;
	}

}
