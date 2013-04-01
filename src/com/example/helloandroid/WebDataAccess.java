/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Descriptions
 *
 * @version 2013-1-24
 * @author PSET
 * @since JDK1.6
 *
 */
public class WebDataAccess {

	public static void webDataAccess( String urlStr, TextView textview) {
		//http地址
//        String httpUrl = "http://www.baidu.com";
        //获得的数据
        String resultData = "";
        URL url = null;
        try
        {
                //构造一个URL对象
                url = new URL(urlStr); 
        }
        catch (MalformedURLException e)
        {
                Log.e( "debug", "MalformedURLException" );
        }
        if (url != null)
        {
                try
                {
                        //使用HttpURLConnection打开连接
                        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                        //得到读取的内容(流)
                        InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
                        // 为输出创建BufferedReader
                        BufferedReader buffer = new BufferedReader(in);
                        String inputLine = null;
                        //使用循环来读取获得的数据
                        while (((inputLine = buffer.readLine()) != null))
                        {
                                //我们在每一行后面加上一个"\n"来换行
                                resultData += inputLine + "\n";
                        }                 
                        //关闭InputStreamReader
                        in.close();
                        //关闭http连接
                        urlConn.disconnect();
                        //设置显示取得的内容
                        if ( resultData != null )
                        {
                        	textview.setText(resultData);
                        }
                        else 
                        {
                        	textview.setText("读取的内容为NULL");
                        }
                }
                catch (IOException e)
                {
                        Log.e("debug", "IOException");
                }
        }
        else
        {
                Log.e("debug", "Url NULL");
        }
//        //设置按键事件监听
//        Button button_Back = (Button) findViewById(R.id.Button_Back);
//        
//        button_Back.setOnClickListener(new Button.OnClickListener() 
//        {
//                public void onClick(View v)
//                {
//                        
//                        Intent intent = new Intent();
//                        
//                        intent.setClass(Activity02.this, Activity01.class);
//                        
//                        startActivity(intent);
//                        
//                        Activity02.this.finish();
//                }
//        });
	}
}
