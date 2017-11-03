package com.guojia.robot.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.guojia.robot.R;

public class SearchActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        mTop_tv = findViewById(R.id.search_cancel);
        mTop_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_cancel:
                finish();
                break;
        }
    }
}
