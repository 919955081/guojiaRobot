package com.guojia.robot.floatwindow;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;

import com.guojia.robot.utils.LogUtil;
import com.guojia.robot.utils.MethodsUtil;

/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:悬浮窗统一管理，与悬浮窗交互的真正实现
 */
public class FloatWindowManager {
    private static LogUtil log = new LogUtil("FloatWindowManager", LogUtil.LogLevel.V);

    /**
     * 悬浮窗
     */
    private static FloatLayout mFloatLayout;
    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams wmParams;
    private static boolean mHasShown;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void createFloatWindow(Context context) {
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(mFloatLayout != null){
                isAttach = mFloatLayout.isAttachedToWindow();
            }else{
                isAttach = false;
            }
        }
        if (mHasShown && isAttach && mWindowManager != null){
            return;
        }

        mFloatLayout = new FloatLayout(context);
        WindowManager windowManager = getWindowManager(context);// context.getSystemService(Context.WINDOW_SERVICE);//

        wmParams = new WindowManager.LayoutParams();
        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        if (Build.VERSION.SDK_INT >= 24) { /*android7.0不能用TYPE_TOAST*/
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else { /*以下代码块使得android6.0之后的用户不必再去手动开启悬浮窗权限*/
            String packname = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packname));
            if (permission) {
                wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;

        //以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = MethodsUtil.dip2px(context, 0);
        wmParams.y = MethodsUtil.dip2px(context, 40);

        mFloatLayout.setParams(wmParams);
        windowManager.addView(mFloatLayout, wmParams);
        mHasShown = true;
    }

    /**
     * 移除悬浮窗
     */
    public static void removeFloatWindowManager() {
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if(mFloatLayout != null){
                isAttach = mFloatLayout.isAttachedToWindow();
            }else{
                isAttach = false;
            }
        }
        if (mHasShown && isAttach && mWindowManager != null){
            mWindowManager.removeView(mFloatLayout);
        }
    }

    /**
     * 返回当前已创建的WindowManager。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }


    public static void hide() {
        if (mHasShown){
            mWindowManager.removeViewImmediate(mFloatLayout);
        }
        mHasShown = false;
    }

    public static void show() {
        if (!mHasShown){
            mWindowManager.addView(mFloatLayout, wmParams);
        }
        mHasShown = true;
    }

    public static void startRobotSpeakAnim(){
        if(mFloatLayout != null){
            mFloatLayout.startRobotSpeakAnim();
        }
    }

    /**
     * 关闭说话动画
     *
     * @return true:如果动画不为空且在运行则关闭  false:悬浮窗为空|无动画
     */
    public static boolean stopRobotSpeakAnim(){
        if(mFloatLayout != null){
            return mFloatLayout.stopRobotSpeakAnim();
        }else{
            return false;
        }
    }

    /**
     * 打开波形动画
     *
     * @return true:无动画则运行动画  false:悬浮窗为空|有动画则停止动画
     */
    public static boolean startRobotWaveformAnim(){
        if(mFloatLayout != null){
            return mFloatLayout.startRobotWaveformAnim();
        }else{
            return false;
        }
    }

    /**
     * 关闭波形动画
     *
     * @return true:如果动画不为空且在运行则关闭  false:悬浮窗为空|无动画
     */
    public static boolean stopRobotWaveformAnim(){
        if(mFloatLayout != null){
            return mFloatLayout.stopRobotWaveformAnim();
        }else{
            return false;
        }
    }
}
