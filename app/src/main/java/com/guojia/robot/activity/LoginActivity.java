package com.guojia.robot.activity;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guojia.robot.R;
import com.guojia.robot.camerainfo.TakePhoteActivity;
import com.guojia.robot.floatwindow.FloatWindowManager;
import com.guojia.robot.iflytek.FaceManager;
import com.guojia.robot.iflytek.IsvManager;
import com.guojia.robot.permission.FloatPermission;
import com.guojia.robot.utils.ConstUtil;
import com.guojia.robot.utils.DialogUtil;
import com.guojia.robot.utils.SharedPreManager;
import com.guojia.robot.utils.ToastUtil;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;

import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class LoginActivity extends BaseActivity implements View.OnClickListener,IsvManager.InnerListener{

    private TextView tv_forget_pwd, tv_regist, tv_noidlogin;
    private Button bt_login;

    private ImageView img_Isv,img_face,img_qq,img_weibo,img_wechat;
    private EditText et_account;
    private String accountID;

    private IsvManager isvManager;
    private FaceManager faceManager;

    private Tencent mTencent;

    private boolean firstIn = false;
    //wechat
    /**第三方与wechat通信接口*/
    private IWXAPI mWXAPI;

    //微博接入相关
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;

    private AuthInfo mAuthInfo;

    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initData();
        findViewById();
    }

    private void initData() {
        mAuthInfo = new AuthInfo(LoginActivity.this, ConstUtil.WEIBO_APP_KEY,ConstUtil.WEIBO__REDIRECT_URL,ConstUtil.WEIBO_SCOPE);
        mSsoHandler = new SsoHandler(LoginActivity.this);
        //wechat
        mWXAPI = WXAPIFactory.createWXAPI(getApplicationContext(), ConstUtil.WECHAT_APP_KEY, true);
        mWXAPI.registerApp(ConstUtil.WECHAT_APP_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void findViewById() {

        tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);
        tv_regist = (TextView) findViewById(R.id.tv_regist);
        tv_noidlogin = (TextView) findViewById(R.id.tv_noidlogin);
        bt_login = (Button) findViewById(R.id.bt_login);

        et_account = (EditText) findViewById(R.id.et_id);
        img_Isv =(ImageView)findViewById(R.id.iv_voice);
        img_face =(ImageView)findViewById(R.id.iv_face);
        img_qq = findViewById(R.id.iv_qq);
        img_wechat = findViewById(R.id.iv_wechat);
        img_weibo = findViewById(R.id.iv_weibo);

        img_qq.setOnClickListener(this);
        img_weibo.setOnClickListener(this);
        img_wechat.setOnClickListener(this);
        img_Isv.setOnClickListener(this);
        img_face.setOnClickListener(this);
        tv_forget_pwd.setOnClickListener(this);
        tv_regist.setOnClickListener(this);
        tv_noidlogin.setOnClickListener(this);
        bt_login.setOnClickListener(this);

        et_account.setText("wkl9199");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_forget_pwd:
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                intent.putExtra("type", RegistActivity.FORGETACTIVITY);
                startActivity(intent);
            break;

            case R.id.tv_regist:
                Intent intent1 = new Intent(LoginActivity.this, RegistActivity.class);
                intent1.putExtra("type", RegistActivity.REGISTACTIVITY);
                startActivity(intent1);
            break;

            case R.id.tv_noidlogin:
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            break;

            case R.id.bt_login:
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            break;
            case R.id.iv_voice:
                accountID = et_account.getText().toString();
                isvManager = new IsvManager(getApplicationContext(),accountID);
                if (TextUtils.isEmpty(accountID)) {
                    ToastUtil.show(this,"用户不能为空", Toast.LENGTH_SHORT);
                    return;
                }
                if (!isvManager.checkInstance())
                    return;
                isvManager.setInnerListener(this);
                //判断声纹库中是否有ID模板  没有则不跳转
                isvManager.search();

                break;
            case R.id.iv_face:
                accountID = et_account.getText().toString();
                if (TextUtils.isEmpty(accountID)) {
                    ToastUtil.show(this,"用户不能为空", Toast.LENGTH_SHORT);
                    return;
                }
//                if (!SharedPreManager.getInstance(this).getString(FaceManager.AUTHER_ID_KEY).equals(accountID)
//                        &&!SharedPreManager.getInstance(this).getBoolean(FaceManager.HAS_MODE_KEY)&&firstIn)
//                {
//                    ToastUtil.show(this,"你还没有注册人脸识别哦！请登录后注册识别密码",Toast.LENGTH_SHORT);
//                    firstIn = true;
//                    return;
//                }
                Intent faceIntent = new Intent(this,TakePhoteActivity.class);
                faceIntent.putExtra("accountID",accountID);
                startActivity(faceIntent);
//              ToastUtil.show(this,"人脸识别", Toast.LENGTH_SHORT);
                break;
            case R.id.iv_qq:
                loginQQ();
                break;
            case R.id.iv_weibo:
                loginWeibo();
                break;
            case R.id.iv_wechat:
                loginWeChat();
                break;
        }
    }

    /**
     * Wechat login
     */
    private void loginWeChat() {
        final SendAuth.Req req =new SendAuth.Req();
        req.scope = ConstUtil.WECHAT_SCOPE;
        req.state = ConstUtil.WECHAT_STATE;
        mWXAPI.sendReq(req);
    }

    /**
     * 微博登录
     */
    private void loginWeibo() {
        mSsoHandler.authorize(new WbAuthListener() {
            @Override
            public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 保存 Token 到 SharedPreferences
                        AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
                        Toast.makeText(LoginActivity.this,
                                R.string.weibosdk_toast_auth_success, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void cancel() {
                Toast.makeText(LoginActivity.this,
                        R.string.weibosdk_toast_auth_canceled, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                Toast.makeText(LoginActivity.this, wbConnectErrorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 第三方QQ登录
     */
    private void loginQQ() {
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
        mTencent = Tencent.createInstance(getString(R.string.tencent_app_id), this.getApplicationContext());
        mTencent.login(this,"get_user_info,add_t", baseUiListener);
//        startActivity(new Intent(AuthActivity.this,MainActivity.class));

    }
   private IUiListener baseUiListener = new IUiListener() {
        @Override
        public void onComplete(Object response) {
            String openidString;
            Log.e(TAG, "-------------"+response.toString());
            try {
                openidString = ((JSONObject) response).getString("openid");
                int ret = ((JSONObject) response).getInt("ret");
                if (ret == 0){
                    ToastUtil.show(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT);

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("login_status",true);
                    startActivity(intent);
                }else if (ret == 100030){
                    //缺少权限 需要增量授权
                    ToastUtil.show(LoginActivity.this,"需要增量授权",Toast.LENGTH_SHORT);
                }
                Log.e(TAG, "-------------"+openidString);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            QQToken qqtoken = mTencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(),qqtoken);
        }

        @Override
        public void onError(UiError uiError) {
           ToastUtil.show(LoginActivity.this,uiError.errorCode+"\t"+uiError.errorMessage+"\t"+uiError.errorDetail,Toast.LENGTH_SHORT);
       }

        @Override
        public void onCancel() {

        }
    };
    @Override
    public void hasMode(boolean mode) {
        if (!mode){
            return;
        }
        Intent voiceIntent = new Intent(this,VoiceActivity.class);
        voiceIntent.putExtra("accountID",accountID);
        startActivity(voiceIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data,baseUiListener);
        mTencent.handleResultData(data, baseUiListener);
        if(requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.REQUEST_LOGIN) {
//                mTencent.handleLoginData(data,baseUiListener);
                ToastUtil.show(LoginActivity.this,"REQUEST_LOGIN",Toast.LENGTH_SHORT);

            }
        }

    }
}
