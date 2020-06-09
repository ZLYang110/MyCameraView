package com.zlyandroid.mycameraview.picture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.zlyandroid.mycameraview.picture.view.BasePhotoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangc on 2017/9/12.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public final class PreviewBuilder {
    private Activity mContext;
    private Intent intent;
    private Class className;

    private PreviewBuilder(@NonNull Activity activity) {
        mContext = activity;
        intent = new Intent();
    }

    /***
     * 设置开始启动预览
     * @param activity  启动
     *@return GPreviewBuilder
     * **/
    public static PreviewBuilder from(@NonNull Activity activity) {
        return new PreviewBuilder(activity);
    }

    /***
     * 设置开始启动预览
     * @param fragment  启动
     *@return GPreviewBuilder
     * **/
    public static PreviewBuilder from(@NonNull Fragment fragment) {
        return new PreviewBuilder(fragment.getActivity());
    }

    /****
     *自定义预览activity 类名
     * @param className   继承GPreviewActivity
     *@return GPreviewBuilder
     * **/
    public PreviewBuilder to(@NonNull Class className) {
        this.className = className;
        intent.setClass(mContext, className);
        return this;
    }
    /****
     *自定义预览activity 类名  方便自定义数据。
     * @param className 继承GPreviewActivity
     * @param bundle 需携带的参数
     *@return GPreviewBuilder
     * **/
    public PreviewBuilder to(@NonNull Class className, @NonNull Bundle bundle) {
        this.className = className;
        intent.setClass(mContext, className);
        intent.putExtras(bundle);
        return this;
    }
    /***
     * 设置数据源
     * @param imgUrls 数据
     *@param   <T>    你的实体类类型
     * @return GPreviewBuilder
     * **/
    public <T extends IThumbViewInfo> PreviewBuilder setData(@NonNull List<T> imgUrls) {
        intent.putParcelableArrayListExtra("imagePaths", new ArrayList<Parcelable>(imgUrls));
        return this;
    }

    /***
     * 设置单个数据源
     * @param imgUrl 数据
     *@param   <E>    你的实体类类型
     * @return GPreviewBuilder
     * **/
    public <E extends IThumbViewInfo> PreviewBuilder setSingleData(@NonNull E imgUrl) {
        ArrayList arrayList = new ArrayList<Parcelable>();
        arrayList.add(imgUrl);
        intent.putParcelableArrayListExtra("imagePaths", arrayList);
        return this;
    }

    /***
     * 设置数据源
     * @param className 你的Fragment类
     * @return GPreviewBuilder
     * **/
    public PreviewBuilder setUserFragment(@NonNull Class<? extends BasePhotoFragment> className) {
        intent.putExtra("className", className);
        return this;
    }

    /***
     * 设置默认索引
     * @param currentIndex 数据
     * @return GPreviewBuilder
     * **/
    public PreviewBuilder setCurrentIndex(int currentIndex) {
        intent.putExtra("position", currentIndex);
        return this;
    }



    /***
     * 设置图片禁用拖拽返回
     * @param isDrag  true  可以 false 默认 true
     * @return GPreviewBuilder
     * **/
    public PreviewBuilder setDrag(boolean isDrag) {
        intent.putExtra("isDrag", isDrag);
        return this;
    }

    /***
     * 设置图片禁用拖拽返回
     * @param isDrag  true  可以 false 默认 true
     * @param sensitivity   sensitivity MAX_TRANS_SCALE 的值来控制灵敏度。
     * @return GPreviewBuilder
     * **/
    public PreviewBuilder setDrag(boolean isDrag, float sensitivity) {
        intent.putExtra("isDrag", isDrag);
        intent.putExtra("sensitivity", sensitivity);
        return this;
    }

    /***
     * 是否设置为一张图片时 显示指示器  默认显示
     * @param isShow   true  显示 false 不显示
     * @return GPreviewBuilder
     * **/
    public PreviewBuilder setSingleShowType(boolean isShow) {
        intent.putExtra("isShow", isShow);
        return this;
    }

    /***
     * 设置超出内容点击退出（黑色区域）
     * @param isSingleFling  true  可以 false
     * @return GPreviewBuilder
     * **/
    public PreviewBuilder setSingleFling(boolean isSingleFling) {
        intent.putExtra("isSingleFling", isSingleFling);
        return this;
    }

    /***
     *  设置动画的时长
     * @param setDuration  单位毫秒
     * @return GPreviewBuilder
     * **/
    public PreviewBuilder setDuration(int setDuration) {
        intent.putExtra("duration", setDuration);
        return this;
    }
    /***
     *  设置是否全屏
     * @param isFullscreen  单位毫秒
     * @return GPreviewBuilder
     * **/
    public PreviewBuilder setFullscreen(boolean isFullscreen) {
        intent.putExtra("isFullscreen", isFullscreen);
        return this;
    }
    /***
     *  设置只有图片没有放大或者的缩小状态触退出
     * @param isScale    true false
     * @return GPreviewBuilder
     * **/
    public PreviewBuilder setIsScale(boolean isScale) {
        intent.putExtra("isScale", isScale);
        return this;
    }


    /***
     * 启动
     * **/
    public void start() {
        if (className == null) {
            intent.setClass(mContext, PreviewActivity.class);
        } else {
            intent.setClass(mContext, className);
        }
        mContext.startActivity(intent);
        mContext.overridePendingTransition(0, 0);
        intent = null;
        mContext = null;
    }


}
