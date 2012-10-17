package com.zhai.work.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import com.zhai.work.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

public class Tools {
	public int responseValue;// 1代表正常0代表其它错误2代表服务端相应错误，3代表解析错误
	public Builder builder;
	public static String ip_data;
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/*
	 * 获取sdk版本信息
	 */
	public String getSDKVersion(Context context) {
		String ver = "";
		String verInt = android.os.Build.VERSION.SDK;
		if ("2".equals(verInt)) {
			ver = "1.1";
		} else if ("3".equals(verInt)) {
			ver = "1.5";
		} else if ("4".equals(verInt)) {
			ver = "1.6";
		} else if ("5".equals(verInt)) {
			ver = "2.0";
		} else if ("6".equals(verInt)) {
			ver = "2.0.1";
		} else if ("7".equals(verInt)) {
			ver = "2.1";
		} else if ("8".equals(verInt)) {
			ver = "2.2";
		} else if ("9".equals(verInt)) {
			ver = "2.3.1";
		} else if ("10".equals(verInt)) {
			ver = "2.3.3";
		} else if ("11".equals(verInt)) {
			ver = "3.0";
		} else if ("12".equals(verInt)) {
			ver = "3.1";
		} else {
			ver = "other";
		}
		return ver;
	}

	/*
	 * 获取运营商信息
	 */
	public String getCarrier(Context context) {
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telephony.getSubscriberId();
		if (imsi != null && !"".equals(imsi)) {
			if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
				return "ChinaMobile";
			} else if (imsi.startsWith("46001")) {
				return "ChinaUnicom";
			} else if (imsi.startsWith("46003")) {
				return "ChinaTelecom";
			} else {
				return "othes";
			}
		}

		return null;
	}

	/*
	 * 读取手机串号
	 */
	public String readTelephoneSerialNum(Context con) {
		TelephonyManager telephonyManager = (TelephonyManager) con
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/*
	 * MD5加密字符
	 */
	public String md5(String source) {
		/*
		 * if (true) { // true表示在测试阶段 return source; } else {
		 */
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			digest.update(source.getBytes());
			byte[] mess = digest.digest();
			return toHexString(mess);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return source;
		// }

	}

	public String toHexString(byte[] b) { // byte to String
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	/*
	 * 获取路径中的文件
	 */
	public String getFileNameFromURL(String url) {
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		return fileName;
	}

	/*
	 * 根据URL得到图片
	 */
	public Bitmap getBitmapFromUrl(String urlStr) {
		try {
			URL url = new URL(urlStr);
			URLConnection connection = url.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			Bitmap bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
			inputStream.close();
			return bitmap;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 保存文件到指定路径，后缀名修改为.dat
	 */
	public void saveImageToSDAndChangePostfix(Bitmap bitmap,
			String path, String fileName) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		BufferedOutputStream bos=null;
		String name = fileName.substring(0, fileName.lastIndexOf("."));
		File imageFile = new File(path + name + ".dat");
		try {
			bos = new BufferedOutputStream(
					new FileOutputStream(imageFile));
			if (bitmap != null) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bos!=null){
					bos.flush();
					bos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	}

	/*
	 * 保存文件到指定路径，后缀名不修改
	 */
	@SuppressWarnings("hiding")
	public void saveImageToSD(Bitmap bitmap, String path, String fileName) {
		File dir = new File(path);
		BufferedOutputStream bos =null;
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File imageFile = new File(path + fileName);
		try {
			bos = new BufferedOutputStream(
					new FileOutputStream(imageFile));
			if (bitmap != null) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bos!=null){
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}

	/*
	 * 获取路径中的文件
	 */
	public String getFileName(String url) {
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		return fileName;
	}

	/*
	 * 从asset中得到source id
	 */
	public String getSourceIdFromAsset(Context context) {
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open("sourid.txt");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte buf[] = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			baos.flush();
			baos.close();
			inputStream.close();
			return baos.toString();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/*
	 * 判断网络是否可用̬
	 */
	public boolean isAccessNetwork(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity.getActiveNetworkInfo() != null
				&& connectivity.getActiveNetworkInfo().isAvailable()) {
			return true;
		}
		return false;
	}

	/*
	 * 获取网络信息
	 */
	public String getAccessNetworkType(Context context) {
		int type = 0;
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info == null) {
			return null;
		}
		type = info.getType();
		if (type == ConnectivityManager.TYPE_WIFI) {
			return "wifi";
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			return "3G/GPRS";
		}
		return null;
	}

	/*
	 * 获取SD卡剩余空间，无卡则返回-1
	 */
	public long getSDSize() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			String path = Environment.getExternalStorageDirectory().getPath();
			StatFs statFs = new StatFs(path);
			long l = statFs.getBlockSize();
			return statFs.getAvailableBlocks() * l;
		}

		return -1;
	}

	/*
	 * 获取当前时间
	 */
	public String getNowTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(Calendar.getInstance().getTime());
	}

	// 删除指定目录下所有文件
	public void deleteAllFile(final String filePath) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				File display = new File(filePath);
				if (!display.exists()) {
					return;
				}
				File[] items = display.listFiles();
				int i = display.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (items[j].isFile()) {
						items[j].delete();// 删除文件
					}
				}
			}
		});
		t.start();
	}

	// 清空指定路径下的的所有文件
	public void deleteAllFiles(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			return;
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			int len = files.length;
			for (int i = 0; i < len; i++) {
				deleteAllFiles(files[i].getPath());
			}
		} else {
			file.delete();
		}

	}

	/*
	 * 网络链接错误对话框
	 */
	public void netError(Context con) {
		AlertDialog.Builder adb = new AlertDialog.Builder(con);
		adb.setTitle(R.string.errorTitle);
		adb.setMessage(R.string.nonetwork);
		adb.setPositiveButton("确定", null);
		adb.show();
	}

	/**
	 * 获得本地ip地址
	 * @return
	 */
	public String getLocalIpAddress() {
		try {
				for (Enumeration<NetworkInterface> en = NetworkInterface
				.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress()) {
								return inetAddress.getHostAddress().toString();
							}
					}
				}
		} catch (SocketException ex) {
				Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}
	
	/**
	 * 获得联网类型
	 * @param context
	 * @return
	 */
	public String getNetType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = cm.getActiveNetworkInfo();
		String typeName = info.getTypeName();
		return typeName;

	}
	
	/**
	 * 获得屏幕尺寸
	 * @param context
	 * @return
	 */
	public String getScreenSize(Context context) {
		
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		return metrics.widthPixels + "x" + metrics.heightPixels;
	}
	/**
	 * Mac地址
	 * @param context
	 * @return
	 */
	public String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) ((Activity)context).getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}
	
	public  DefaultHandler requestToParseXML(String url,Context context,
			Map<String, String> data, DefaultHandler xmlParse) {
		responseValue = 0;
		// 封装传送的数据
		ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
		if (data != null) {
			for (Map.Entry<String, String> m : data.entrySet()) {
				postData.add(new BasicNameValuePair(m.getKey(), m.getValue()));
			}
		}
		HttpPost httpPost = new HttpPost(url);
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpResponse response = null;
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData,
					HTTP.UTF_8);
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				responseValue = 1;
			} else {
				responseValue = 2;
				httpPost.abort();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			responseValue = 2;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			responseValue = 2;
		} catch (IOException e) {
			e.printStackTrace();
			responseValue = 2;
		}

		// 响应正常
		if (responseValue != 2) {
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();  
            SAXParser parser;
            XMLReader reader;
			try {
				parser = saxFactory.newSAXParser();
				reader = parser.getXMLReader();
				reader.setContentHandler(xmlParse);
				HttpEntity httpEntity = response.getEntity();
				Log.e("解析XML文件---", response.getEntity().toString());
				InputStream inputStream = httpEntity.getContent();
				parser.parse(inputStream,xmlParse);
				responseValue = 1;// 解析成功
				inputStream.close();
			} catch (ParserConfigurationException e1) {
				responseValue = 3;
				e1.printStackTrace();
			} catch (SAXException e1) {
				responseValue = 3;
				e1.printStackTrace();
			} catch (IllegalStateException e) {
				responseValue = 3;
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				responseValue = 3;
			}  finally{
				
			}
		}
		return xmlParse;
	}
	

	
	
	public String GetIpAddress(Context mContext) {
		//获取ip地址
		if(getSDKVersion(mContext).equals("other")){
			return getLocalIpAddress4_0();//如果是4。0  则返回默认值
		}
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ip = ips.nextElement();
                    if (!ip.isLoopbackAddress()) {
                    	ip_data= ip.getHostAddress();
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
	
	public String getLocalIpAddress4_0() {  
        try {  
            String ipv4;  
            ArrayList<NetworkInterface>  nilist = Collections.list(NetworkInterface.getNetworkInterfaces());  
            for (NetworkInterface ni: nilist)   
            {  
                ArrayList<InetAddress>  ialist = Collections.list(ni.getInetAddresses());  
                for (InetAddress address: ialist){  
                    if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress()))   
                    {   
                        return ipv4;  
                    }  
                }  
   
            }  
   
        } catch (SocketException ex) {  
            Log.e("e", ex.toString());  
        }  
        return null;  
    }  
	
	/*
	 * POST请求并解析返回的XML
	 */
	public DefaultHandler requestToParse(Context context, String url, Map<String, String> data,
			DefaultHandler handler) {
		responseValue = 0;
		// 封装传 ?的数 ?
		ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
		if (data != null) {
			for (Map.Entry<String, String> m : data.entrySet()) {
				postData.add(new BasicNameValuePair(m.getKey(), m.getValue()));
			}
		}
		HttpPost httpPost = new HttpPost(url);
		
		// 设置连接网络超时时间
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);

		HttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpResponse response = null;

		try {
			// 对请求的数据进行UTF-8转码
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, HTTP.UTF_8);
			httpPost.setEntity(entity);

			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				responseValue = 1;
			} else {
				responseValue = 2;
				httpPost.abort();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			responseValue = 2;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			responseValue = 2;
		} catch (IOException e) {
			e.printStackTrace();
			responseValue = 2;
		}

		// 响应正常
		InputStreamReader isr = null;
		if (responseValue != 2) {
			try {
				HttpEntity httpEntity = response.getEntity();
				InputStream inputStream = httpEntity.getContent();

				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parse = factory.newSAXParser();
				XMLReader reader = parse.getXMLReader();
				reader.setContentHandler(handler);
				isr = new InputStreamReader(inputStream);
				InputSource iSource = new InputSource(isr);
				reader.parse(iSource);
				responseValue = 1;// 解析成功

			} catch (IllegalStateException e) {
				e.printStackTrace();
				// 解析错误
				responseValue = 3;
			} catch (IOException e) {
				e.printStackTrace();
				// 解析错误
				responseValue = 3;
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				// 解析错误
				responseValue = 3;
			} catch (SAXException e) {
				e.printStackTrace();
				// 解析错误
				responseValue = 3;
			}

			finally {
				try {
					if (isr != null)
						isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				isr = null;
			}
		}

		return handler;
	}
	
	/*
	 * POST请求并解析返回的json
	 */
	public  DefaultJSONData requestToParse(Context context,String url, DefaultJSONData jsonData) {
		responseValue = 0;
		// 封装传送的数据
		HttpPost httpPost = new HttpPost(url);
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				responseValue = 1;
			} else {
				responseValue = 2;
				httpPost.abort();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			responseValue = 2;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			responseValue = 2;
		} catch (IOException e) {
			e.printStackTrace();
			responseValue = 2;
		}

		// 响应正常
		if (responseValue != 2) {
			try {
				HttpEntity httpEntity = response.getEntity();
				InputStream inputStream = httpEntity.getContent();
				StringBuffer buff = new StringBuffer();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String temp = null;
				while ((temp = reader.readLine()) != null) {
					buff.append(temp);
				}
				String buffData = buff.toString();
				if (buffData.startsWith("{")) {
					JSONObject object = new JSONObject(buffData);
					jsonData.parse(object);
				} else if (buffData.startsWith("[")) {
					JSONArray object = new JSONArray(buff.toString());
					jsonData.parse(object);
				}
				responseValue = 1;// 解析成功
			} catch (Exception e) {
				e.printStackTrace();
				responseValue = 3;
			}
		}
		else{
		}
		return jsonData;
	}

	
}
