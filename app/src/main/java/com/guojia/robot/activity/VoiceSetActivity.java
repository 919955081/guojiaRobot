package com.guojia.robot.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guojia.robot.R;
import com.guojia.robot.iflytek.IsvManager;
import com.guojia.robot.utils.ToastUtil;

import static com.guojia.robot.utils.ConstUtil.AUTHER_ID;
import static com.guojia.robot.utils.ConstUtil.VOICE_STATE;

public class VoiceSetActivity extends BaseActivity implements View.OnClickListener {
    private Button mAddVoiceBtn;
    private RelativeLayout mContentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_set);
        initData();
        initView();
    }

    private void initData() {

    }

    private void initView() {
        mTop_tv = findViewById(R.id.mode_bar_tv);
        mTop_tv.setText(getString(R.string.voice_name));
        mBackImg = findViewById(R.id.back);
        mAddVoiceBtn = findViewById(R.id.add_voice);
        mContentLayout = findViewById(R.id.set_voice_msg_content);

        mBackImg.setOnClickListener(this);
        mAddVoiceBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add_voice:
                //跳转到voiceActivity 开启注册模式
                Intent voiceRegist = new Intent(this,VoiceActivity.class);
                voiceRegist.putExtra(VOICE_STATE,true);
                //重复注册问题
                voiceRegist.putExtra("accountID","wklceas2342hi");
                startActivity(voiceRegist);
                break;
        }
    }
    /** 动态创建tv*/
    private TextView createMsg() {
        TextView tv = new TextView(this);
        RelativeLayout.LayoutParams lp  = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,52);
        lp.addRule(Gravity.CENTER_VERTICAL);
        Drawable rightDrawable = getResources().getDrawable(R.mipmap.intor_right_icon);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        tv.setCompoundDrawables(null, null, rightDrawable, null);
        tv.setLayoutParams(lp);
        return tv;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
