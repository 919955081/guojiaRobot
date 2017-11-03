package com.guojia.robot.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.guojia.robot.R;

public class BaseActivity extends Activity  {
	public TextView mTop_tv;
	public ImageView mBackImg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }*/
		requestWindowFeature(Window.FEATURE_NO_TITLE);

	}


}
