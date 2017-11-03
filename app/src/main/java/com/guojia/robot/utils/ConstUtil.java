package com.guojia.robot.utils;

/**
 * Created by YL on 2017/10/13.
 */

public class ConstUtil {
    /** 无密码连接 */
    public static final int WIFICIPHER_NOPASS = 0;
    /** wpa密码连接 */
    public static final int WIFICIPHER_WPA = 1;
    /** wep密码连接 */
    public static final int WIFICIPHER_WEP = 2;

    /** 读写SD卡权限 */
    public static final int PERMISSIONS_WRITE_READ_SD = 0;
    /** 录音权限 */
    public static final int PERMISSIONS_REQUEST_AUDIO = 1;
    /** 位置权限 */
    public static final int PERMISSIONS_LOCATION = 2;

    /** 读写SD卡权限 */
    public static final String[] ACTIVITY_ROBOT_NAME = new String[]{
            "GuideActivity","LoginActivity","MainActivity"
    };

    /** 密码 */
    public static final String WIFI_SPEAK_PWD0 = "密码";
    /** "连接热点","连接WIFI","连接wifi","连接无线网","连接无线" */
    public static final String[] IAT_RESULT_CONN_WIFI = new String[]{
            "连接热点","连接WIFI","连接wifi","连接无线网","连接无线"
    };

    /**
     * 微博授权app key
     */
    public static final String WEIBO_APP_KEY = "3092010802";
    /**
     * 微博授权REDIRECT_URL
     */
    public static final String WEIBO__REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    /**
     * 微博授权SCOPE
     */
    public static final String WEIBO_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";

    /**
     * wechat appkey
     */
    public static final String WECHAT_APP_KEY = "";

    public static final String WECHAT_SCOPE = "";
    public static final String WECHAT_STATE = "";


    public static final String AUTHER_ID = "account_id";
    public static final String VOICE_STATE = "voice_state";
    public static final String FACE_STATE = "face_state";

}
