package com.zmm.twserverpc_zbd.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zmm.twserverpc_zbd.client.model.ReportModel;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;

public class MyOkHttpUtils {
	
	private static String url2 = "http://172.28.6.36:8080/api-webapp/api/ACGReport/addACGReport";
	private static String url = "http://test.ricamed.com/api/activePassive/addReport.json";
//	private static String url = "http://172.28.6.83:9090/api/activePassive/addReport.json";

	public static void sendYunData(String s_id, int flag, int curSpeed, int curResistance, int spasmTimes, int spasticity,
			int offset, int curDirection, int smartMode) {

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
    	String curTime = format.format(date);
    	
    	OkHttpUtils
			.post()
			.url(url2)
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
				}
		
				@Override
				public void onResponse(String arg0, int arg1) {
					// TODO Auto-generated method stub
					System.out.println("执行成功");
				}
				
			});
    	
		
	}

	/**
	 * 发送数据报告
	 * @param reportModel
	 */
	public static void sendReport(ReportModel reportModel) {

		String json = JSON.toJSONString(reportModel);
		System.out.println("报告json ： ： "+json);

		OkHttpUtils
				.postString()
				.url(url)
				.content(json)
				.mediaType(MediaType.parse("application/json; charset=utf-8"))
				.build()
				.execute(new Callback() {
					@Override
					public Object parseNetworkResponse(Response response, int i) throws Exception {
						return null;
					}

					@Override
					public void onError(Call call, Exception e, int i) {
						System.out.println("发送失败");
						e.printStackTrace();
					}

					@Override
					public void onResponse(Object o, int i) {
						System.out.println("报告生成成功");
					}
				});

	}
}
