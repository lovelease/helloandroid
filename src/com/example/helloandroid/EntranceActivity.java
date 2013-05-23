package com.example.helloandroid;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;

public class EntranceActivity extends Activity {

	// target activities
	Class<?> mActivities[] = {
			MainActivity.class
	};
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_entrance );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.entrance, menu );
		return true;
	}
	
	// 是否是多窗口
	public boolean isMultiPane() {
		return getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_LANDSCAPE;
	}

	public void showDetails(int index) {
		if (isMultiPane()) {// 如果是横屏(多窗口), 使用MainFragment
			MainFragment details = (MainFragment)getFragmentManager()
					.findFragmentById(R.id.details);

			if (details == null || details.getShownIndex() != index) {
				details = MainFragment.newInstance(index);

				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.replace(R.id.details, details);
				ft.addToBackStack("details");
				ft.commit();
			}
		} else {// 如果是竖屏，调用MainActivity，将当前索引值存入intent
			
			try {
				Intent intent = new Intent();
				intent.setClass(this, mActivities[index]);
				intent.putExtra("index", index);
				startActivity(intent);
			} catch (IndexOutOfBoundsException e) {
				// do nothing
			}
			
		}
	}

}
