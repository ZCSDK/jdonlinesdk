package com.sobot.online.weight.camera.listener;

import android.graphics.Bitmap;

public interface StCameraListener {

    void captureSuccess(Bitmap bitmap);

    void recordSuccess(String url, Bitmap firstFrame);

}
