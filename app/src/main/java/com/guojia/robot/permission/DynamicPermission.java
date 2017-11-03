package com.guojia.robot.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.guojia.robot.application.GJApplication;
import com.guojia.robot.utils.ConstUtil;
import com.guojia.robot.utils.LogUtil;
import com.guojia.robot.utils.ToastUtil;

/**
 * Created by YL on 2017/10/13.
 */

public class DynamicPermission {

    private static LogUtil log = new LogUtil("DynamicPermission", LogUtil.LogLevel.V);

    /** 录音权限 */
    public static boolean audioPermission(){
        return dynamicPermission(GJApplication.getRunActivity(), Manifest.permission.RECORD_AUDIO, ConstUtil.PERMISSIONS_REQUEST_AUDIO);
    }
    /** 读写SD卡权限 */
    public static boolean sdCardPermission(){
        return dynamicPermission(GJApplication.getRunActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, ConstUtil.PERMISSIONS_WRITE_READ_SD);
    }
    /** GPS权限 */
    public static boolean locationPermission(){
        return dynamicPermission(GJApplication.getRunActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, ConstUtil.PERMISSIONS_LOCATION);
    }

    /** 动态权限 */
    public static boolean dynamicPermission(Activity activity, String Permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(activity != null){
                if (ContextCompat.checkSelfPermission(activity, Permission) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Permission)){
                        log.i("dynamicPermission shouldShowRequestPermissionRationale1: 你已禁止该权限，需要重新开启 "+System.currentTimeMillis());
                        ToastUtil.showNoRCToast(activity, "你已禁止该权限，请在设置中开启。", 1);
                    }else{
                        log.i("dynamicPermission requestPermissions1: 选择  "+System.currentTimeMillis());
                        ActivityCompat.requestPermissions(activity, new String[]{Permission}, requestCode);
                    }
                    return false;
                } else {
                    return true;
                }
            }else{
                return false;
            }
        }else{
            return true;
        }
    }
    /** 动态权限 */
    public static boolean dynamicPermission(Activity activity, String[] Permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(activity != null){
                boolean b = false;
                for (String per : Permission) {
                    if (ContextCompat.checkSelfPermission(activity, per)
                            != PackageManager.PERMISSION_GRANTED) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, per)){
                            log.i("dynamicPermission shouldShowRequestPermissionRationale2: 你已禁止该权限，需要重新开启 "+System.currentTimeMillis());
                            ToastUtil.showNoRCToast(activity, "你已禁止该权限，需要重新开启。", 1);
                        }else{
                            log.i("dynamicPermission requestPermissions2: 选择 "+System.currentTimeMillis());
                            ActivityCompat.requestPermissions(activity, Permission, requestCode);
                        }
                        b = false;
                        log.i("dynamicPermission: "+per+": "+b+" "+System.currentTimeMillis());
                        break;
                    } else {
                        b = true;
                        log.i("dynamicPermission: "+per+": "+b+" "+System.currentTimeMillis());
                    }
                }
                log.i("dynamicPermission: for: "+b+" "+System.currentTimeMillis());
                return b;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }
    /** 动态权限 */
    @SuppressLint({ "InlinedApi", "NewApi" })
    public static boolean dynamicPermission(Activity activity, String op, String Permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(activity != null){
                AppOpsManager appOpsManager = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
                int checkOp = appOpsManager.checkOp(op, Binder.getCallingUid(), activity.getPackageName());
                if (ContextCompat.checkSelfPermission(activity, Permission) != PackageManager.PERMISSION_GRANTED ||
                        checkOp == AppOpsManager.MODE_IGNORED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Permission)){
                        log.i("dynamicPermission shouldShowRequestPermissionRationale1: 你已禁止该权限，需要重新开启 "+System.currentTimeMillis());
                        ToastUtil.showNoRCToast(activity, "你已禁止该权限，请在设置中开启。", 1);
                    }else{
                        log.i("dynamicPermission requestPermissions1: 选择  "+System.currentTimeMillis());
                        ActivityCompat.requestPermissions(activity, new String[]{Permission}, requestCode);
                    }
                    return false;
                } else {
                    return true;
                }
            }else{
                return false;
            }
        }else{
            return true;
        }
    }
    /** 动态权限 */
    @SuppressLint({ "InlinedApi", "NewApi" })
    public static boolean dynamicPermission(Activity activity, String[] op, String[] Permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(activity != null){
                boolean a = false;
                for (String per : Permission) {
                    if (ContextCompat.checkSelfPermission(activity, per)
                            != PackageManager.PERMISSION_GRANTED) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, per)){
                            log.i("dynamicPermission shouldShowRequestPermissionRationale2: 你已禁止该权限，需要重新开启 "+System.currentTimeMillis());
                            ToastUtil.showNoRCToast(activity, "你已禁止该权限，需要重新开启。", 1);
                        }else{
                            log.i("dynamicPermission requestPermissions2: 选择 "+System.currentTimeMillis());
                            ActivityCompat.requestPermissions(activity, Permission, requestCode);
                        }
                        a = false;
                        log.i("dynamicPermission: "+per+": "+a+" "+System.currentTimeMillis());
                        break;
                    } else {
                        a = true;
                        log.i("dynamicPermission: "+per+": "+a+" "+System.currentTimeMillis());
                    }
                }
                log.i("dynamicPermission: for Permission: "+a+" "+System.currentTimeMillis());

                boolean b = false;
                if(a){
                    for (String o : op) {
                        AppOpsManager appOpsManager = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
                        int checkOp = appOpsManager.checkOp(o, Binder.getCallingUid(), activity.getPackageName());
                        if (checkOp == AppOpsManager.MODE_IGNORED) {
                            b = false;
                            log.i("dynamicAppOpsManager: "+o+": "+b+" "+System.currentTimeMillis());
                            break;
                        } else {
                            b = true;
                            log.i("dynamicAppOpsManager: "+o+": "+b+" "+System.currentTimeMillis());
                        }
                    }
                    log.i("dynamicAppOpsManager: for op: "+b+" "+System.currentTimeMillis());
                }

                return a && b;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }

}
