# MyCameraView

自定义相机

1. 仿照系统相机拍照录像功能。
2. 仿照微信点击拍照，长按录像。
3. 预览图片和视频。仿照微信朋友圈预览，支持左右滑动， 缩放 拖拽下拉缩小退出

[GitHub主页](https://github.com/ZLYang110/MyCameraView)


# 引用开源库

- [视频播放 JiaoZiVideoPlayer](https://github.com/Jzvd/JiaoZiVideoPlayer)
- [图片加载 glide](https://github.com/bumptech/glide)
- [RecyclerView](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
- [eventbus](https://github.com/greenrobot/EventBus)



# 运行截图
<img src="https://github.com/ZLYang110/MyCameraView/raw/master/screenshot/Screenshot_20200601_181036_com.zlyandroid.mycameraview.jpg" width = "180" height = "300" alt="图片名称"/> <img src="https://github.com/ZLYang110/MyCameraView/raw/master/screenshot/Screenshot_20200601_181043_com.zlyandroid.mycameraview.jpg" width = "180" height = "300" alt="图片名称" /> <img src="https://github.com/ZLYang110/MyCameraView/raw/master/screenshot/Screenshot_20200601_181051_com.zlyandroid.mycameraview.jpg" width = "180" height = "300" alt="图片名称"/>

# 相机

 ```java

  // ——————————————————————————————————————初始化——————————————————————————————————————————
   Capture capture = new Capture(surfaceView);
   capture.setOnCameraCaptureListener(this);

   //拍照
   capture.capturePhoto(cameraOrientationListener.getOrientation());

    //对焦
   capture.focus(X, Y);

  //切换闪光灯
  capture.enableFlashLight();

   //切换摄像头
   capture.switchCamera()

    //视频录制
     capture.captureRecordStart(cameraOrientationListener.getOrientation());

 //录制停止
       capture.captureRecordEnd();


 ```

# 预览

```java


// 1.使用方式
 PreviewBuilder.from(AllPictureActivity.this)
                            .setData(mThumbViewInfoList)
                            .setCurrentIndex(position)
                            .setSingleFling(true)
                            .start();

```

# 联系方式

QQ ： 1833309873


