/**
 *  Copyright(C) 2012 Pioneer Electronics Co., Ltd.
 *  All Right Reserved.
 */
package com.example.helloandroid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

/**
 * Descriptions
 *
 * @version 2013-1-24
 * @author PSET
 * @since JDK1.6
 *
 */
public class CopyOfWebDataAccess {

	/**获取参数(ArrayList<NameValuePair> nameValuePairs,String url)后post给远程服务器
	 * 将获得的返回结果(String)返回给调用者
	 * 本函数适用于查询数量较少的时候
	 * Chen.Zhidong
	 * 2011-02-15*/
	public String posturl(ArrayList<NameValuePair> nameValuePairs,String url){
	    String result = "";
	    String tmp= "";
	    InputStream is = null;
	    try{
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(url);
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	    }catch(Exception e){
	        return "Fail to establish http connection!";
	    }
	 
	    try{
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	        is.close();
	 
	        tmp=sb.toString();
	    }catch(Exception e){
	        return "Fail to convert net stream!";
	    }
	 
	    try{
	        JSONArray jArray = new JSONArray(tmp);
	        for(int i=0;i<jArray.length();i++){
	            JSONObject json_data = jArray.getJSONObject(i);
	            Iterator<?> keys=json_data.keys();
	            while(keys.hasNext()){
	                result += json_data.getString(keys.next().toString());
	            }
	        }
	    }catch(JSONException e){
	        return "The URL you post is wrong!";
	    }
	 
	    return result;
	}
}
