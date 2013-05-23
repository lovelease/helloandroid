package com.example.helloandroid;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainFragment extends Fragment implements Button.OnClickListener{

	private int mIndex = 0;
	
	EditText editText;
	Button okBtn;
	TextView showContent;
	Button goNextBtn;
	Button googleMapBtn;
	Button baiduMapBtn;
	
	public static MainFragment newInstance(int index) {
		MainFragment df = new MainFragment();
		Bundle args = new Bundle();
		args.putInt("index", index);
		df.setArguments(args);
		return df;
	}
	
	public static MainFragment newInstance(Bundle bundle) {
		int index = bundle.getInt("index", 0);
		return newInstance(index);
	}
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		Bundle arguments = getArguments();
		if (arguments != null) {
			mIndex = getArguments().getInt("index", 0);
		}
	}
	
	public int getShownIndex() {
		return mIndex;
	}
	
	@Override
	public void onClick(View v) {
		String welcome = this.getString(R.string.welcome);
		showContent.setText( editText.getText() + welcome );
	}
	
	class GoNextButtonOnClickLsnr implements Button.OnClickListener{

		@Override
		public void onClick( View v ) {
			Intent in = new Intent(getActivity(), IntentActivity.class);
			// Transport values to next activity
			Bundle bd = new Bundle();
			bd.putString( "inName", editText.getText().toString() );
			in.putExtras( bd );
			startActivity(in);
		}
	}
	
	class GoogleMapButtonOnClickLsnr implements Button.OnClickListener{

		@Override
		public void onClick( View v ) {
			Intent in = new Intent(getActivity(), GoogleMapActivity.class);
			startActivity(in);
		}
	}
	
	class BaiduMapButtonOnClickLsnr implements Button.OnClickListener{

		@Override
		public void onClick( View v ) {
			Intent in = new Intent(getActivity(), BaiduMapActivity.class);
			startActivity(in);
		}
	}
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		View rootView = inflater.inflate( R.layout.fragment_main, container, false );
		editText=(EditText)rootView.findViewById(R.id.nameTxt);
		okBtn = (Button)rootView.findViewById(R.id.btn);
		showContent = (TextView)rootView.findViewById(R.id.showContent);
		goNextBtn = (Button)rootView.findViewById(R.id.btn_goNext);
		googleMapBtn = (Button)rootView.findViewById(R.id.btn_googlemap);
		baiduMapBtn = (Button)rootView.findViewById(R.id.btn_baidumap);
		
		// OK button
		okBtn.setOnClickListener( this );
		
		// go next!
		goNextBtn.setOnClickListener( new GoNextButtonOnClickLsnr() );
		
		// Google Map
		googleMapBtn.setOnClickListener( new GoogleMapButtonOnClickLsnr() );
		
		// Baidu Map
		baiduMapBtn.setOnClickListener( new BaiduMapButtonOnClickLsnr() );
		return rootView;
	}

}
