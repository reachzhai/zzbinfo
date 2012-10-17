package com.zhai.work.utils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author zhailong
 * 
 */
public interface DefaultJSONData {

	// 解析json数组
	public abstract void parse(JSONArray array);

	// 解析json对象
	public abstract void parse(JSONObject object);
}
