package com.zlyandroid.mycameraview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zlyandroid.mycameraview.activity.CameraActivity;
import com.zlyandroid.mycameraview.activity.WXCameraActivity;
import com.zlyandroid.mycameraview.activity.AllPictureActivity;
import com.zlyandroid.mycameraview.utils.PermissionUtils;
import com.zlylib.mypermissionlib.RequestListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btn_openCamera;
    private Button btn_openWXCamera;
    private Button btn_previewPicture;//预览照片


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        btn_openCamera=  findViewById(R.id.btn_openCamera);
        btn_openWXCamera =  findViewById(R.id.btn_openWXCamera);
        btn_previewPicture=  findViewById(R.id.btn_previewPicture);
        btn_openCamera.setOnClickListener(this);
        btn_openWXCamera.setOnClickListener(this);
        btn_previewPicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_openCamera:
                /**
                 * 权限申请
                 * */
                PermissionUtils.request(new RequestListener() {
                    @Override
                    public void onSuccess() {
                        CameraActivity.start(MainActivity.this);
                    }
                    @Override
                    public void onFailed() { }
                },MainActivity.this,PermissionUtils.PermissionGroup.PERMISSIONS_STORAGE);
                break;
            case R.id.btn_openWXCamera:
                /**
                 * 权限申请
                 * */
                PermissionUtils.request(new RequestListener() {
                    @Override
                    public void onSuccess() {
                        WXCameraActivity.start(MainActivity.this);
                    }
                    @Override
                    public void onFailed() { }
                },MainActivity.this,PermissionUtils.PermissionGroup.PERMISSIONS_STORAGE);
                break;
            case R.id.btn_previewPicture:
                AllPictureActivity.start(MainActivity.this);
                break;
        }
    }
}
