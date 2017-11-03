package com.guojia.robot.iflytek;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.guojia.robot.R;
import com.guojia.robot.okhttp.MyOkHttp;
import com.guojia.robot.okhttp.RawResponseHandler;
import com.guojia.robot.receiver.MyReceiver;
import com.guojia.robot.utils.ConstUtil;
import com.guojia.robot.utils.DialogUtil;
import com.guojia.robot.utils.LogUtil;
import com.guojia.robot.utils.MethodsUtil;
import com.guojia.robot.utils.ToastUtil;
import com.guojia.robot.utils.WifiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YL on 2017/10/18.
 */

public class DealIat {
    private static LogUtil log = new LogUtil("DealIat", LogUtil.LogLevel.V);

    public static enum ContextType {
        NONE, WIFI_NAME_LIST, WIFI_SPEAK_PWD, WIFI_PWD_LIST
    }
    public static ContextType iatContextType = ContextType.NONE;

    private static List<ScanResult> mScanResultList;
    private static ScanResult scanResult;
    private static String[] listPwdData;
    private static Dialog wifiNamelistDialog;
    private static Dialog wifiPwdlistDialog;


    public static void openWifiNameList(final Context context, String result){
        WifiUtil.get().openWifi();
        mScanResultList = WifiUtil.get().getWifiList();
        if(mScanResultList != null && mScanResultList.size()>0){
            String listData[] = new String[Math.min(5, mScanResultList.size())];
            for(int i = mScanResultList.size()-1; i >= 0 && mScanResultList.size()-1-i <=4 ; i--){
                listData[mScanResultList.size()-1-i] = mScanResultList.get(i).SSID;
                log.i("-------"+mScanResultList.get(i));
            }

            wifiNamelistDialog = DialogUtil.ListDialog(context, listData, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ConnectWifi(context, i+1);
                }
            });

            IflytekUtil.startTts(context.getString(R.string.dialog_wifi_name_list));
            iatContextType = ContextType.WIFI_NAME_LIST;
        }
    }
    public static void ConnectWifi(final Context context, int item){
        if(mScanResultList != null && mScanResultList.size()>0){
            if (item <= mScanResultList.size()){
                scanResult = mScanResultList.get(mScanResultList.size()-item);
                int i = WifiUtil.get().getWifiType(scanResult);
                if(ConstUtil.WIFICIPHER_NOPASS == i){
                    WifiConfiguration WifiCfg = WifiUtil.get().CreateWifiInfo(scanResult.SSID, null, ConstUtil.WIFICIPHER_NOPASS);
                    boolean a = WifiUtil.get().addNetwork(WifiCfg);
                    if(a){
                        IflytekUtil.startTts("正在连接热点！");
                    }else{
                        IflytekUtil.startTts("热点连接失败了！");
                    }
                    iatContextType = ContextType.NONE;
                }else{
                    int networkId = WifiUtil.get().getConfiguration(scanResult.SSID);
                    if (networkId > 0){
                        boolean a = WifiUtil.get().addNetwork(networkId);
                        if(a){
                            IflytekUtil.startTts("正在连接热点哦！");
                        }else{
                            IflytekUtil.startTts("热点连接失败了呀！");
                        }
                        iatContextType = ContextType.NONE;
                    }else{
                        IflytekUtil.startTts(context.getString(R.string.dialog_wifi_pwd_speak));
                        wifiPwdlistDialog = DialogUtil.ListPwdDialog(context, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EditText et_wifipwd = (EditText) wifiPwdlistDialog.getCurrentFocus().findViewById(R.id.et_wifipwd);
                                String pwd = et_wifipwd.getText().toString();
                                if (!TextUtils.isEmpty(pwd)) {
                                    ConnectWifiPwd(context, pwd);
                                } else {
                                    IflytekUtil.startTts("你还没有输入密码呀！");
                                }
                            }
                        }, null, null);
                        iatContextType = ContextType.WIFI_SPEAK_PWD;
                    }
                }

                if(wifiNamelistDialog != null && wifiNamelistDialog.isShowing()){
                    wifiNamelistDialog.dismiss();
                }
            }else{
                IflytekUtil.startTts("没有第"+item+"个呀！");
            }

        }
    }

    public static void openWifiPwdList(final Context context, ArrayList<String> resultList){
        log.i("-------openWifiPwdList resultList： "+resultList);
        ArrayList<String> newResultList = new ArrayList<String>();
        for(int i = 0; i < resultList.size() && i <=4; i++){
            String str = MethodsUtil.string2wifipwd(resultList.get(i));
            if (!TextUtils.isEmpty(str)){
                newResultList.add(str);
            }
        }

        log.i("-------openWifiPwdList resultList： "+newResultList);
        if(newResultList.isEmpty()){
            IflytekUtil.startTts(context.getString(R.string.dialog_wifi_pwd_speak_no));
            return;
        }
        listPwdData = new String[Math.min(5, newResultList.size())];
        for(int i = 0; i < newResultList.size() && i <=4; i++){
            listPwdData[i] = newResultList.get(i);
            log.i("-------openWifiPwdList"+newResultList.get(i));
        }

        wifiPwdlistDialog = DialogUtil.ListPwdDialog(context, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_wifipwd = (EditText) wifiPwdlistDialog.getCurrentFocus().getRootView().findViewById(R.id.et_wifipwd);
                log.i("-------EditText0: "+wifiPwdlistDialog.getCurrentFocus().getRootView());
                log.i("-------EditText1: "+wifiPwdlistDialog.getCurrentFocus());
                log.i("-------EditText2: "+et_wifipwd);
                log.i("-------EditText3: "+et_wifipwd.getText());
                String pwd = et_wifipwd.getText().toString();
                if (!TextUtils.isEmpty(pwd)) {
                    ConnectWifiPwd(context, pwd);
                } else {
                    IflytekUtil.startTts("你还没有输入密码呢！");
                }
            }
        }, listPwdData, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ConnectWifiPwd(context, listPwdData[i]);
            }
        });

        IflytekUtil.startTts(context.getString(R.string.dialog_wifi_pwd_list));
        iatContextType = ContextType.WIFI_PWD_LIST;
    }

    public static void ConnectWifiPwd(Context context, int item){
        if (item <= listPwdData.length){
            ConnectWifiPwd(context, listPwdData[item-1]);
        }else{
            IflytekUtil.startTts("没有第"+item+"个呀！");
        }
    }

    public static void ConnectWifiPwd(Context context, String pwd){
        int i = WifiUtil.get().getWifiType(scanResult);
        WifiConfiguration WifiCfg = WifiUtil.get().CreateWifiInfo(scanResult.SSID, pwd, i);
        boolean a = WifiUtil.get().addNetwork(WifiCfg);
        log.i("ConnectWifiPwd00 type: "+i+" success: "+a);
        if(a){
            IflytekUtil.startTts("正在连接热点！");
        }else{
            if(1 == i){
                i = 2;
            }else{
                i = 1;
            }
            WifiCfg = WifiUtil.get().CreateWifiInfo(scanResult.SSID, pwd, i);
            boolean b = WifiUtil.get().addNetwork(WifiCfg);
            log.i("ConnectWifiPwd01 type: "+i+" success: "+b);
            if(b){
                IflytekUtil.startTts("正在连接热点！");
            }else{
                IflytekUtil.startTts("热点连接失败了呢！");
            }
        }
        MyReceiver.pwdErroeSpeak = true;//语音提醒密码是否错误

        if(wifiPwdlistDialog != null && wifiPwdlistDialog.isShowing()){
            wifiPwdlistDialog.dismiss();
        }
        iatContextType = ContextType.NONE;
    }

    public static void sendRobot(Context context, String result){
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", "gjrobot123456789");
        params.put("version", "1");
        params.put("brand", Build.BRAND);
        params.put("model", Build.MODEL);
        params.put("versionA", Build.VERSION.RELEASE);
        params.put("sim", "无SIM卡");
        params.put("network", "WIFI");
        params.put("info", result);
        params.put("infoBefore", "测试");
        params.put("infoBeforeType", "0");

        MyOkHttp.get().post(context, "http://47.93.229.80:8088/YLing/robot", params, new RawResponseHandler() {

            @Override
            public void onSuccess(int statusCode, String response) {
                log.i(statusCode + " " + response);
                if(response == null || response.isEmpty() || response.length()<5){
                    log.i("get json is null ? count|playm");
                }else{
                    String httpText;

                    JsonObject returnObj = new com.google.gson.JsonParser().parse(response).getAsJsonObject();
                    final int error_code = returnObj.get("error_code").getAsInt();

                    log.i("-------------网络请求错误码：" + error_code);
                    if (0 == error_code) {
                        JsonObject resultData = returnObj.getAsJsonObject("result");
                        int code = resultData.get("code").getAsInt();
                        log.i("resultData: "+resultData);

                        if(resultData.has("text")){
                            httpText = resultData.get("text").getAsString();
                        }else{
                            httpText = "聊点别的，怎么样？";
                        }
                    } else if (10000 == error_code || 30000 == error_code) {
                        httpText = "网络有点问题，要不等会再说！";
                    } else if (10005 == error_code) {
                        httpText = "你说得太多了，下次少说点！";
                    } else if (10010 == error_code) {
                        httpText = "你还是说点别的吧！";
                    } else {
                        httpText = "我头有点晕，你等会再说呗！";
                    }

                    IflytekUtil.startTts(httpText);
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                log.v(statusCode + " " + error_msg);
            }
        });
    }

}
