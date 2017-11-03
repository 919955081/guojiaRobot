package com.guojia.robot.okhttp;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.guojia.robot.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by YL on 2017/10/16.
 */

public class MyOkHttp {
    private static LogUtil log = new LogUtil("MyOkHttp", LogUtil.LogLevel.V);

    private OkHttpClient client;
    private static MyOkHttp instance;

    public MyOkHttp() {
        client = new OkHttpClient();
    }

    /**
     * 获取句柄
     * @return
     */
    public static MyOkHttp get() {
        if(instance == null) {
            instance = new MyOkHttp();
        }
        return instance;
    }

    /**
     * post 请求
     * @param url url
     * @param params 参数
     * @param responseHandler 回调
     */
    public void post(final String url, final Map<String, String> params, final IResponseHandler responseHandler) {
        post(null, url, params, responseHandler);
    }

    /**
     * post 请求
     * @param context 发起请求的context
     * @param url url
     * @param params 参数
     * @param responseHandler 回调
     */
    public void post(Context context, final String url, final Map<String, String> params, final IResponseHandler responseHandler) {
        //post builder 参数
        FormBody.Builder builder = new FormBody.Builder();
        if(params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }

        Request request;

        //发起request
        if(context == null) {
            request = new Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .tag(context)
                    .build();
        }


        client.newCall(request).enqueue(new MyCallback(new Handler(), responseHandler));
    }

    /**
     * get 请求
     * @param url url
     * @param params 参数
     * @param responseHandler 回调
     */
    public void get(final String url, final Map<String, String> params, final IResponseHandler responseHandler) {
        get(null, url, params, responseHandler);
    }

    /**
     * get 请求
     * @param context 发起请求的context
     * @param url url
     * @param params 参数
     * @param responseHandler 回调
     */
    public void get(Context context, final String url, final Map<String, String> params, final IResponseHandler responseHandler) {
        //拼接url
        String get_url = url;
        if(params != null && params.size() > 0) {
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if(i++ == 0) {
                    get_url = get_url + "?" + entry.getKey() + "=" + entry.getValue();
                } else {
                    get_url = get_url + "&" + entry.getKey() + "=" + entry.getValue();
                }
            }
        }

        Request request;

        //发起request
        if(context == null) {
            request = new Request.Builder()
                    .url(url)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .tag(context)
                    .build();
        }

        client.newCall(request).enqueue(new MyCallback(new Handler(), responseHandler));
    }

    /**
     * 取消当前context的所有请求
     * @param context
     */
    public void cancel(Context context) {
        if(client != null) {
            for(Call call : client.dispatcher().queuedCalls()) {
                if(call.request().tag().equals(context))
                    call.cancel();
            }
            for(Call call : client.dispatcher().runningCalls()) {
                if(call.request().tag().equals(context))
                    call.cancel();
            }
        }
    }


    //callback
    private class MyCallback implements Callback {

        private Handler mHandler;
        private IResponseHandler mResponseHandler;

        public MyCallback(Handler handler, IResponseHandler responseHandler) {
            mHandler = handler;
            mResponseHandler = responseHandler;
        }

        @Override
        public void onFailure(Call call, final IOException e) {
            log.e("onFailure", e);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mResponseHandler.onFailure(0, e.toString());
                }
            });
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if(response.isSuccessful()) {
                final String response_body = response.body().string();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((RawResponseHandler)mResponseHandler).onSuccess(response.code(), response_body);
                    }
                });
            } else {
                log.e("onResponse fail status=" + response.code());

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mResponseHandler.onFailure(0, "fail status=" + response.code());
                    }
                });
            }
        }
    }

}
