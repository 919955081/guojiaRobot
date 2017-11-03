package com.guojia.robot.iflytek;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.guojia.robot.R;
import com.guojia.robot.activity.FaceActivity;
import com.guojia.robot.activity.MainActivity;
import com.guojia.robot.utils.SharedPreManager;
import com.guojia.robot.utils.ToastUtil;
import com.guojia.robot.utils.face.FaceUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.logging.Handler;

/**
 * Created by win7 on 2017/10/26.
 */

public class FaceManager implements FaceActivity.ActivityResultListener{

    public static final int REQUEST_PICTURE_CHOOSE = 1;

    public static final int REQUEST_CAMERA_IMAGE = 2;

    public static final String HAS_MODE_KEY = "hasMode";

    public static final String AUTHER_ID_KEY = "id_key";

    private Activity context;
    //进度对话框
    private ProgressDialog mProDialog;

    // 拍照得到的照片文件
    private File mPictureFile;

    private Bitmap mImage = null;

    //人脸识别唯一标识id
    private String mAuthId = null;

    //人脸识别对象 集成人脸识别各种功能
    private FaceRequest mFaceRequest;

    private byte[] mImageData = null;

    private ImageView mImageView;

    private boolean hasMode = false;




    public FaceManager(Activity context, String Authid, ImageView image){
        this.context = context;
        this.mAuthId = Authid;
        this.mImageView = image;
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 在程序入口处传入appid，初始化SDK
        SpeechUtility.createUtility(context, "appid=" + context.getString(R.string.app_key));
        mProDialog = new ProgressDialog(context);
        mProDialog.setCancelable(true);
        mProDialog.setTitle("请稍后");
        mProDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // cancel进度框时,取消正在进行的操作
                if (null != mFaceRequest) {
                    mFaceRequest.cancel();
                }
            }
        });

        mFaceRequest = new FaceRequest(context);
//        FaceActivity face = new FaceActivity();
//        face.setResultListener(this);
    }

    public FaceActivity.ActivityResultListener getListenerInstance(){
        return this;
    }
    /**
     * 选择人脸注册（验证）图片
     */
    public void pickPic(){

    }
    /**
     * 注册人脸密码
     */
    public void regist(){
        checkAuthID();
        int ret = ErrorCode.SUCCESS;
        if (null != mImageData) {
            mProDialog.setMessage("注册中...");
            mProDialog.show();

            // 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
            // 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
            mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthId);
            mFaceRequest.setParameter(SpeechConstant.WFR_SST,"reg");
            ret = mFaceRequest.sendRequest(mImageData,mRequestListener);
        }else {
            ToastUtil.show(context,"选择图片后注册",Toast.LENGTH_SHORT);
        }
        printfErrorCode(ret);
    }
    /**
     * 拍照
     */
    public void camera(){
        mPictureFile = new File(Environment.getExternalStorageDirectory(),
                "picture" + System.currentTimeMillis()/1000 + ".jpg");
        // 启动拍照,并保存到临时文件
        Intent mIntent = new Intent();
        mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPictureFile));
        mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        context.startActivityForResult(mIntent, REQUEST_CAMERA_IMAGE);
    }
    /**
     * 验证
     */
    public void verfify(){
        checkAuthID();
        int ret = ErrorCode.SUCCESS;
        if (null != mImageData) {
            mProDialog.setMessage("验证中...");
            mProDialog.show();
            // 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
            // 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
            mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthId);
            mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
            ret = mFaceRequest.sendRequest(mImageData, mRequestListener);
        } else {
            ToastUtil.show(context,"请选择图片后再验证",Toast.LENGTH_SHORT);
        }
        printfErrorCode(ret);
    }
    private void printfErrorCode(int ret){
        if (ret!= ErrorCode.SUCCESS) {
            mProDialog.dismiss();
            ToastUtil.show(context,"出现错误"+ret,Toast.LENGTH_SHORT);
        }
    }
    private RequestListener mRequestListener = new RequestListener() {
        @Override
        public void onEvent(int i, Bundle bundle) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            if (null != mProDialog) {
                mProDialog.dismiss();
            }
            try {
                String result = new String(bytes, "utf-8");
                Log.d("FaceDemo", result);

                JSONObject object = new JSONObject(result);
                String type = object.optString("sst");

                if ("reg".equals(type)) {
                    register(object);
                } else if ("verify".equals(type)) {
                    verify(object);
                } else if ("detect".equals(type)) {
                    detect(object);
                } else if ("align".equals(type)) {
                    align(object);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if (null != mProDialog) {
                mProDialog.dismiss();
            }
            if (speechError != null) {
                switch (speechError.getErrorCode()) {
                    case ErrorCode.MSP_ERROR_ALREADY_EXIST:
//                        ToastUtil.show(context,"authid已经被注册，请更换后再试",Toast.LENGTH_SHORT);
//                        hasMode = true;
                        ToastUtil.show(context,"您已经注册过了哦",Toast.LENGTH_SHORT);
                        break;
                    case ErrorCode.MSP_ERROR_NOT_FOUND:
                        ToastUtil.show(context,"您还没有注册人脸哦，请等先登录注册",Toast.LENGTH_SHORT);
                        break;
                    default:
                        ToastUtil.show(context,""+speechError.getPlainDescription(true),Toast.LENGTH_SHORT);
                        break;
                }
            }
        }
    };

    /**
     * 聚焦
     * @param obj
     */
    private void align(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret != 0) {
            ToastUtil.show(context,"聚焦失败",Toast.LENGTH_SHORT);
            return;
        }
        if ("success".equals(obj.get("rst"))) {
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(Math.max(mImage.getWidth(), mImage.getHeight()) / 100f);

            Bitmap bitmap = Bitmap.createBitmap(mImage.getWidth(),
                    mImage.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(mImage, new Matrix(), null);

            JSONArray faceArray = obj.getJSONArray("result");
            for (int i = 0; i < faceArray.length(); i++) {
                JSONObject landmark = faceArray.getJSONObject(i).getJSONObject(
                        "landmark");
                Iterator it = landmark.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    JSONObject postion = landmark.getJSONObject(key);
                    canvas.drawPoint((float) postion.getDouble("x"),
                            (float) postion.getDouble("y"), paint);
                }
            }

            mImage = bitmap;
            mImageView.setImageBitmap(mImage);
        } else {
            ToastUtil.show(context,"聚焦失败",Toast.LENGTH_SHORT);
        }
    }

    /**
     * 检测
     * @param obj
     */
    private void detect(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret != 0) {
            ToastUtil.show(context,"检测失败",Toast.LENGTH_SHORT);
            return;
        }

        if ("success".equals(obj.get("rst"))) {
            JSONArray faceArray = obj.getJSONArray("face");

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(Math.max(mImage.getWidth(), mImage.getHeight()) / 100f);

            Bitmap bitmap = Bitmap.createBitmap(mImage.getWidth(),
                    mImage.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(mImage, new Matrix(), null);
            for (int i = 0; i < faceArray.length(); i++) {
                float x1 = (float) faceArray.getJSONObject(i)
                        .getJSONObject("position").getDouble("left");
                float y1 = (float) faceArray.getJSONObject(i)
                        .getJSONObject("position").getDouble("top");
                float x2 = (float) faceArray.getJSONObject(i)
                        .getJSONObject("position").getDouble("right");
                float y2 = (float) faceArray.getJSONObject(i)
                        .getJSONObject("position").getDouble("bottom");
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(new Rect((int)x1, (int)y1, (int)x2, (int)y2),
                        paint);
            }

            mImage = bitmap;
//            mImageView.setImageBitmap(mImage);
        } else {
            ToastUtil.show(context,"检测失败",Toast.LENGTH_SHORT);
        }
    }

    /**
     * 验证
     * @param object
     */
    private void verify(JSONObject object) throws JSONException {
        int ret = object.getInt("ret");
        if (ret != 0) {
            ToastUtil.show(context,"验证失败",Toast.LENGTH_SHORT);
            return;
        }
        if ("success".equals(object.get("rst"))) {
            if (object.getBoolean("verf")) {
                ToastUtil.show(context,"通过验证！欢迎回来",Toast.LENGTH_SHORT);
                context.findViewById(R.id.face_ver_yes_layout).setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else {
                ToastUtil.show(context,"验证不通过",Toast.LENGTH_SHORT);
            }
        } else {
            ToastUtil.show(context,"验证失败",Toast.LENGTH_SHORT);
        }
    }

    /**
     * 注册
     * @param object
     */
    private void register(JSONObject object) throws JSONException {
        int ret = object.getInt("ret");
        if (ret != 0) {
            ToastUtil.show(context,"注册失败",Toast.LENGTH_SHORT);
            return;
        }
        if ("success".equals(object.get("rst"))) {
            //保存唯一ID  再次登录判断是否为同一ID
            hasMode = true;
            SharedPreManager.getInstance(context).put(AUTHER_ID_KEY,mAuthId);
            SharedPreManager.getInstance(context).put(HAS_MODE_KEY,hasMode);
            ToastUtil.show(context,"注册成功",Toast.LENGTH_SHORT);
            context.finish();
        } else {
            ToastUtil.show(context,"注册失败",Toast.LENGTH_SHORT);
        }
    }

    private void checkAuthID() {
        if (TextUtils.isEmpty(mAuthId)||mAuthId.equals("")){
            ToastUtil.show(context,"不正确的AuthId数据", Toast.LENGTH_SHORT);
            return;
        }
    }

    @Override
    public void handleActivityCode(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        String fileSrc = null;
        if (requestCode == REQUEST_PICTURE_CHOOSE) {
            if ("file".equals(data.getData().getScheme())) {
                // 有些低版本机型返回的Uri模式为file
                fileSrc = data.getData().getPath();
            } else {
                // Uri模型为content
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(data.getData(), proj,
                        null, null, null);
                cursor.moveToFirst();
                int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                fileSrc = cursor.getString(idx);
                cursor.close();
            }
            // 跳转到图片裁剪页面
            FaceUtil.cropPicture(context, Uri.fromFile(new File(fileSrc)));


        } else if (requestCode == REQUEST_CAMERA_IMAGE) {
            if (null == mPictureFile) {
                ToastUtil.show(context,"拍照失败，请重试",Toast.LENGTH_SHORT);
                return;
            }

            fileSrc = mPictureFile.getAbsolutePath();
            updateGallery(fileSrc);
            // 跳转到图片裁剪页面
            FaceUtil.cropPicture((Activity) context,Uri.fromFile(new File(fileSrc)));
        } else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {
            // 获取返回数据
            Bitmap bmp = data.getParcelableExtra("data");
            // 若返回数据不为null，保存至本地，防止裁剪时未能正常保存
            if(null != bmp){
                FaceUtil.saveBitmapToFile(context, bmp);
            }
            // 获取图片保存路径
            fileSrc = FaceUtil.getImagePath(context);
            // 获取图片的宽和高
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            mImage = BitmapFactory.decodeFile(fileSrc, options);

            // 压缩图片
            options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
                    (double) options.outWidth / 1024f,
                    (double) options.outHeight / 1024f)));
            options.inJustDecodeBounds = false;
            mImage = BitmapFactory.decodeFile(fileSrc, options);


            // 若mImageBitmap为空则图片信息不能正常获取
            if(null == mImage) {
                ToastUtil.show(context,"图片信息无法正常获取！",Toast.LENGTH_SHORT);
                return;
            }

            // 部分手机会对图片做旋转，这里检测旋转角度
            int degree = FaceUtil.readPictureDegree(fileSrc);
            if (degree != 0) {
                // 把图片旋转为正的方向
                mImage = FaceUtil.rotateImage(degree, mImage);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //可根据流量及网络状况对图片进行压缩
            mImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            mImageData = baos.toByteArray();

            mImageView.setImageBitmap(mImage);
        }
    }
    private void updateGallery(String filename) {
        MediaScannerConnection.scanFile(context, new String[] {filename}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }

}
