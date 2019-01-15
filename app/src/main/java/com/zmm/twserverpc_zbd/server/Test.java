package com.zmm.twserverpc_zbd.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class Test {
	
	
	private static String url = "http://172.28.6.36:8080/api-webapp/api/ACGReport/addACGReport";
	
	public static void main(String[] args) {
		
        String s_id = "test_test";
		int flag = 1;
		int curSpeed = 66;
		int curResistance = 11;
		int spasmTimes = 11;
		int spasticity = 11;
		int curDirection = 1;
		int offset = 12;
		int smartMode = 1;
		
		sendYunData(s_id, flag, curSpeed, curResistance, spasmTimes, spasticity, offset, curDirection, smartMode);
	}
	
	private static void sendYunData(String s_id, int flag, int curSpeed, int curResistance, int spasmTimes, int spasticity,
			int offset, int curDirection, int smartMode) {

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
    	String curTime = format.format(date);
    	
    	OkHttpUtils
			.post()
			.url(url)
			.addParams("s_id", s_id)
			.addParams("flag", flag+"")
			.addParams("curSpeed", curSpeed+"")
			.addParams("curResistance", curResistance+"")
			.addParams("spasmTimes", spasmTimes+"")
			.addParams("spasticity", spasticity+"")
			.addParams("curDirection", curDirection+"")
			.addParams("offset", offset+"")
			.addParams("smartMode", smartMode+"")
			.addParams("curTime", curTime)
			.build()
			.execute(new StringCallback(){
		
				@Override
				public void onError(Call arg0, Exception arg1, int arg2) {
					// TODO Auto-generated method stub
					System.out.println("执行失败！！！");
					arg1.printStackTrace();
				}
		
				@Override
				public void onResponse(String arg0, int arg1) {
					// TODO Auto-generated method stub
					System.out.println("执行成功"+arg0.toString());
				}
				
			});
    	
		
	}


}
