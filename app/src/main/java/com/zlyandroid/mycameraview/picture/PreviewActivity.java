package com.zlyandroid.mycameraview.picture;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zlyandroid.mycameraview.R;
import com.zlyandroid.mycameraview.picture.view.BasePhotoFragment;
import com.zlyandroid.mycameraview.picture.widget.PhotoViewPager;
import com.zlyandroid.mycameraview.picture.widget.SmoothImageView;

import java.util.ArrayList;
import java.util.List;

public class PreviewActivity  extends FragmentActivity {
    protected boolean isTransformOut = false;
    /*** 图片的地址***/
    private List<IThumbViewInfo> imgUrls;
    /*** 当前图片的位置 ***/
    protected int currentIndex;
    /*** 图片的展示的Fragment***/
    private List<BasePhotoFragment> fragments = new ArrayList<>();
    /*** 展示图片的viewPager ***/
    private PhotoViewPager viewPager;
    /*** 显示图片数**/
    private TextView ltAddDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview);
        initView();
        initData();
    }
    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        ltAddDot = findViewById(R.id.ltAddDot);

    }
    @SuppressLint("StringFormatMatches")
    private void initData() {
        imgUrls = getIntent().getParcelableArrayListExtra("imagePaths");
        currentIndex = getIntent().getIntExtra("position", -1);
        int duration = getIntent().getIntExtra("duration", 300);
        boolean isFullscreen=getIntent().getBooleanExtra("isFullscreen",false);
        boolean isScale=getIntent().getBooleanExtra("isScale",false);
        SmoothImageView.setFullscreen(isFullscreen);
        SmoothImageView.setIsScale(isScale);
        if (isFullscreen){
            setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        }
        try {
            SmoothImageView.setDuration(duration);
            Class<? extends BasePhotoFragment> sss;
            sss = (Class<? extends BasePhotoFragment>) getIntent().getSerializableExtra("className");
            iniFragment(imgUrls, currentIndex, sss);
        } catch (Exception e) {
            iniFragment(imgUrls, currentIndex, BasePhotoFragment.class);
        }

        //viewPager的适配器
        PhotoPagerAdapter adapter = new PhotoPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex);
        viewPager.setOffscreenPageLimit(3);
        ltAddDot.setVisibility(View.VISIBLE);
        ltAddDot.setText(getString(R.string.string_count, (currentIndex + 1), imgUrls.size()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //当被选中的时候设置小圆点和当前位置
                if (ltAddDot != null) {
                    ltAddDot.setText(getString(R.string.string_count, (position + 1), imgUrls.size()));
                }
                currentIndex = position;
                viewPager.setCurrentItem(currentIndex, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                BasePhotoFragment fragment = fragments.get(currentIndex);
            }
        });
    }

    /**
     * 初始化
     *
     * @param imgUrls      集合
     * @param currentIndex 选中索引
     * @param className    显示Fragment
     **/
    protected void iniFragment(List<IThumbViewInfo> imgUrls, int currentIndex, Class<? extends BasePhotoFragment> className) {
        if (imgUrls != null) {
            int size = imgUrls.size();
            for (int i = 0; i < size; i++) {
                fragments.add(BasePhotoFragment.
                        getInstance(className, imgUrls.get(i),
                                currentIndex == i,
                                getIntent().getBooleanExtra("isSingleFling", false),
                                getIntent().getBooleanExtra("isDrag", false),
                                getIntent().getFloatExtra("sensitivity", 0.5f))
                );
            }
        } else {
            finish();
        }
    }



    /***退出预览的动画***/
    public void transformOut() {
        if (isTransformOut) {
            return;
        }
        getViewPager().setEnabled(false);
        isTransformOut = true;
        int currentItem = viewPager.getCurrentItem();
        if (currentItem < imgUrls.size()) {
            BasePhotoFragment fragment = fragments.get(currentItem);
            if (ltAddDot != null) {
                ltAddDot.setVisibility(View.GONE);
            }
            fragment.changeBg(Color.TRANSPARENT);
            fragment.transformOut(new SmoothImageView.onTransformListener() {
                @Override
                public void onTransformCompleted(SmoothImageView.Status status) {
                    getViewPager().setEnabled(true);
                    exit();
                }
            });
        } else {
            exit();
        }
    }
    /***
     * 得到PhotoViewPager
     * @return PhotoViewPager
     * **/
    public PhotoViewPager getViewPager() {
        return viewPager;
    }
    @Override
    public void onBackPressed() {
        isTransformOut =false;
        transformOut();
    }
    /**
     * 关闭页面
     */
    private void exit() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        ZoomMediaLoader.getInstance().getLoader().clearMemory(this);
        if (viewPager != null) {
            viewPager.setAdapter(null);
            viewPager.clearOnPageChangeListeners();
            viewPager.removeAllViews();
            viewPager = null;
        }
        if (fragments != null) {
            fragments.clear();
            fragments = null;
        }
        if (imgUrls != null) {
            imgUrls.clear();
            imgUrls = null;
        }
        super.onDestroy();
    }

    /**
     * pager的适配器
     */
    private class PhotoPagerAdapter extends FragmentStatePagerAdapter {

        PhotoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }

    }

}
