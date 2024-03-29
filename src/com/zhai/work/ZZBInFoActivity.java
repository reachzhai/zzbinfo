package com.zhai.work;

import java.util.Iterator;
import com.zhai.work.utils.Constant;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ZZBInFoActivity extends BaseActivity implements OnClickListener {
	private TextView InfoLine;
	private TextView InfoGPS;
	private TextView InfoStation;
	private TextView myPostion;
	private EditText keywordLine;
	private EditText keywordGPS;
	private EditText keywordStation;
	private LocationManager lm;
    private static final String TAG="GpsActivity";
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	private void init() {
		InfoLine = (TextView) this.findViewById(R.id.info_line);
		InfoLine.setOnClickListener(this);
		InfoGPS = (TextView) this.findViewById(R.id.info_gps);
		InfoGPS.setOnClickListener(this);
		InfoStation = (TextView) this.findViewById(R.id.info_station);
		InfoStation.setOnClickListener(this);
		keywordLine = (EditText) this.findViewById(R.id.keyword_line);
		keywordGPS = (EditText) this.findViewById(R.id.keyword_gps);
		keywordStation = (EditText) this.findViewById(R.id.keyword_station);
		myPostion=(TextView)this.findViewById(R.id.mypositon);
//		initGPS();
	}

	private void initGPS() {
		 lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
	        
	        //判断GPS是否正常启动
	        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
	            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
	            //返回开启GPS导航设置界面
	            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
	            startActivityForResult(intent,0); 
	            return;
	        }
	        
	        //为获取地理位置信息时设置查询条件
	        String bestProvider = lm.getBestProvider(getCriteria(), true);
	        //获取位置信息
	        //如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
	        Location location= lm.getLastKnownLocation(bestProvider);    
	        updateView(location);
	        //监听状态
	        lm.addGpsStatusListener(listener);
	        //绑定监听，有4个参数    
	        //参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
	        //参数2，位置信息更新周期，单位毫秒    
	        //参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息    
	        //参数4，监听    
	        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新   
	        
	        // 1秒更新一次，或最小位移变化超过1米更新一次；
	        //注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
	        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
		
	}

	@Override
	public void onClick(View v) {
		Intent i;
		String keyword;
		switch (v.getId()) {
		case R.id.info_line:
			i = new Intent(this, ResultInfoResultActivity.class);
			keyword = getInfoInput(keywordLine);
			if (keyword != null) {
				startNEW(i, Constant.LINE, keyword);
			} else {
				showTips();
			}
			break;
		case R.id.info_gps:
			i = new Intent(this, ResultInfoResultActivity.class);
			keyword = getInfoInput(keywordGPS);
			if (keyword != null) {
				startNEW(i, Constant.GPS, keyword);
			} else {
				showTips();
			}
			break;
		case R.id.info_station:
			i = new Intent(this, ResultInfoResultActivity.class);
			keyword = getInfoInput(keywordStation);
			if (keyword != null) {
				startNEW(i, Constant.STATION, keyword);
			} else {
				showTips();
			}
			break;

		default:
			break;
		}
		super.onClick(v);
	}

	private void startNEW(Intent i, String type, String keyword) {
		i.putExtra(Constant.TYPE, type);
		i.putExtra(Constant.KEYWORD, keyword);
		this.startActivity(i);
	}

	private void showTips() {
		Dialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.errorTitle).setMessage(R.string.input_error)
				.setIcon(R.drawable.info).create();
		alertDialog.show();
	}

	private String getInfoInput(EditText view) {
		String keyword = null;
		if (view != null && view.getText() != null
				&& !"".equals(view.getText().toString().trim())) {
			keyword = view.getText().toString();
		}
		return keyword;
	}
	
	
	/**
     * 实时更新文本内容
     * 
     * @param location
     */
    private void updateView(Location location){
        if(location!=null){
        	myPostion.setText("经度："+String.valueOf(location.getLongitude()));
            myPostion.append(" 纬度：");
            myPostion.append(String.valueOf(location.getLatitude()));
        }else{
            //清空EditText对象
        }
    }
	 //位置监听
    private LocationListener locationListener=new LocationListener() {
        
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            updateView(location);
            Log.i(TAG, "时间："+location.getTime()); 
            Log.i(TAG, "经度："+location.getLongitude()); 
            Log.i(TAG, "纬度："+location.getLatitude()); 
            Log.i(TAG, "海拔："+location.getAltitude()); 
        }
        
        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
            //GPS状态为可见时
            case LocationProvider.AVAILABLE:
                Log.i(TAG, "当前GPS状态为可见状态");
                break;
            //GPS状态为服务区外时
            case LocationProvider.OUT_OF_SERVICE:
                Log.i(TAG, "当前GPS状态为服务区外状态");
                break;
            //GPS状态为暂停服务时
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.i(TAG, "当前GPS状态为暂停服务状态");
                break;
            }
        }
    
        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location=lm.getLastKnownLocation(provider);
            updateView(location);
        }
    
        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            updateView(null);
        }

    
    };
    
    //状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
            //第一次定位
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Log.i(TAG, "第一次定位");
                break;
            //卫星状态改变
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                Log.i(TAG, "卫星状态改变");
                //获取当前状态
                GpsStatus gpsStatus=lm.getGpsStatus(null);
                //获取卫星颗数的默认最大值
                int maxSatellites = gpsStatus.getMaxSatellites();
                //创建一个迭代器保存所有卫星 
                Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                int count = 0;     
                while (iters.hasNext() && count <= maxSatellites) {     
                    GpsSatellite s = iters.next();     
                    count++;     
                }   
                System.out.println("搜索到："+count+"颗卫星");
                break;
            //定位启动
            case GpsStatus.GPS_EVENT_STARTED:
                Log.i(TAG, "定位启动");
                break;
            //定位结束
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.i(TAG, "定位结束");
                break;
            }
        };
    };
    
    /**
     * 返回查询条件
     * @return
     */
    private Criteria getCriteria(){
        Criteria criteria=new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细 
        criteria.setAccuracy(Criteria.ACCURACY_FINE);    
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费  
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求  
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

}