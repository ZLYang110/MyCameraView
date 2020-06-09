package com.zlyandroid.mycameraview.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zlyandroid.mycameraview.R;
import com.zlyandroid.mycameraview.camera.Util;
import com.zlyandroid.mycameraview.picture.PreviewBuilder;
import com.zlyandroid.mycameraview.picture.IThumbViewInfo;
import com.zlyandroid.mycameraview.picture.ImageUrlConfig;
import com.zlyandroid.mycameraview.picture.MyBaseQuickAdapter;
import com.zlyandroid.mycameraview.utils.FileUtils;
import com.zlylib.titlebarlib.ActionBarCommon;
import com.zlylib.titlebarlib.OnActionBarChildClickListener;

import java.util.ArrayList;
import java.util.List;

public class AllPictureActivity extends AppCompatActivity {



    private ActionBarCommon abc;
    private RecyclerView mRecyclerView;

    private ArrayList<IThumbViewInfo> mThumbViewInfoList = new ArrayList<>();
    private GridLayoutManager mGridLayoutManager;


    /**
     * 获取启动UserActivity的intent
     *
     * @param activity
     * @return
     */
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, AllPictureActivity.class);
        activity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);
        initView();
        initData();
    }
    private void initView() {
        abc = findViewById(R.id.abc);
        mRecyclerView = findViewById(R.id.recycler_view);
        mGridLayoutManager = new GridLayoutManager(this,4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

    }
    private void initData() {
        //准备数据
        List<String> urls = FileUtils.getFilesAllName(Util.photographPath);
        if(urls.size()==0){
            urls = ImageUrlConfig.getUrls();
        }
        if(urls.size()>0){
            for (int i = 0; i < urls.size(); i++) {
               // mThumbViewInfoList.add(new IThumbViewInfo(urls.get(i),urls.get(i))); 视频
                mThumbViewInfoList.add(new IThumbViewInfo(urls.get(i)));
            }

            MyBaseQuickAdapter adapter=new MyBaseQuickAdapter(this);
            adapter.addData(mThumbViewInfoList);
            mRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    computeBoundsBackward(mGridLayoutManager.findFirstVisibleItemPosition());
                    PreviewBuilder.from(AllPictureActivity.this)
                            .setData(mThumbViewInfoList)
                            .setCurrentIndex(position)
                            .setSingleFling(true)
                            .start();
                }
            });
        }else{
            Toast.makeText(AllPictureActivity.this,"没有图片哦",Toast.LENGTH_LONG);
        }


        abc.setOnLeftIconClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * 查找信息
     * 从第一个完整可见item逆序遍历，如果初始位置为0，则不执行方法内循环
     */
    private void computeBoundsBackward(int firstCompletelyVisiblePos) {
        for (int i = firstCompletelyVisiblePos;i < mThumbViewInfoList.size(); i++) {
            View itemView = mGridLayoutManager.findViewByPosition(i);
            Rect bounds = new Rect();
            if (itemView != null) {
                ImageView thumbView = (ImageView) itemView.findViewById(R.id.iv);
                thumbView.getGlobalVisibleRect(bounds);
            }
            mThumbViewInfoList.get(i).setBounds(bounds);
        }
    }
}
