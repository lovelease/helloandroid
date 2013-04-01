package com.example.helloandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements Button.OnClickListener{

	EditText editText;
	Button okBtn;
	TextView showContent;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		editText=(EditText)findViewById(R.id.nameTxt);
		okBtn = (Button)findViewById(R.id.btn);
		showContent = (TextView)findViewById(R.id.showContent);
		
		okBtn.setOnClickListener( this );
	}
	
	@Override
	public void onClick(View v) {
		String welcome = this.getString(R.string.welcome);
		showContent.setText( editText.getText() + welcome );
		
//		String httpUrl = "http://www.baidu.com";
//		WebDataAccess.webDataAccess(httpUrl, showContent);
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.activity_main, menu );
		return true;
	}

}
