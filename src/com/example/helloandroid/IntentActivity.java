package com.example.helloandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class IntentActivity extends Activity {

	TextView showMsgFromPrePage;
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_intent );
		
		showMsgFromPrePage = (TextView)findViewById(R.id.showMsgFromPrePage);
		showMsgFromPrePage.setText( "success" );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.activity_intent, menu );
		return true;
	}

}
