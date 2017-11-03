package com.guojia.robot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.guojia.robot.R;

public class RegistActivity extends BaseActivity implements View.OnClickListener{

    public static final int REGISTACTIVITY = 0;
    public static final int FORGETACTIVITY = 1;

    private TextView tv_regist;
    private Button bt_regist;
    private int activityType = REGISTACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        findViewById();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            activityType = bundle.getInt("type");

            switch (activityType) {
                case FORGETACTIVITY:
                    tv_regist.setText(R.string.regist_forget_pwd);
                break;
            }
        }
    }

    private void findViewById() {
        tv_regist = (TextView) findViewById(R.id.tv_regist);
        bt_regist = (Button) findViewById(R.id.bt_regist);

        bt_regist.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_regist:
                onBackPressed();
                break;
        }
    }
}
