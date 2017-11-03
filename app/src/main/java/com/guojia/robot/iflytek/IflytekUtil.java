package com.guojia.robot.iflytek;

import android.app.Activity;
import android.text.TextUtils;

import com.guojia.robot.application.GJApplication;
import com.guojia.robot.floatwindow.FloatWindowManager;
import com.guojia.robot.utils.LogUtil;
import com.guojia.robot.utils.ToastUtil;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by YL on 2017/10/18.
 */

public class IflytekUtil {
    private static LogUtil log = new LogUtil("IflytekUtil", LogUtil.LogLevel.V);

    /** 安装讯飞语记 */
    public static boolean isInstallYuji(Activity activity){
        if (!SpeechUtility.getUtility().checkServiceInstalled()) {
            ApkInstaller.install(activity);
            return false;
        } else {
            String result = FucUtil.checkLocalResource();
            if (!TextUtils.isEmpty(result)) {
                ToastUtil.showNoRCToast(activity, result, 0);
                return false;
            }
        }
        return true;
    }

    /** 开启语音听写（关闭语音合成-关闭合成动画） */
    public static void startIat(){
        stopTts();
        stopIat();
        FloatWindowManager.startRobotWaveformAnim();
        GJApplication.getIatManager().startIat();
    }
    /** 关闭语音听写 */
    public static void stopIat(){
        FloatWindowManager.stopRobotWaveformAnim();
        GJApplication.getIatManager().stopIat();
    }

    /** 开启语音合成（关闭语音合成-关闭语音听写-关闭听写动画） */
    public static void startTts(String str){
        stopTts();
        stopIat();
        FloatWindowManager.startRobotSpeakAnim();
        GJApplication.getTtsManager().startTts(str);
    }
    /** 关闭语音合成 */
    public static void stopTts(){
        FloatWindowManager.stopRobotSpeakAnim();
        GJApplication.getTtsManager().stopTts();
    }

}
