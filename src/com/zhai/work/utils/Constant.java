package com.zhai.work.utils;

import java.io.File;
import android.os.Environment;

public class Constant {
	public static final File SD_PATH = Environment.getExternalStorageDirectory();
	public static final File BASE_PATH = new File(SD_PATH, ".zzbus");
	public static final int TIMEOUT_TIME = 30000;// 联网超时时间,30s
	public static final File PATH_SETTING = new File(BASE_PATH, "d");
	public static final String TYPE = "type";
	public static final String KEYWORD = "keyword";
	public static final String LINE = "line";
	public static final String GPS = "gps";
	public static final String STATION = "station";
}
