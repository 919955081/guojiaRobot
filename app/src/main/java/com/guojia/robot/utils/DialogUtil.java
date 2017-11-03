package com.guojia.robot.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.guojia.robot.R;
import com.guojia.robot.application.GJApplication;
import com.guojia.robot.iflytek.DealIat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class DialogUtil {
	private static LogUtil log = new LogUtil("DialogUtil", LogUtil.LogLevel.V);

	private static Dialog dialog = null;
	private static String[] listdatas;
	private static String nplistColor = "#29a4e4";

	public static Dialog ListDialog(final Context mContext, String[] listdata, OnItemClickListener listener){
		
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View layout = inflater.inflate(R.layout.alert_dialog_list, null);

		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}
		dialog = new Dialog(mContext);

		Window dialogWindow = dialog.getWindow();
		dialogWindow.setBackgroundDrawableResource(R.color.transparency);
		dialogWindow.setDimAmount(0);
		dialogWindow.setGravity(Gravity.RIGHT | Gravity.BOTTOM);

		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		//以屏幕左上角为原点，设置x、y初始值，相对于gravity
		lp.x = MethodsUtil.dip2px(mContext, 110);
		lp.y = MethodsUtil.dip2px(mContext, 60);
		dialogWindow.setAttributes(lp);

		dialog.show();
		dialogWindow.setContentView(layout);

		TextView tv_wifi_tips = (TextView) layout.findViewById(R.id.tv_wifi_tips);
		ListView list = (ListView) layout.findViewById(R.id.list);

		tv_wifi_tips.setText(Html.fromHtml(
				"主人：为您找到以下热点，请说连接<font color='"+nplistColor+"'>第几个</font>！"));

		if(listdata != null && listdata.length > 0){
			RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) list.getLayoutParams(); //取控件textView当前的布局参数
			int min = Math.min(5, listdata.length);
			linearParams.height = MethodsUtil.dip2px(mContext, min * 40);
			list.setLayoutParams(linearParams);
			
			//list.setAdapter(new ArrayAdapter<Object>(mContext, R.layout.alert_dialog_item, listdata));
			listdatas = listdata;
			list.setAdapter(new WifiListAdapter());
		}
		if(listener != null){
			list.setOnItemClickListener(listener);
		}

		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK ) {
					DealIat.iatContextType = DealIat.ContextType.NONE;
					dialog.dismiss();
					return true;
				}
				return false;
			}
		});
		dialog.setCancelable(false);
		return dialog;
	}

	public static Dialog ListPwdDialog(final Context mContext, OnClickListener connlistener, String[] listdata, OnItemClickListener listener){

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View layout = inflater.inflate(R.layout.alert_dialog_list_pwd, null);

		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}
		dialog = new Dialog(mContext);
		dialog.setContentView(layout);

		Window dialogWindow = dialog.getWindow();
		dialogWindow.setBackgroundDrawableResource(R.color.transparency);
		dialogWindow.setDimAmount(0);
		dialogWindow.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		//以屏幕左上角为原点，设置x、y初始值，相对于gravity
		lp.x = MethodsUtil.dip2px(mContext, 110);
		lp.y = MethodsUtil.dip2px(mContext, 60);
		dialogWindow.setAttributes(lp);

		dialog.show();
		dialogWindow.setContentView(layout);

		Button bt_conn = (Button) layout.findViewById(R.id.bt_conn);
		TextView tv_pwd_tips = (TextView) layout.findViewById(R.id.tv_pwd_tips);
		ListView list = (ListView) layout.findViewById(R.id.list);
		EditText et_wifipwd = (EditText) layout.findViewById(R.id.et_wifipwd);

		if(connlistener != null){
			bt_conn.setOnClickListener(connlistener);
		}
		if(listener != null){
			tv_pwd_tips.setText(Html.fromHtml(
					"主人：为您找到以下密码，请说选择<font color='"+nplistColor+"'>第几个</font>！"));
			list.setOnItemClickListener(listener);
		}else{
			tv_pwd_tips.setText(Html.fromHtml(
					"主人：请说<font color='"+nplistColor+"'>密码314***</font>，或输入密码！"));
		}

		log.i("===============: "+listdata);
		if(listdata != null && listdata.length > 0){
			RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) list.getLayoutParams(); //取控件textView当前的布局参数
			int min = Math.min(5, listdata.length);
			linearParams.height = MethodsUtil.dip2px(mContext, min * 40);
			list.setLayoutParams(linearParams);

			//list.setAdapter(new ArrayAdapter<Object>(mContext, R.layout.alert_dialog_item, listdata));
			listdatas = listdata;
			list.setAdapter(new WifiListAdapter());
		}

		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK ) {
					DealIat.iatContextType = DealIat.ContextType.NONE;
					dialog.dismiss();
					return true;
				}
				return false;
			}
		});
		dialog.setCancelable(false);
		return dialog;
	}

	private static class WifiListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return listdatas.length;
		}

		@Override
		public Object getItem(int i) {
			return null;
		}

		@Override
		public long getItemId(int i) {
			return 0;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			LayoutInflater inflater = (LayoutInflater) GJApplication.getRunActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.alert_dialog_item, null);

			TextView tv_item_namepwd = (TextView) view.findViewById(R.id.tv_item_namepwd);

			SpannableStringBuilder builder = new SpannableStringBuilder("第"+(i+1)+"个："+listdatas[i]);
			ForegroundColorSpan speakSpan = new ForegroundColorSpan(Color.parseColor(nplistColor));
			builder.setSpan(speakSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			tv_item_namepwd.setText(builder);

			return view;
		}
	}

}
