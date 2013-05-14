package com.example.helloandroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;


public class IntentActivity extends Activity {

	TextView showMsgFromPrePage;
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_intent );
		
		showMsgFromPrePage = (TextView)findViewById(R.id.showMsgFromPrePage);
		// Get values from previous page
		Bundle bd = getIntent().getExtras(); 
		String name = bd.getString( "inName" ); 
		showMsgFromPrePage.setText( name + ",you succeeded in transporting between activities!" );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.activity_intent, menu );
		return true;
	}

}
