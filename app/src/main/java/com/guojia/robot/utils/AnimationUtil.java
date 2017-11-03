package com.guojia.robot.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * Created by win7 on 2017/10/27.
 */

public class AnimationUtil {
    private static final int ANIMATION_DUR = 1200;
    private static AnimationUtil mInstance = null;
    private AnimationSet animWaveform;
    public AnimationUtil(){

    }
    public static AnimationUtil getInstance(){
        if (mInstance == null) {
            mInstance = new AnimationUtil();
        }
        return mInstance;
    }
    /**
     * 打开波形动画
     *
     * @return true:无动画则运行动画  false:有动画则停止动画
     */
    public boolean startRobotWaveformAnim(ImageView iv_waveform) {
        if(!stopRobotWaveformAnim(iv_waveform)){
            iv_waveform.setVisibility(View.VISIBLE);
            animWaveform = initAnimationSet(ANIMATION_DUR);
            iv_waveform.startAnimation(animWaveform);
            return true;
        }else{
            return false;
        }
    }
    /**
     *
     * @return 返回波形动画器
     */
    private AnimationSet initAnimationSet(int duration) {
        AnimationSet as = new AnimationSet(true);

        ScaleAnimation sa = new ScaleAnimation(1f, 3f, 1f, 3f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(duration);
        sa.setRepeatCount(Animation.INFINITE);// 设置循环
        as.addAnimation(sa);

        AlphaAnimation aa = new AlphaAnimation(1f, 0f);
        aa.setDuration(duration);
        aa.setRepeatCount(Animation.INFINITE);//设置循环
        as.addAnimation(aa);

        return as;
    }
    /**
     * 关闭波形动画
     *
     * @return true:如果动画不为空且在运行则关闭  false:无动画
     * @param iv_waveform
     */
    public boolean stopRobotWaveformAnim(ImageView iv_waveform) {
        if(animWaveform != null && !animWaveform.hasEnded()){
            iv_waveform.clearAnimation();
            iv_waveform.setVisibility(View.GONE);
            animWaveform = null;
            return true;
        }else{
            animWaveform = null;
            return false;
        }
    }

}
