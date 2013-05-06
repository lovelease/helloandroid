package com.example.helloandroid;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements Button.OnClickListener{

	EditText editText;
	Button okBtn;
	TextView showContent;
	Button goNextBtn;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		editText=(EditText)findViewById(R.id.nameTxt);
		okBtn = (Button)findViewById(R.id.btn);
		showContent = (TextView)findViewById(R.id.showContent);
		goNextBtn = (Button)findViewById(R.id.btn_goNext);
		
		// OK button
		okBtn.setOnClickListener( this );
		
		// go next!
		goNextBtn.setOnClickListener( new GoNextButtonOnClickLsnr() );
		
	}
	
	@Override
	public void onClick(View v) {
		String welcome = this.getString(R.string.welcome);
		showContent.setText( editText.getText() + welcome );
		
//		String httpUrl = "http://www.baidu.com";
//		WebDataAccess.webDataAccess(httpUrl, showContent);
	}
	
	class GoNextButtonOnClickLsnr implements Button.OnClickListener{

		@Override
		public void onClick( View v ) {
			Intent in = new Intent(MainActivity.this, IntentActivity.class);
			// Transport values to next activity
			Bundle bd = new Bundle();
			bd.putString( "inName", editText.getText().toString() );
			in.putExtras( bd );
			startActivity(in);
		}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.activity_main, menu );
		return true;
	}

}
