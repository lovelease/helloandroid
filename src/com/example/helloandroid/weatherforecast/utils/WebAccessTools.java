/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid.weatherforecast.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.example.helloandroid.weatherforecast.consts.PublicConsts;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Descriptions 网站访问工具类，用于Android的网络访问
 *
 * @version 2013-6-3
 * @author PSET
 * @since JDK1.6
 *
 */
public class WebAccessTools {

	private static final String TAG = "WebAccessTools";
	/**
	 * 当前的Context上下文对象
	 */
	private Context context;
	/**
	 * 构造一个网站访问工具类
	 * @param context 记录当前Activity中的Context上下文对象
	 */
	public WebAccessTools(Context context) {
		this.context = context;
	}
	
	/**
	 * 无context对象的构造方法供本地调用
	 */
	public WebAccessTools() {
	}
	
	/**
	 * 根据给定的url地址访问网络，得到响应内容(这里为GET方式访问)
	 * @param url 指定的url地址
	 * @return web服务器响应的内容，为<code>String</code>类型，当访问失败时，返回为null
	 */
	public  String getWebContent(String url) {
		LogUtil.i( TAG, "web access url：" + url );
		//创建一个http请求对象
		HttpGet request = new HttpGet(url);
		//创建HttpParams以用来设置HTTP参数
		HttpParams params=new BasicHttpParams();
		//设置连接超时或响应超时
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSoTimeout(params, 5000);
		//创建一个网络访问处理对象
		HttpClient httpClient = new DefaultHttpClient(params);
		try{
			LogUtil.i( TAG, "send request start" );
			//执行请求参数项
			HttpResponse response = httpClient.execute(request);
			LogUtil.i( TAG, "get response success" );
			//判断是否请求成功
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				LogUtil.i( TAG, "网络访问成功,status code = " + response.getStatusLine().getStatusCode() );
				//获得响应信息
				String content = EntityUtils.toString(response.getEntity());
				LogUtil.i( TAG, "response content = " + content );
				return content;
			} else {
				LogUtil.i( TAG, "网络访问失败,status code = " + response.getStatusLine().getStatusCode() );
				if (context != null) {
					//网连接失败，使用Toast显示提示信息
					Toast.makeText(context, "网络访问失败，请检查网络设置!", Toast.LENGTH_SHORT).show();
				} else {
					System.out.println("网络访问失败");
				}
			}
			
		}catch(Exception e) {
			LogUtil.e(TAG, "getWebContent throws exception :" + e.getMessage());
		} finally {
			//释放网络连接资源
			httpClient.getConnectionManager().shutdown();
		}
		return null;
	}

}
