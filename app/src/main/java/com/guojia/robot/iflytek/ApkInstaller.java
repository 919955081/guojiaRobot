package com.guojia.robot.iflytek;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;

import com.iflytek.cloud.SpeechUtility;


/**
 * 弹出提示框，下载服务组件
 */
public class ApkInstaller {
	private static AlertDialog mAlertDialog ;

	public static void install(final Activity activity){
		if(mAlertDialog != null && mAlertDialog.isShowing()){
			mAlertDialog.dismiss();
		}
		Builder builder = new Builder(activity);
		builder.setMessage("检测到您未安装语记，无法使用语音！\n是否前往下载语记？");
		builder.setTitle("下载提示");
		builder.setPositiveButton("确认前往", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				String url = SpeechUtility.getUtility().getComponentUrl();
				String assetsApk="SpeechService.apk";
				processInstall(activity, url, assetsApk);
			}
		});
		builder.setNegativeButton("残忍拒绝", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		mAlertDialog = builder.create();
		mAlertDialog.show();
		return;
	}
	/**
	 * 如果服务组件没有安装打开语音服务组件下载页面，进行下载后安装。
	 */
	private static boolean processInstall(Context context, String url, String assetsApk){
		//直接下载方式
		Uri uri = Uri.parse(url);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(it);
		return true;		
	}

}
