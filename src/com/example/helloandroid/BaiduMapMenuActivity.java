package com.example.helloandroid;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BaiduMapMenuActivity extends Activity {

	private static final String[] MENU_LIST = { "Map Demo", "Search Demo", "Others" };
	// 各个demo对应的activity，应该和MENU_LIST对应起来
	private static final Class<?>[] ACTIVITY_LIST = { BaiduMapActivity.class };
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_baidu_map_menu );
		
		ListView lv = (ListView)findViewById(R.id.menulist);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setAdapter( new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1, MENU_LIST) );
		lv.setOnItemClickListener( new OnItemClickListener() );

	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.baidu_map_menu, menu );
		return true;
	}
	
	class OnItemClickListener implements AdapterView.OnItemClickListener{

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick( AdapterView<?> arg0, View arg1, int arg2, long arg3 ) {
			try {
				Intent intent = new Intent();
				intent.setClass(BaiduMapMenuActivity.this, ACTIVITY_LIST[arg2]);
				intent.putExtra("index", arg2);
				startActivity(intent);
			} catch (IndexOutOfBoundsException e) {
				// do nothing
			}
		}
		
	}

}
