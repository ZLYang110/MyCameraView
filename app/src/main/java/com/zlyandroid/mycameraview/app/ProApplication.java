package com.zlyandroid.mycameraview.app;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import com.zlyandroid.mycameraview.picture.ZoomMediaLoader;

/**
 * 应用, 主要用来做一下初始化的操作
 *
 * @author zhangliyang
 * @since 1.0
 */
public class ProApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ZoomMediaLoader.getInstance().init(new TestImageLoader());
    }


}
