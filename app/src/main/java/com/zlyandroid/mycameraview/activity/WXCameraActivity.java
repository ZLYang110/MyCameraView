package com.zlyandroid.mycameraview.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zlyandroid.mycameraview.R;
import com.zlyandroid.mycameraview.camera.CameraOrientationListener;
import com.zlyandroid.mycameraview.camera.Capture;
import com.zlyandroid.mycameraview.camera.OnCameraCaptureListener;
import com.zlyandroid.mycameraview.camera.Util;
import com.zlyandroid.mycameraview.widget.AutoFitTextureView;
import com.zlyandroid.mycameraview.widget.CaptureButton;
import com.zlyandroid.mycameraview.widget.CustomRecordImageView;
import com.zlyandroid.mycameraview.widget.HorizontalScrollPickView;

public class WXCameraActivity extends AppCompatActivity implements View.OnClickListener,  OnCameraCaptureListener {

    private Context mContext;
    private int mode =CaptureButton.Mode.MODE_CAPTURE_RECORD; // 拍摄模式
    private long duration = 15000; // 拍摄时长
    //视图
    private AutoFitTextureView surfaceView;

    private View viewBack;
    private CaptureButton captureButton;
    private ImageView viewSplashMode, viewSwitch, viewFocusView;
    private TextView viewTextInfo;
    private View viewNavigation;


    Capture capture;



    private CameraOrientationListener cameraOrientationListener;


    /**
     * 获取启动UserActivity的intent
     *
     * @param activity
     * @return
     */
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, WXCameraActivity.class);
        activity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxcamera);
        cameraOrientationListener = new CameraOrientationListener(this);
        cameraOrientationListener.enable();
        mContext = this;
        initView();
        iniData();

    }

    private void initView() {
        surfaceView = findViewById(R.id.camera_capture_record_surface_view);
        viewBack =  findViewById(R.id.camera_capture_record_btn_back);
        captureButton = findViewById(R.id.camera_capture_record_capture_button);
        viewSplashMode =  findViewById(R.id.camera_capture_record_iv_splash);
        viewSwitch =  findViewById(R.id.camera_capture_record_iv_switch);
        viewFocusView =  findViewById(R.id.camera_capture_record_focus_view);
        viewTextInfo =  findViewById(R.id.camera_capture_record_tv_info);
        viewNavigation =  findViewById(R.id.camera_capture_record_view_navigation);
        viewBack.setOnClickListener(this);
        viewSplashMode.setOnClickListener(this);
        viewSwitch.setOnClickListener(this);
    }

    private void iniData() {
        // ——————————————————————————————————————初始化——————————————————————————————————————————
        capture = new Capture(surfaceView);
        capture.setOnCameraCaptureListener(this);

        captureButton.setMode(mode);
        captureButton.setDuration(duration);
        if (mode == CaptureButton.Mode.MODE_CAPTURE) {
            viewTextInfo.setText("轻触拍照");
        } else if (mode == CaptureButton.Mode.MODE_RECORD) {
            viewTextInfo.setText("长按摄像");
        } else if (mode == CaptureButton.Mode.MODE_CAPTURE_RECORD) {
            viewTextInfo.setText("轻触拍照 长按摄像");
        }
        // 点击拍摄
        captureButton.setOnProgressTouchListener(new CaptureButton.OnProgressTouchListener() {
            @Override
            public void onCapture() {
                capture.capturePhoto(cameraOrientationListener.getOrientation());
            }

            @Override
            public void onCaptureRecordStart() {
                capture.captureRecordStart(cameraOrientationListener.getOrientation());
            }

            @Override
            public void onCaptureRecordEnd() {
                capture.captureRecordEnd();
            }

            @Override
            public void onCaptureError(String message) {
                capture.captureRecordFailed();
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

        });

        // 对焦
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        float X = event.getRawX();
                        float Y = event.getRawY();
                        Util.log("click    X：" + X + "    Y：" + Y);
                        capture.focus(X, Y);
                        break;
                }
                return true;
            }
        });
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.camera_capture_record_btn_back:// 返回
                finish();
                break;
            case R.id.camera_capture_record_iv_splash:// 切换闪光灯
                capture.enableFlashLight();
                break;
            case R.id.camera_capture_record_iv_switch:// 切换摄像头
                capture.switchCamera();
                break;

        }
    }



    @Override
    public void onToggleSplash(String flashMode) {
        if (flashMode == null) {
            // 说明不支持闪光灯
            viewSplashMode.setVisibility(View.GONE);
            return;
        }
        viewSplashMode.setVisibility(View.VISIBLE);
        if (flashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
            viewSplashMode.setImageResource(R.drawable.camera_ic_capture_flash_off_24dp);
        }

        if (flashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
            viewSplashMode.setImageResource(R.drawable.camera_ic_camera_flash_auto_24dp);
        }

        if (flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            viewSplashMode.setImageResource(R.drawable.camera_ic_capture_flash_on_24dp);
        }
    }

    @Override
    public void onFocusSuccess(float x, float y) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewFocusView.getLayoutParams();
        layoutParams.leftMargin = (int) x - Util.dip2px(mContext, 35);
        layoutParams.topMargin = (int) y - Util.dip2px(mContext, 35);
        viewFocusView.setLayoutParams(layoutParams);
        Util.scale(viewFocusView);
    }

    @Override
    public void onCapturePhoto(String photoPath) {
        // 拍照成功
        Toast.makeText(mContext, photoPath, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCaptureRecord(String filePath) {
        // 录像成功
        Toast.makeText(mContext, filePath, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Util.setFullScreen(WXCameraActivity.this);
        if (capture != null) {
            capture.startPreview();
        }
    }

    @Override
    public void onDestroy() {
        cameraOrientationListener.disable();
        cameraOrientationListener = null;
        super.onDestroy();
    }

}
