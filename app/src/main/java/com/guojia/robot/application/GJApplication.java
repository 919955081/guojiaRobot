package com.guojia.robot.application;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.guojia.robot.floatwindow.FloatWindowManager;
import com.guojia.robot.iflytek.IatManager;
import com.guojia.robot.iflytek.TtsManager;
import com.guojia.robot.permission.FloatPermission;
import com.guojia.robot.utils.ConstUtil;
import com.guojia.robot.utils.MethodsUtil;
import com.guojia.robot.utils.ToastUtil;
import com.iflytek.cloud.SpeechUtility;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;

public class GJApplication extends Application{

	private static IatManager mIatManager = null;
	private static TtsManager mTtsManager = null;
    private static Activity runActivity = null;

    @Override
    public void onCreate() {
    	super.onCreate();

		// 添加讯飞AppId
		SpeechUtility.createUtility(getApplicationContext(), "appid=" + "59e05af7");
		//初始化微博授权
		WbSdk.install(getApplicationContext(),new AuthInfo(getApplicationContext(),ConstUtil.WEIBO_APP_KEY,ConstUtil.WEIBO__REDIRECT_URL,ConstUtil.WEIBO_SCOPE));
    	registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }



	public static IatManager getIatManager(){
		if(mIatManager == null){
			return new IatManager();
		}
		return mIatManager;
	}
	public static TtsManager getTtsManager(){
		if(mTtsManager == null){
			return new TtsManager();
		}
		return mTtsManager;
	}

	public static Activity getRunActivity(){
		return runActivity;
	}

	private class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks{
		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
		@Override
		public void onActivityStarted(Activity activity) {}
		@Override
		public void onActivityResumed(Activity activity) {
			runActivity = activity;

			String runActivity = MethodsUtil.getActivityName(activity);
			if(MethodsUtil.isEquals(runActivity, ConstUtil.ACTIVITY_ROBOT_NAME)){
				boolean isPermission = FloatPermission.getInstance().applyFloatWindow(activity);
				//有对应权限或者系统版本小于7.0
				if (isPermission || Build.VERSION.SDK_INT < 24) {
					FloatWindowManager.createFloatWindow(activity);
					if(mIatManager == null){
						mIatManager = new IatManager();
					}
					if(mTtsManager == null){
						mTtsManager = new TtsManager();
					}
				}
			}else{
				FloatWindowManager.removeFloatWindowManager();
			}
		}
		@Override
		public void onActivityPaused(Activity activity) {}
		@Override
		public void onActivityStopped(Activity activity) {}
		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
		@Override
		public void onActivityDestroyed(Activity activity) {}
	}

}
