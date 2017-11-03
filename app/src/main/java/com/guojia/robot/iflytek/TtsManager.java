package com.guojia.robot.iflytek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.guojia.robot.application.GJApplication;
import com.guojia.robot.floatwindow.FloatWindowManager;
import com.guojia.robot.utils.ConstUtil;
import com.guojia.robot.utils.LogUtil;
import com.guojia.robot.utils.MethodsUtil;
import com.guojia.robot.utils.ToastUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.msc.VAD;

/**
 * Created by YL on 2017/10/16.
 */

public class TtsManager {

    private static LogUtil log = new LogUtil("TtsManager", LogUtil.LogLevel.V);
    private Context mContext = GJApplication.getRunActivity();

    /** 讯飞语音合成对象 */
    private SpeechSynthesizer mTts;

    public TtsManager() {
        initTts();
    }

    /** 初始化语音合成 */
    public void initTts(){
        if(IflytekUtil.isInstallYuji((Activity) mContext)){
            mTts = SpeechSynthesizer.createSynthesizer(mContext, mTtsInitListener);
        }
    }

    /** 开始语音合成 */
    public void startTts(String strTts){
        setTtsParam();
        int code = mTts.startSpeaking(strTts, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                FloatWindowManager.stopRobotSpeakAnim();
                IflytekUtil.isInstallYuji((Activity) mContext);
            } else {
                FloatWindowManager.stopRobotSpeakAnim();
                ToastUtil.showNoRCToast(mContext, "语音合成失败，错误码: " + code, 0);
            }
        }
    }

    /** 停止语音合成 */
    public void stopTts(){
        if(mTts!=null && mTts.isSpeaking()){
            mTts.stopSpeaking();
        }
    }

    /** 语音合成初始化监听 */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
        log.i("InitListener init() code = " + code);
        if (code != ErrorCode.SUCCESS) {
            ToastUtil.showNoRCToast(mContext, "语音合成初始化失败，错误码：" + code, 0);
        }
        }
    };

    /** 语音合成参数设置 */
    private void setTtsParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "");//xiaoyan
        // 设置合成语速
        //mTts.setParameter(SpeechConstant.SPEED, "50");
        // 设置合成音调
        //mTts.setParameter(SpeechConstant.PITCH, "50");
        // 设置合成音量
        //mTts.setParameter(SpeechConstant.VOLUME, "80");
        // 设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");// 0:通话 1:系统 2:铃声 3:音乐 4:闹铃 5:通知
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        //mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        //mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {}

        @Override
        public void onSpeakPaused() {}

        @Override
        public void onSpeakResumed() {}

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {}

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {

            } else if (error != null) {
                ToastUtil.showNoRCToast(mContext, "我暂时说不了话了！", 0);
                log.i("语音合成失败，错误码：" + error.getPlainDescription(true));
            }

            FloatWindowManager.stopRobotSpeakAnim();
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
}
