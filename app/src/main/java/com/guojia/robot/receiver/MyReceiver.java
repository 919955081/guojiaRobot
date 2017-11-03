package com.guojia.robot.receiver;

import com.guojia.robot.iflytek.IflytekUtil;
import com.guojia.robot.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class MyReceiver extends BroadcastReceiver{

	private LogUtil log = new LogUtil("MyReceiver", LogUtil.LogLevel.V);
	private Context mContext;
	public static boolean pwdErroeSpeak = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
		log.i("intent type: "+intent.getAction());
		if(intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)){
			int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
			if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
				if(pwdErroeSpeak){
					IflytekUtil.startTts("密码不对哦！");
					pwdErroeSpeak = false;
				}
				log.e("MyReceiver: WifiManager.ERROR_AUTHENTICATING");
			}

		}
			
	}

}
