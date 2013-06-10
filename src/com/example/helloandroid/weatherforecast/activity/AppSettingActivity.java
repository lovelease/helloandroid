package com.example.helloandroid.weatherforecast.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.helloandroid.R;
import com.example.helloandroid.weatherforecast.consts.PublicConsts;
import com.example.helloandroid.weatherforecast.utils.Utility;

public class AppSettingActivity extends Activity {

	private static final String[] SETTING_MENU_LIST = { 
		"更新间隔"
		, "Others"
	};
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_app_setting );
		
		ListView lv = (ListView)findViewById(R.id.settingmenulist);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setAdapter( new ArrayAdapter<String>(getApplicationContext(),
				R.layout.customize_list_item1, SETTING_MENU_LIST) );
		lv.setOnItemClickListener( new OnItemClickListener() );
	}

	class OnItemClickListener implements AdapterView.OnItemClickListener{

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick( AdapterView<?> arg0, View arg1, int index, long arg3 ) {
			switch (index) {
			case 0:
				SharedPreferences sp = AppSettingActivity.this.getSharedPreferences( PublicConsts.STORE_WEATHER, MODE_PRIVATE );
				//得到天气缓冲文件中的更新间隔
	    		long updInterval = sp.getLong(PublicConsts.WEATHER_FILE_UPD_INTERVAL, PublicConsts.DEFAULT_UPD_INTERVAL);
	    		//更新间隔是1小时或未定义的时间
	    		int checked = 0;
	    		//更新间隔是2小时
	    		if (updInterval == 2*60*60*1000) {
	    			checked = 1;
	    		//更新间隔是3小时
	    		} else if (updInterval == 3*60*60*1000) {
	    			checked = 2;
	    		}
				new AlertDialog.Builder( AppSettingActivity.this )
						.setTitle( "设置更新间隔" )
						.setIcon( android.R.drawable.ic_dialog_info )
						.setSingleChoiceItems( new String[] { "1小时", "2小时", "3小时" }, checked,
								new OnUpdIntervalClickListerner() ).setNegativeButton( "取消", null ).show();
				break;
			default:
				Toast.makeText( AppSettingActivity.this, "This part has not finished yet", Toast.LENGTH_SHORT ).show();
				break;
			}
				
			
		}
		
		class OnUpdIntervalClickListerner implements DialogInterface.OnClickListener {

			/* (non-Javadoc)
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
			 */
			@Override
			public void onClick( DialogInterface dialog, int which ) {
				//时间间隔
				int interval = 0;
				switch (which) {
				case 0 :
					interval = 1;
					break;
				case 1 :
					interval = 2;
					break;
				case 2 :
					interval = 3;
					break;
				default:
					break;
				}
				Utility.setUpdInterval( AppSettingActivity.this, interval );
				//关闭单选框
				dialog.dismiss();
			}
			
		}
		
	}

}
