package com.guojia.robot.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.guojia.robot.R;

import static com.guojia.robot.utils.ConstUtil.FACE_STATE;
import static com.guojia.robot.utils.ConstUtil.VOICE_STATE;

public class FaceSetActivity extends BaseActivity implements View.OnClickListener {
    private Button mAddFaceBtn;
    private RelativeLayout mContentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_set);
        initData();
        initView();
    }

    private void initView() {
        mBackImg = findViewById(R.id.back);
        mAddFaceBtn = findViewById(R.id.add_face);
        mContentLayout = findViewById(R.id.set_face_msg_content);

        mBackImg.setOnClickListener(this);
        mAddFaceBtn.setOnClickListener(this);
    }

    private void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add_face:
                //开启注册模式
                Intent faceRegist = new Intent(this,FaceActivity.class);
                faceRegist.putExtra(FACE_STATE,true);
                faceRegist.putExtra("accountID","wklceshi");
                startActivity(faceRegist);
                break;
        }
    }
}
