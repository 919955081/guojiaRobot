package com.guojia.robot.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by win7 on 2017/10/26.
 */

public class SharedPreManager {
    private static final String DEFAULT_STRING = "empty";
    private static final int DEFAULT_INT = 404;
    private static final boolean DEFAULT_BOOLEAN = false;

    private static SharedPreManager mInstance = null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    public SharedPreManager(Context context){
        this.context = context;
        preferences = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static SharedPreManager getInstance(Context context){
        if (mInstance == null){
            mInstance = new SharedPreManager(context);
            synchronized (SharedPreManager.class){
                if (mInstance == null){
                    mInstance = new SharedPreManager(context);
                }
            }
        }
        return mInstance;
    }

    public void put(String key , int value){
        editor.putInt(key,value);
    }
    public int getInt(String key){
      return  preferences.getInt(key,DEFAULT_INT);
    }
    public void put(String key , String value){
        editor.putString(key,value);
    }
    public String getString(String key){
        return preferences.getString(key,DEFAULT_STRING);
    }
    public void put(String key , boolean value){
        editor.putBoolean(key,value);
    }
    public boolean getBoolean(String key){
       return preferences.getBoolean(key,DEFAULT_BOOLEAN);
    }

}
