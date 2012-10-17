package com.zhai.work.utils;

public class URLUtil {
	public static String setUrl(String type,String keyword){
		String url = "http://www.zhengzhoubus.com/index.aspx";
		if(Constant.LINE.equals(type)){
			url="http://218.28.136.21:8081/line.asp?xl="+keyword;
		}else if(Constant.GPS.equals(type)){
			url="http://218.28.136.21:8081/gps.asp?xl="+keyword;
		}else if(Constant.STATION.equals(type)){
			url="http://218.28.136.21:8081/station.asp?sta="+keyword;
		}
		return url;
	}
 }
