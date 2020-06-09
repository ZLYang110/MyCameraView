package com.zlyandroid.mycameraview.picture;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zlyandroid.mycameraview.R;

public class MyBaseQuickAdapter extends BaseQuickAdapter<IThumbViewInfo, BaseViewHolder> {
    public static final String TAG = "MyBaseQuickAdapter";
    private Context context;
    public MyBaseQuickAdapter(Context context) {
        super(R.layout.item_image);
        this.context=context;
    }
    @Override
    protected void convert(BaseViewHolder helper, IThumbViewInfo item) {
        final ImageView thumbView = helper.getView(R.id.iv);
        Glide.with(context)
                .load(item.getUrl())
                .apply(new RequestOptions().error(R.mipmap.ic_erreri))
                .into(thumbView);
        helper.getView(R.id.iv).setTag(R.id.iv,item.getUrl());
    }
}
