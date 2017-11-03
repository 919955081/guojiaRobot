package com.guojia.robot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guojia.robot.R;
import com.guojia.robot.iflytek.IsvManager;
import com.guojia.robot.utils.AnimationUtil;
import com.guojia.robot.utils.ToastUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;

import static android.content.ContentValues.TAG;
import static com.guojia.robot.utils.ConstUtil.VOICE_STATE;

/**
 * Created by win7 on 2017/10/24.
 */

public class VoiceActivity extends BaseActivity implements View.OnClickListener {
    private TextView showHint_tv,showMsg_tv,showPwd_tv;
    private ImageView isv_iv,iv_waveform;
    private IsvManager isvManager;
    private Button goback_btn;
    //默认状态为验证声纹状态
    private boolean fucFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_layout);
        handleData();
        initView();
    }

    private void handleData() {
        Intent intent = getIntent();
        String accountId = intent.getStringExtra("accountID");
        fucFlag = intent.getBooleanExtra(VOICE_STATE,false);
        isvManager = new IsvManager(getApplicationContext(),accountId);
    }

    private void initView() {
        iv_waveform = findViewById(R.id.iv_isv_waveform);
        showHint_tv = findViewById(R.id.show_hint_tv);
        showMsg_tv = findViewById(R.id.show_msg_tv);
        showPwd_tv = findViewById(R.id.show_pwd_tv);
        isv_iv = findViewById(R.id.voice_iv);
        goback_btn = findViewById(R.id.goback_voice);

        isv_iv.setOnClickListener(this);
        goback_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voice_iv:
                if (!fucFlag)   //为验证状态
                isvManager.verify(mVerifyListener);
                else {
                    isvManager.regist(mRegisterListener);
                }
                break;
            case R.id.goback_voice:
                finish();
                break;
        }
    }
    private VerifierListener mRegisterListener = new VerifierListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            ToastUtil.show(VoiceActivity.this,"当前正在说话，音量大小：" + i, Toast.LENGTH_SHORT);
            Log.d(TAG, "返回音频数据："+bytes.length);
        }

        @Override
        public void onBeginOfSpeech() {
            showPwd_tv.setText("请读出：" + IsvManager.TEXT_PWD_ONE);
            showHint_tv.setText("将读第" + 1 + "遍，剩余4遍");
            AnimationUtil.getInstance().startRobotWaveformAnim(iv_waveform);
            ToastUtil.show(VoiceActivity.this,"开始说话" ,Toast.LENGTH_SHORT);
        }

        @Override
        public void onEndOfSpeech() {
            AnimationUtil.getInstance().stopRobotWaveformAnim(iv_waveform);
            ToastUtil.show(VoiceActivity.this,"结束说话" ,Toast.LENGTH_SHORT);
        }

        @Override
        public void onResult(VerifierResult verifierResult) {
            showHint_tv.setText(verifierResult.source);
            if (verifierResult.ret == ErrorCode.SUCCESS){
                switch (verifierResult.err) {
                    case VerifierResult.MSS_ERROR_IVP_GENERAL:
                        showHint_tv.setText("内核异常");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_EXTRA_RGN_SOPPORT:
                        showMsg_tv.setText("训练达到最大次数");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
                        showMsg_tv.setText("出现截幅");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
                        showMsg_tv.setText("太多噪音");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
                        showMsg_tv.setText("录音太短");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
                        showMsg_tv.setText("训练失败，您所读的文本不一致");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
                        showMsg_tv.setText("音量太低");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
                        showHint_tv.setText("音频长达不到自由说的要求");
                    default:
                        showMsg_tv.setText("");
                        break;
                }
                if (verifierResult.ret == verifierResult.rgn){
                    showHint_tv.setText("注册成功");

                }else {
                    int nowTimes = verifierResult.suc + 1;
                    int leftTimes = verifierResult.rgn - nowTimes;
                    showPwd_tv.setText("请读出：" + IsvManager.TEXT_PWD_ONE);
                    showHint_tv.setText("已读第"+ nowTimes +"遍，剩余"+ leftTimes + "遍");
                }
            }else {
                showHint_tv.setText("注册失败，请重新开始。");
            }
        }

        @Override
        public void onError(SpeechError error) {
            if (error.getErrorCode() == ErrorCode.MSP_ERROR_ALREADY_EXIST) {
                ToastUtil.show(VoiceActivity.this,"模型已存在，如需重新注册，请先删除",Toast.LENGTH_SHORT);
            } else {
                ToastUtil.show(VoiceActivity.this,"onError Code：" + error.getPlainDescription(true),Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }
    };
    private VerifierListener mVerifyListener = new VerifierListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            ToastUtil.show(VoiceActivity.this,"当前正在说话，音量大小：" + i,Toast.LENGTH_SHORT);
            Log.d(TAG, "返回音频数据："+bytes.length);
        }

        @Override
        public void onBeginOfSpeech() {
            AnimationUtil.getInstance().startRobotWaveformAnim(iv_waveform);
            ToastUtil.show(VoiceActivity.this,"开始说话",Toast.LENGTH_SHORT);
        }

        @Override
        public void onEndOfSpeech() {
            AnimationUtil.getInstance().stopRobotWaveformAnim(iv_waveform);
            ToastUtil.show(VoiceActivity.this,"结束说话",Toast.LENGTH_SHORT);
        }

        @Override
        public void onResult(VerifierResult verifierResult) {
            if (verifierResult.ret == 0){
                //验证通过
                ToastUtil.show(VoiceActivity.this,"验证通过",Toast.LENGTH_SHORT);
                findViewById(R.id.voice_ver_yes_layout).setVisibility(View.VISIBLE);
                Intent verIntent = new Intent(VoiceActivity.this,MainActivity.class);
                startActivity(verIntent);

            }else{
                // 验证不通过
                switch (verifierResult.err) {
                    case VerifierResult.MSS_ERROR_IVP_GENERAL:
                        showHint_tv.setText("内核异常");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
                        showHint_tv.setText("出现截幅");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
                        showHint_tv.setText("太多噪音");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
                        showHint_tv.setText("录音太短");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
                        showHint_tv.setText("验证不通过，您所读的文本不一致");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
                        showHint_tv.setText("音量太低");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
                        showHint_tv.setText("音频长达不到自由说的要求");
                        break;
                    default:
                        showHint_tv.setText("验证不通过");
                        break;
                }
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            AnimationUtil.getInstance().stopRobotWaveformAnim(iv_waveform);
            switch (speechError.getErrorCode()) {
                case ErrorCode.MSP_ERROR_NOT_FOUND:
                    showHint_tv.setText("模型不存在，请先注册");
                    break;
                case ErrorCode.MSP_ERROR_IVP_TOO_LOW:
                    ToastUtil.show(VoiceActivity.this,"我好像听见了蚊子的声音" ,Toast.LENGTH_SHORT);
                    break;
                default:
//                    ToastUtil.show(VoiceActivity.this,"" + speechError.getPlainDescription(true),Toast.LENGTH_SHORT);
                    ToastUtil.show(VoiceActivity.this,"请再说一遍我好像没听清哦" ,Toast.LENGTH_SHORT);
                    break;
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    @Override
    protected void onDestroy() {
        isvManager.destroy();
        super.onDestroy();
    }



}
