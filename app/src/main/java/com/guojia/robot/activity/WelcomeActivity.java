package com.guojia.robot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.guojia.robot.R;

public class WelcomeActivity extends BaseActivity {

    private TextView tv_time;
    private int count = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                handler.removeCallbacksAndMessages(null);
                finish();
            }
        });
        handler.sendEmptyMessageDelayed(0, 1000);

    }

    private int getCount() {
        count--;
        if (count == 0) {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            handler.removeCallbacksAndMessages(null);
            finish();
        }
        return count;
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                tv_time.setText("跳过 "+getCount()+" 秒");
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        };
    };


}
