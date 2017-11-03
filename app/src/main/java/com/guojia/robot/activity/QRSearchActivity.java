package com.guojia.robot.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.guojia.robot.R;

public class QRSearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_search);
        initView();
    }

    private void initView() {
        mTop_tv = findViewById(R.id.mode_bar_tv);
        mTop_tv.setText(getString(R.string.qr_top_tv));
    }
}
