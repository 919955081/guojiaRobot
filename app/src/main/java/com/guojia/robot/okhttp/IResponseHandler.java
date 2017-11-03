package com.guojia.robot.okhttp;

/**
 * Created by tsy on 16/8/15.
 */
public interface IResponseHandler {

    void onFailure(int statusCode, String error_msg);

    void onProgress(long currentBytes, long totalBytes);
}
