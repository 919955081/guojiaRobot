package com.guojia.robot.iflytek;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.guojia.robot.application.GJApplication;
import com.guojia.robot.floatwindow.FloatWindowManager;
import com.guojia.robot.okhttp.MyOkHttp;
import com.guojia.robot.okhttp.RawResponseHandler;
import com.guojia.robot.permission.DynamicPermission;
import com.guojia.robot.utils.ConstUtil;
import com.guojia.robot.utils.LogUtil;
import com.guojia.robot.utils.MethodsUtil;
import com.guojia.robot.utils.ToastUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by YL on 2017/10/13.
 */

public class IatManager {

    private static LogUtil log = new LogUtil("IatManager", LogUtil.LogLevel.V);
    private Context mContext = GJApplication.getRunActivity();

    /** 语音听写对象 */
    private SpeechRecognizer mIat;
    /** 用HashMap存储听写结果 */
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    /** 用ArrayList存储听写集合 */
    private ArrayList<String> resultList;

    public IatManager() {
        initIat();
    }

    /** 初始化语音听写 */
    public void initIat(){
        if(IflytekUtil.isInstallYuji((Activity) mContext)){
            mIat = SpeechRecognizer.createRecognizer(mContext, mInitListener);
        }
    }

    /** 开始语音听写 */
    public void startIat(){
        setIatParam();
        if(DynamicPermission.audioPermission()){
            int code = mIat.startListening(mRecognizerListener);
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                FloatWindowManager.stopRobotWaveformAnim();
                IflytekUtil.isInstallYuji((Activity) mContext);
            }else if (code != ErrorCode.SUCCESS) {
                FloatWindowManager.stopRobotWaveformAnim();
                ToastUtil.showNoRCToast(mContext, "语音听写失败，错误码: " + code, 0);
            }
        }else{
            FloatWindowManager.stopRobotWaveformAnim();
            ToastUtil.showNoRCToast(mContext, "请打开录音权限！", 0);
        }
    }

    /** 停止语音听写 */
    public void stopIat(){
        if(mIat != null && mIat.isListening()){
            mIat.stopListening();
        }
    }

    /** 语音听写初始化监听 */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            log.i("SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                ToastUtil.showNoRCToast(mContext, "语音听写初始化失败，错误码：" + code, 0);
                DynamicPermission.sdCardPermission();
            }
        }
    };

    /** 语音听写参数设置 */
    private void setIatParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置返回多候选结果
        mIat.setParameter(SpeechConstant.ASR_NBEST, "5");
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域  音乐垂直听写参数
        mIat.setParameter(SpeechConstant.ACCENT, "zh_cn");//mandarin
        // 音乐垂直听写 参数
        //mIat.setParameter(SpeechConstant.DOMAIN, "entrancemusic");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "5000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        // 设置网络超时,默认20000
        mIat.setParameter(SpeechConstant.NET_TIMEOUT, "10000");
        // 设置录音最大时长,默认60s
        mIat.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "30000");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/GJRobot_Msc/iat.wav");

        DynamicPermission.sdCardPermission();
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {}

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            log.e("mRecognizerListener onError: "+error.getErrorCode());
            if(error.getErrorCode() == 20002) {
                IflytekUtil.startTts("你的网络有点不稳定呢！");
            } else {
                IflytekUtil.startTts("你还没开始说话呢！");
            }
            FloatWindowManager.stopRobotWaveformAnim();
        }

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            log.i("iat onResult: " + results.getResultString());

            String strResult = printResult(results);

            if (isLast) {
                FloatWindowManager.stopRobotWaveformAnim();
                if(!TextUtils.isEmpty(strResult)){
                    doResult(strResult);
                }else{
                    IflytekUtil.startTts("你似乎没有说话哦！");
                }

            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {}

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };

    /**
     * 听写结果转换RecognizerResult转String
     *
     * @param results RecognizerResult数据
     * @return String
     */
    private String printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        resultList = JsonParser.parseIatResultList(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        return resultBuffer.toString();
    }

    public void doResult(String result){
        String runActivity = MethodsUtil.getActivityName(GJApplication.getRunActivity());
        int itemNumber = MethodsUtil.getItemNumber(result);

        if((DealIat.iatContextType == DealIat.ContextType.WIFI_SPEAK_PWD ||
                DealIat.iatContextType == DealIat.ContextType.WIFI_PWD_LIST) &&
                MethodsUtil.isMatcher(result, ConstUtil.WIFI_SPEAK_PWD0)){
            DealIat.openWifiPwdList(mContext, resultList);
        }else if (DealIat.iatContextType != DealIat.ContextType.NONE && itemNumber >= 0){
            log.i("doResult itemNumber: "+itemNumber);
            if(DealIat.iatContextType == DealIat.ContextType.WIFI_NAME_LIST){
                DealIat.ConnectWifi(mContext, itemNumber);
            }else if(DealIat.iatContextType == DealIat.ContextType.WIFI_PWD_LIST){
                DealIat.ConnectWifiPwd(mContext, itemNumber);
            }
        }else{
            if(MethodsUtil.isEqualsLike(result, ConstUtil.IAT_RESULT_CONN_WIFI) || result.contains("热点")){
                DealIat.openWifiNameList(mContext, result);                                          //连接WIFI
            }else{
                DealIat.sendRobot(mContext, result);                                                //发送Robot
            }
        }
    }

}
