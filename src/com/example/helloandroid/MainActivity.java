package com.example.helloandroid;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		FragmentManager fragmentManager = getFragmentManager(); 
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		// add MainFragment into MainActivity
		MainFragment fragment = new MainFragment(); 
		fragmentTransaction.add(R.id.mainActivity, fragment); 
		fragmentTransaction.commit();
	}
	
}
