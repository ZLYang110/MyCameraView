package com.zlyandroid.mycameraview.camera;

/**
 * Created by zhangliyang on 2019/10/14.
 * gethub https://github.com/ZLYang110
 * Email 1833309873@qq.com
 * Description:
 */
public interface OnCameraCaptureListener {

    void onToggleSplash(String flashMode);

    void onFocusSuccess(float x, float y);

    void onCapturePhoto(String photoPath);

    void onCaptureRecord(String filePath);

    void onError(Throwable throwable);
}
