package com.cql.imbilibili.view.adapter.bangumi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cql.imbilibili.R;
import com.cql.imbilibili.model.bangumi.Tag;
import com.cql.imbilibili.widget.ScalableImageView;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CQL on 2016/12/9.
 * 索引
 */

public class BangumiTagAdapter extends RecyclerView.Adapter {

    private List<Tag> mBangumiTagList;

    private Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public BangumiTagAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addBangumiIndexList(List<Tag> indexList){
        if (indexList == null){
            return;
        }
        if (mBangumiTagList == null){
            mBangumiTagList = indexList;
        }else {
            mBangumiTagList.addAll(indexList);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bangumi_tag_item,parent,false);
        return new IndexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IndexViewHolder viewHolder = (IndexViewHolder) holder;
        Tag tag = mBangumiTagList.get(position);
        Glide.with(mContext).load(tag.getCover())
                .asBitmap()
                .centerCrop()
                .into(viewHolder.ivCover);
        viewHolder.tvType.setText(tag.getTagName());
    }

    @Override
    public int getItemCount() {
        if (mBangumiTagList == null){
            return 0;
        }else {
            return mBangumiTagList.size();
        }
    }

    public class IndexViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.container)
        ViewGroup container;
        @BindView(R.id.cover)
        ScalableImageView ivCover;
        @BindView(R.id.tv_type)
        TextView tvType;

        IndexViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null){
                mOnItemClickListener.onItemClick();
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick();
    }
}
