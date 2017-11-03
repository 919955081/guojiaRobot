package com.guojia.robot.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.guojia.robot.application.GJApplication;
import com.guojia.robot.permission.DynamicPermission;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by YL on 2017/10/17.
 */

public class WifiUtil {
    private static LogUtil log = new LogUtil("WifiUtil", LogUtil.LogLevel.V);

    private static WifiUtil mWifiUtil;
    private static WifiManager mWifiManager;

    @SuppressLint("WifiManagerLeak")
    public static WifiUtil get(){
        DynamicPermission.locationPermission();
        if (mWifiUtil == null){
            mWifiUtil = new WifiUtil();

            mWifiManager = (WifiManager) GJApplication.getRunActivity().getSystemService(GJApplication.getRunActivity().WIFI_SERVICE);
        }
        return mWifiUtil;
    }

    /** 打开WIFI */
    public void openWifi(){
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /** 得到配置好的网络 */
    public int getConfiguration(String SSID) {
        List<WifiConfiguration> wifiConfigurationList = mWifiManager.getConfiguredNetworks();
        for (int i = 0; i < wifiConfigurationList.size(); i++){
            String wificeg = wifiConfigurationList.get(i).SSID;
            log.i("--------"+wificeg);
            if(wificeg.contains(SSID)){
                return wifiConfigurationList.get(i).networkId;
            }
        }
        return -1;
    }

    /** 获得WIFI列表 */
    public List<ScanResult> getWifiList(){
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.startScan();//扫描
            List<ScanResult> mScanResultList = mWifiManager.getScanResults();

            //根据SSID去重复
            HashMap<String, ScanResult> map = new HashMap<>();
            for (ScanResult scanResult : mScanResultList) {
                map.put(scanResult.SSID, scanResult);
            }
            mScanResultList.clear();
            mScanResultList.addAll(map.values());

            //根据level排序
            TreeMap<Integer, ScanResult> map1 = new TreeMap<>();
            for (ScanResult scanResult1 : mScanResultList) {
                map1.put(scanResult1.level, scanResult1);
            }
            mScanResultList.clear();
            mScanResultList.addAll(map1.values());

            return mScanResultList;
        }else{
            return null;
        }
    }

    /** 添加一个网络并连接 */
    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        mWifiManager.saveConfiguration();
        return b;
    }
    /** 添加一个网络并连接 */
    public boolean addNetwork(int networkId) {
        boolean b = mWifiManager.enableNetwork(networkId, true);
        return b;
    }

    /** 获取连接配置 */
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if(Type == ConstUtil.WIFICIPHER_NOPASS) {
            config.wepKeys [0] ="\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == ConstUtil.WIFICIPHER_WPA) {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        if(Type == ConstUtil.WIFICIPHER_WEP) {
            config.wepKeys[0]= "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        return config;
    }

    /**
     * 利用WifiConfiguration.KeyMgmt的管理机制，来判断当前wifi是否需要连接密码
     * @return
     */
    public int getWifiType(ScanResult scanResult) {
        if (scanResult.capabilities.contains("WPA") || scanResult.capabilities.contains("wpa")) {
            return ConstUtil.WIFICIPHER_WPA;
        } else if (scanResult.capabilities.contains("WEP") || scanResult.capabilities.contains("wep")) {
            return ConstUtil.WIFICIPHER_WEP;
        }else{
            return ConstUtil.WIFICIPHER_NOPASS;
        }
    }

    public static boolean isWifiConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected()){
            return true ;
        }
        return false ;
    }
}
