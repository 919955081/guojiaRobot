package com.guojia.robot.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.Manifest;
import android.util.Log;

import com.guojia.robot.application.GJApplication;
import com.guojia.robot.permission.DynamicPermission;

public class LogUtil {

	private static final String LOG_TAG = "DBug";
	private static final boolean PRINT_LOG = false;
	public enum LogLevel {
		V(1), I(2), D(3), W(4), E(5);

		private int level;

		private LogLevel(int level) {
			this.level = level;
		}

		public boolean isEnableLog(LogLevel configLevel) {
			if (level >= configLevel.level) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static String getSDpath() {
		String path = "";
		if (android.os.Environment.getExternalStorageState().equalsIgnoreCase(
				android.os.Environment.MEDIA_MOUNTED)) {
			path = android.os.Environment.getExternalStorageDirectory()
					.toString();
		}
		return path;
	}

	@SuppressWarnings("unused")
	public static void printSDcard(String str) {
		if(!PRINT_LOG){
			return;
		}
		if(!DynamicPermission.sdCardPermission()){
			return;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
	    String currentDateTimeString = df.format(new Date(System.currentTimeMillis()));
		if (str == null) {
			Log.w(LOG_TAG, "print str is null");
			return;
		}
		String timelog = currentDateTimeString + " " + str;
		String sdpath = getSDpath() + "/YLing.txt";
		try {
			File f = new File(sdpath);
			if (f == null) {
				Log.w(LOG_TAG, "not path:" + sdpath);
				return;
			}
			if (!f.isFile()) {
				f.createNewFile();
			}else if(f.length() > 10000000){
				boolean suc = f.delete();
				if(suc){
					f.createNewFile();
				}
			}
			FileWriter fw = new FileWriter(f, true);
			//System.out.println("\r\n");// 换行
			if (fw == null) {
				Log.w(LOG_TAG, "not write:" + sdpath);
				return;
			}
			fw.write(timelog);
			fw.write("\r\n");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String tag;
	private LogLevel level;
	private LogLevel V = LogLevel.V;
	private LogLevel I = LogLevel.I;
	private LogLevel D = LogLevel.D;
	private LogLevel W = LogLevel.W;
	private LogLevel E = LogLevel.E;

	public LogUtil(String tag, LogLevel level) {
		this.tag = tag;
		this.level = level;
	}

	public void v(String s) {
		if (V.isEnableLog(level)) {
			Log.v(LOG_TAG, "[" + tag + "] " + s);
			printSDcard("[" + tag + "] " + s);
		}
	}

	public void v(String s, Throwable t) {
		if (V.isEnableLog(level)) {
			Log.v(LOG_TAG, "[" + tag + "] " + s, t);
		}
	}

	public void i(String s) {
		if (I.isEnableLog(level)) {
			Log.i(LOG_TAG, "[" + tag + "] " + s);
			printSDcard("[" + tag + "] " + s);
		}
	}

	public void i(String s, Throwable t) {
		if (I.isEnableLog(level)) {
			Log.i(LOG_TAG, "[" + tag + "] " + s, t);
		}
	}

	public void d(String s) {
		if (D.isEnableLog(level)) {
			Log.d(LOG_TAG, "[" + tag + "] " + s);
			printSDcard("[" + tag + "] " + s);
		}
	}

	public void d(String s, Throwable t) {
		if (D.isEnableLog(level)) {
			Log.d(LOG_TAG, "[" + tag + "] " + s, t);
		}
	}

	public void w(String s) {
		if (W.isEnableLog(level)) {
			Log.w(LOG_TAG, "[" + tag + "] " + s);
			printSDcard("[" + tag + "] " + s);
		}
	}

	public void w(String s, Throwable t) {
		if (W.isEnableLog(level)) {
			Log.w(LOG_TAG, "[" + tag + "] " + s, t);
		}
	}

	public void e(String s) {
		if (E.isEnableLog(level)) {
			Log.e(LOG_TAG, "[" + tag + "] " + s);
			printSDcard("[" + tag + "] " + s);
		}
	}

	public void e(String s, Throwable t) {
		if (E.isEnableLog(level)) {
			Log.e(LOG_TAG, "[" + tag + "] " + s, t);
		}
	}
}
