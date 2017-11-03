package com.guojia.robot.iflytek;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.guojia.robot.utils.ToastUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by win7 on 2017/10/24.
 */

public class IsvManager {

    private static final int PWD_TYPE_TEXT = 1;
    private static final int PWD_TYPE_NUM = 2;
    private static boolean hasMode ;         //search库中是否已有ID声纹模型
    public static final String TEXT_PWD_ONE = "芝麻开门";

    // 当前声纹密码类型，1、2、3分别为文本、自由说和数字密码
    private int mPwdType = PWD_TYPE_TEXT;

    // 声纹识别对象
    private SpeakerVerifier mVerifier;

    // 声纹AuthId，用户在云平台的身份标识，也是声纹模型的标识
    // 请使用英文字母或者字母和数字的组合，勿使用中文字符
    private String mAuthId = "";

    // 文本声纹密码
    private String mTextPwd = "";

    // 数字声纹密码段，默认有5段
    private String[] mNumPwdSegs;

    private Context context;

    private static IsvManager mInstance = null;
    private boolean FirstLogin = true;



    private InnerListener innerListener;

    //ui
//    private TextView showHint_tv,showMsg_tv,showPwd_tv;

    public IsvManager(final Context context, String authId) {
        this.context = context;
        this.mAuthId = authId;
//        SpeechUtility.createUtility(context, "appid=" + "59e05af7");
        mVerifier = SpeakerVerifier.createVerifier(context, new InitListener() {
            @Override
            public void onInit(int i) {
                if (ErrorCode.SUCCESS == i) {
                    ToastUtil.show(context,"引擎初始化成功",Toast.LENGTH_SHORT);
                } else {
                    ToastUtil.show(context,"引擎初始化失败，错误码："+i,Toast.LENGTH_SHORT);
                }
            }
        });
    }
    public IsvManager(){

    }

    /**
     * 传递接口实例
     * @param innerListener
     */
    public void setInnerListener(InnerListener innerListener) {
        this.innerListener = innerListener;
    }
    /**
     * 初始化TextView和密码文本
     */
    private void initTextView(){
        mTextPwd = null;
    }
    /**
     * 是否是首次登陆
     * @return
     */
    public boolean checkFristLogin(){
        return FirstLogin;
    }
    /**
     * 检验唯一标识用户ID是否为空
     * @return
     */
    private boolean checkAuthId(){
        if (TextUtils.isEmpty(mAuthId)||mAuthId.equals(""))
            return false;
        return true;
    }

    /**
     * 获取声纹密码
     */
    private void getPwd(){
        if (!checkAuthId())
            return;
        // 获取密码之前先终止之前的注册或验证过程
        mVerifier.cancel();
        mVerifier.setParameter(SpeechConstant.PARAMS, null);                  // 清空参数
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + PWD_TYPE_TEXT);  //设置密码类型
        mVerifier.getPasswordList(mPwdListenter);                           //获取密码列表
    }
    private String[] items;
    private SpeechListener mPwdListenter = new SpeechListener() {
        @Override
        public void onEvent(int i, Bundle bundle) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            String result = new String(bytes);
            try {
                JSONObject object = new JSONObject(result);
                if (!object.has("txt_pwd")) {
                    Log.e(TAG, "onBufferReceived: 没有正确获取到文本密码列表json数据" );
                    return;
                }
                JSONArray pwdArray = object.getJSONArray("txt_pwd");
                items = new String[pwdArray.length()];
                for (int i = 0; i < items.length; i++) {
                    items[i] = pwdArray.getString(i);
                    if (items[i].equals(TEXT_PWD_ONE))
                        mTextPwd = items[i];
                }
                Log.w(TAG, "获取的文本密码 : "+mTextPwd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if (null != speechError && ErrorCode.SUCCESS != speechError.getErrorCode()) {
                Log.e(TAG, "onCompleted: "+"获取失败：" + speechError.getErrorCode());
            }
        }
    };
    public String getIsvPwd(){
        if (TextUtils.isEmpty(mTextPwd)||mTextPwd.equals(""))
            return null;
        return mTextPwd;
    }
    /**
     * 注册声纹
     */
    public void regist(VerifierListener mRegisterListener){
        getPwd();
        if (getIsvPwd()==null){
            return;
        }
        // 清空参数
        mVerifier.setParameter(SpeechConstant.PARAMS, null);
        mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test.pcm");
        // 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
//		mVerify.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);
        if (TextUtils.isEmpty(mTextPwd)) {
            Log.e(TAG, "请获取声纹密码后进行操作" );
            return;
        }
        mVerifier.setParameter(SpeechConstant.ISV_PWD, mTextPwd);
//        ShowPwdTextView.setText("请读出：" + mTextPwd);
//        ShowHintTextView.setText("训练 第" + 1 + "遍，剩余4遍");

        // 设置auth_id，不能设置为空
        mVerifier.setParameter(SpeechConstant.AUTH_ID, mAuthId);
        // 设置业务类型为注册
        mVerifier.setParameter(SpeechConstant.ISV_SST, "train");
        // 设置声纹密码类型
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
        // 开始注册
        mVerifier.startListening(mRegisterListener);
    }

    /**
     * 验证声纹
     */
    public void verify(VerifierListener mVerifyListener){
        getPwd();
        // 清空参数
        mVerifier.setParameter(SpeechConstant.PARAMS, null);
        mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
        mVerifier = SpeakerVerifier.getVerifier();
        // 设置业务类型为验证
        mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");
        // 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
//			mVerify.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);

//        if (mPwdType == PWD_TYPE_TEXT) {
//            // 文本密码注册需要传入密码
//            if (TextUtils.isEmpty(mTextPwd)) {
//                ToastUtil.show(context,"请获取密码后进行操作",Toast.LENGTH_SHORT);
//                return;
//            }
            mVerifier.setParameter(SpeechConstant.ISV_PWD, TEXT_PWD_ONE);
//            showPwd_tv.setText("请读出："
//                    + mTextPwd);
//        } else if (mPwdType == PWD_TYPE_NUM) {
            // 数字密码注册需要传入密码
//            String verifyPwd = mVerifier.generatePassword(8);
//            mVerifier.setParameter(SpeechConstant.ISV_PWD, verifyPwd);
//            showPwd_tv.setText("请读出："
//                    + verifyPwd);

            // 设置auth_id，不能设置为空
            mVerifier.setParameter(SpeechConstant.AUTH_ID, mAuthId);
            mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);

            // 开始验证
            mVerifier.startListening(mVerifyListener);
//        }
    }

    public void search(){
        getPwd();
        performModelOperation("que", mModelOperationListener);
        Log.e(TAG, hasMode+"search当前线程 "+Thread.currentThread().getId());

    }
    /**
     * 执行模型操作
     *
     * @param operation 操作命令
     * @param listener  操作结果回调对象
     */
    private void performModelOperation(String operation, SpeechListener listener) {
        // 清空参数
        mVerifier.setParameter(SpeechConstant.PARAMS, null);
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);

//        if (mPwdType == PWD_TYPE_TEXT) {
//            // 文本密码删除需要传入密码
//            if (TextUtils.isEmpty(mTextPwd)) {
//                Log.i(TAG, "请获取密码后进行操作");
//                return;
//            }
            mVerifier.setParameter(SpeechConstant.ISV_PWD, TEXT_PWD_ONE);
//        } else if (mPwdType == PWD_TYPE_NUM) {
//
//        }

        // 设置auth_id，不能设置为空
        mVerifier.sendRequest(operation, mAuthId, listener);
    }
    public SpeechListener mModelOperationListener = new SpeechListener() {
        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {


            String result = new String(buffer);
            try {
                JSONObject object = new JSONObject(result);
                String cmd = object.getString("cmd");
                int ret = object.getInt("ret");

                if ("del".equals(cmd)) {
                    if (ret == ErrorCode.SUCCESS) {
                        Log.w(TAG, "删除成功 ");
//                        mResultEditText.setText("");
                    } else if (ret == ErrorCode.MSP_ERROR_FAIL) {
                        Log.w(TAG, "删除失败！模型不存在 ");
                    }
                } else if ("que".equals(cmd)) {
                    if (ret == ErrorCode.SUCCESS) {
                        innerListener.hasMode(true);
                        Log.w(TAG, "模型已存在 ");
                    } else if (ret == ErrorCode.MSP_ERROR_FAIL) {
                        innerListener.hasMode(false);
                        Log.w(TAG, "模型不存在 ");
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted(SpeechError error) {
//            setRadioClickable(true);

            if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
                switch (error.getErrorCode()) {
                    case 10116:
                        ToastUtil.show(context,"您还没有注册声纹哦！请先登录注册" ,Toast.LENGTH_SHORT);
                        break;
                    case 10114:
                        ToastUtil.show(context,"网络连接发生异常 请稍后重试" ,Toast.LENGTH_SHORT);
                }

//                ToastUtil.show(context,"操作失败：" + error.getPlainDescription(true),Toast.LENGTH_SHORT);

            }
        }
    };
    /**
     * 检查实例是否存在
     * @return
     */
    public boolean checkInstance(){
        if( null == mVerifier ){
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            ToastUtil.show(context,"创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化" ,Toast.LENGTH_SHORT);
            return false;
        }else{
            return true;
        }
    }

    public void destroy(){
        if (null != mVerifier) {
            mVerifier.stopListening();
            mVerifier.destroy();
        }
    }
    public interface InnerListener{
        void hasMode(boolean mode);
    }

}
