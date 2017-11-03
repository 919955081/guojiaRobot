package com.guojia.robot.floatwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.guojia.robot.R;
import com.guojia.robot.application.GJApplication;
import com.guojia.robot.iflytek.ApkInstaller;
import com.guojia.robot.iflytek.FucUtil;
import com.guojia.robot.iflytek.IflytekUtil;
import com.guojia.robot.utils.LogUtil;
import com.guojia.robot.utils.MethodsUtil;
import com.guojia.robot.utils.ToastUtil;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/** 悬浮窗的布局 */
public class FloatLayout extends FrameLayout implements View.OnClickListener{
    private static LogUtil log = new LogUtil("FloatLayout", LogUtil.LogLevel.V);

    /** 动画播放时间 */
    private final int OFFSET = 700;

    //private final WindowManager mWindowManager;
    private RelativeLayout rl_robot;
    private ImageView iv_robot1, iv_robot2, iv_robot_waveform;
    private AnimationDrawable animSpeak;
    private AnimationSet animWaveform;

    private WindowManager.LayoutParams mWmParams;
    private Context mContext;

    public FloatLayout(Context context) {
        this(context, null);
        mContext = context;
    }

    public FloatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_robot_layout, this);

        iv_robot1 = (ImageView) findViewById(R.id.iv_robot1);
        iv_robot2 = (ImageView) findViewById(R.id.iv_robot2);
        iv_robot_waveform = (ImageView) findViewById(R.id.iv_robot_waveform);
        rl_robot = (RelativeLayout) findViewById(R.id.rl_robot);

        rl_robot.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_robot:
                if(IflytekUtil.isInstallYuji((Activity) mContext)){
                    boolean b = isRobotWaveformAnimStart();
                    if(!b){
                        IflytekUtil.startIat();
                    }else{
                        IflytekUtil.stopIat();
                    }
                }
                break;
        }
    }

    public void startRobotSpeakAnim(){
        stopRobotSpeakAnim();

        iv_robot1.setVisibility(View.VISIBLE);
        iv_robot1.setBackgroundResource(R.drawable.robot_speak_anim);
        animSpeak = (AnimationDrawable) iv_robot1.getBackground();
        animSpeak.start();
    }

    /**
     * 关闭说话动画
     *
     * @return true:如果动画不为空且在运行则关闭  false:无动画
     */
    public boolean stopRobotSpeakAnim(){
        if(animSpeak != null && animSpeak.isRunning()){
            animSpeak.stop();
            iv_robot1.setVisibility(View.GONE);
            animSpeak = null;
            return true;
        }else{
            animSpeak = null;
            return false;
        }
    }

    public boolean isRobotSpeakAnimStart() {
        if(animSpeak != null && animSpeak.isRunning()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 打开波形动画
     *
     * @return true:无动画则运行动画  false:有动画则停止动画
     */
    public boolean startRobotWaveformAnim() {
        if(!stopRobotWaveformAnim()){

            iv_robot_waveform.setVisibility(View.VISIBLE);
            animWaveform = initAnimationSet();
            iv_robot_waveform.startAnimation(animWaveform);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 关闭波形动画
     *
     * @return true:如果动画不为空且在运行则关闭  false:无动画
     */
    public boolean stopRobotWaveformAnim() {
        if(animWaveform != null && !animWaveform.hasEnded()){
            iv_robot_waveform.clearAnimation();
            iv_robot_waveform.setVisibility(View.GONE);
            animWaveform = null;
            return true;
        }else{
            animWaveform = null;
            return false;
        }
    }

    public boolean isRobotWaveformAnimStart() {
        if(animWaveform != null && !animWaveform.hasEnded()){
            return true;
        }else{
            return false;
        }
    }

    private AnimationSet initAnimationSet() {
        AnimationSet as = new AnimationSet(true);

        ScaleAnimation sa = new ScaleAnimation(1f, 1.5f, 1f, 1.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(OFFSET);
        sa.setRepeatCount(Animation.INFINITE);// 设置循环
        as.addAnimation(sa);

        AlphaAnimation aa = new AlphaAnimation(1f, 0f);
        aa.setDuration(OFFSET);
        aa.setRepeatCount(Animation.INFINITE);//设置循环
        as.addAnimation(aa);

        return as;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mWmParams = params;
    }

}
