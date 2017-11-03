package com.guojia.robot.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guojia.robot.R;

import org.w3c.dom.Text;

public class VoiceSearchActivity extends BaseActivity implements View.OnClickListener {
    private TextView titleBar_tv;
    private ImageView titleBar_back,voiceSearch_Iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_search);
        initView();
    }

    private void initView() {
        titleBar_tv = findViewById(R.id.mode_bar_tv);
        titleBar_back = findViewById(R.id.back);
        voiceSearch_Iv = findViewById(R.id.voice_search_iv);

        voiceSearch_Iv.setOnClickListener(this);
        titleBar_back.setOnClickListener(this);
        titleBar_tv.setText(getString(R.string.voice_search));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.voice_search_iv:
                //语音搜索
                break;
        }
    }
}
