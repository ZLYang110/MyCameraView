package com.zlyandroid.mycameraview.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.zlyandroid.mycameraview.R;
import com.zlyandroid.mycameraview.camera.CameraOrientationListener;
import com.zlyandroid.mycameraview.camera.Capture;
import com.zlyandroid.mycameraview.camera.OnCameraCaptureListener;
import com.zlyandroid.mycameraview.camera.Util;
import com.zlyandroid.mycameraview.picture.PreviewBuilder;
import com.zlyandroid.mycameraview.picture.IThumbViewInfo;
import com.zlyandroid.mycameraview.utils.FileUtils;
import com.zlyandroid.mycameraview.widget.AutoFitTextureView;
import com.zlyandroid.mycameraview.widget.CustomRecordImageView;
import com.zlyandroid.mycameraview.widget.HorizontalScrollPickView;
import com.zlylib.titlebarlib.widget.ActionBarEx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, HorizontalScrollPickView.SelectListener, OnCameraCaptureListener {

    private Context mContext;

    //视图
    private ActionBarEx action_bar;
    private ImageView viewBack;
    private ImageView viewSplashMode, viewSwitch;
    private AutoFitTextureView surfaceView;
    private ImageView  index_album;
    private HorizontalScrollPickView ll_cameraTypeView;//选择器
    private ImageView img_camera;//拍照
    private CustomRecordImageView img_record;//录像


    Capture capture;

    private ArrayList<IThumbViewInfo> mThumbViewInfoList = new ArrayList<>();
    private boolean isRecording = false;//是否正在录制
    private int cameraType=0;//0 拍照 1 录制

    private CameraOrientationListener cameraOrientationListener;
    //震动器
    private Vibrator vibrator;

    /**
     * 获取启动UserActivity的intent
     *
     * @param activity
     * @return
     */
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, CameraActivity.class);
        activity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        cameraOrientationListener = new CameraOrientationListener(this);
        cameraOrientationListener.enable();
        mContext = this;
        initView();
        iniData();

    }

    private void initView() {
        action_bar = findViewById(R.id.action_bar);
        viewBack= findViewById(R.id.iv_back);
        viewSplashMode =  findViewById(R.id.camera_capture_record_iv_splash);
        viewSwitch =  findViewById(R.id.camera_capture_record_iv_switch);
        surfaceView = findViewById(R.id.camera_capture_record_surface_view);
        ll_cameraTypeView = findViewById(R.id.ll_cameraTypeView);
        index_album = findViewById(R.id.index_album);
        img_camera = findViewById(R.id.img_camera);
        img_record = findViewById(R.id.img_record);
        ll_cameraTypeView = findViewById(R.id.ll_cameraTypeView);
        ll_cameraTypeView.setSelectListener(this);
        ll_cameraTypeView.setDefaultSelectedIndex(1);
        img_camera.setOnClickListener(this);
        img_record.setOnClickListener(this);
        viewBack.setOnClickListener(this);
        viewSplashMode.setOnClickListener(this);
        viewSwitch.setOnClickListener(this);
        index_album.setOnClickListener(this);

    }

    private void iniData() {
        // ——————————————————————————————————————初始化——————————————————————————————————————————
        capture = new Capture(surfaceView);
        capture.setOnCameraCaptureListener(this);

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
    public void onSelect(int beforePosition, int position) {
        switch (position) {

            case 0:
                cameraType = 1;
                img_camera.setVisibility(View.GONE);
                img_record.setVisibility(View.VISIBLE);
                setVibrator();
                break;
            case 1:
                cameraType = 0;
                img_camera.setVisibility(View.VISIBLE);
                img_record.setVisibility(View.GONE);
                setVibrator();
                break;
        }
    }

    /**
     * 震动一下
     */
    private void setVibrator() {
        //震动一下
        vibrator.vibrate(50);
    }

    private long lastActionDownTime = 0; // 记录点击录制视频时的时间
    private long lastActionUpTime = 0;  // 记录最后一次手指抬起的时间

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                 finish();
                break;
            case R.id.camera_capture_record_iv_splash:// 切换闪光灯
                capture.enableFlashLight();
                break;
            case R.id.camera_capture_record_iv_switch:// 切换摄像头
                capture.switchCamera();
                break;
            case R.id.img_camera:
                long now = System.currentTimeMillis();
                if (Math.abs(now - lastActionDownTime) > 1000) {
                    capture.capturePhoto(cameraOrientationListener.getOrientation());
                } else {
                    Toast.makeText(mContext, "您的操作太快了", Toast.LENGTH_SHORT).show();
                }
                lastActionDownTime = now;
                break;

            case R.id.img_record:
                if (!isRecording) {
                    startVideoRecord();
                } else {
                    stopVideoRecord();
                }

                break;
            case R.id.index_album:
                mThumbViewInfoList = new ArrayList<>();
                if(cameraType == 0){
                    List<String> urls = FileUtils.getFilesAllName(Util.photographPath);

                    if(urls.size()>0) {
                        for (int i = 0; i < urls.size(); i++) {
                            mThumbViewInfoList.add(new IThumbViewInfo(urls.get(i)));
                        }
                    }
                }else{
                    List<String> urls = FileUtils.getFilesAllName(Util.videotapePath);
                    if(urls.size()>0) {
                        for (int i = 0; i < urls.size(); i++) {
                            mThumbViewInfoList.add(new IThumbViewInfo(urls.get(i),urls.get(i)));
                        }
                    }
                }
                Rect bounds = new Rect();
                index_album.getGlobalVisibleRect(bounds);
                mThumbViewInfoList.get(mThumbViewInfoList.size()-1).setBounds(bounds);
                PreviewBuilder.from(CameraActivity.this)
                        .setData(mThumbViewInfoList)
                        .setCurrentIndex(mThumbViewInfoList.size()-1)
                        .setSingleFling(true)
                        .start();
                break;

        }
    }

    //=============================视频录制===============================
    private void startVideoRecord() {
        isRecording = true;
        img_record.startRecord();
        ll_cameraTypeView.setVisibility(View.GONE);
        capture.captureRecordStart(cameraOrientationListener.getOrientation());

    }

    private void stopVideoRecord() {

        isRecording = false;
        img_record.stopRecord();
        ll_cameraTypeView.setVisibility(View.VISIBLE);
        capture.captureRecordEnd();
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
       
    }

    @Override
    public void onCapturePhoto(final String photoPath) {
        // 拍照成功
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isDestroyed()) {
                    Glide.with(CameraActivity.this).load(new File(photoPath)).into(index_album);
                }
            }
        });
        Toast.makeText(mContext, photoPath, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCaptureRecord(final String filePath) {
        // 录像成功
        Toast.makeText(mContext, filePath, Toast.LENGTH_SHORT).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isDestroyed()) {
                    Glide.with(CameraActivity.this).load(new File(filePath)).into(index_album);
                }
            }
        });
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Util.setFullScreen(CameraActivity.this);
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
