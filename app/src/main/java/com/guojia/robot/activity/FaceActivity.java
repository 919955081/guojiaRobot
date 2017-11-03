package com.guojia.robot.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.guojia.robot.R;
import com.guojia.robot.iflytek.FaceManager;
import com.guojia.robot.utils.ToastUtil;

import java.io.File;

import static com.guojia.robot.utils.ConstUtil.FACE_STATE;

/**
 * Created by win7 on 2017/10/26.
 */

public class FaceActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {
    private ImageView mImage_iv,mImage_back;
    private String mAutherID;
    private FaceManager mFaceManager;
    private boolean fucFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_ver_layout);
        initView();
        handleData();
        mFaceManager.camera();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void handleData() {
        Intent intent = getIntent();
        mAutherID = intent.getStringExtra("accountID");
        fucFlag = intent.getBooleanExtra(FACE_STATE,false);
        mFaceManager = new FaceManager(this,mAutherID,mImage_iv);
        resultListener = mFaceManager.getListenerInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        mImage_back = findViewById(R.id.back);
        mImage_iv = findViewById(R.id.online_img);


        mImage_back.setOnClickListener(this);
//        verify_btn.setOnClickListener(this);
//        regist_btn.setOnClickListener(this);
//        camera_btn.setOnClickListener(this);
//        pick_btn.setOnClickListener(this);
        mImage_iv.setOnClickListener(this);
        mImage_iv.setOnLongClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.online_img:
                if (!fucFlag)   //为验证状态
                mFaceManager.verfify();
                else {
                    //注册状态
                    mFaceManager.regist();
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        resultListener.handleActivityCode(requestCode,resultCode,data);
    }

    private  ActivityResultListener resultListener;

    @Override
    public boolean onLongClick(View v) {
//        mFaceManager.camera();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, FaceManager.REQUEST_PICTURE_CHOOSE);
        return false;
    }


    public interface ActivityResultListener{
       void handleActivityCode(int requestCode,int resultCode,Intent data);
    }
}
