package com.cql.imbilibili.view.adapter.bangumi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cql.imbilibili.R;
import com.cql.imbilibili.model.bangumi.TimeTable;
import com.cql.imbilibili.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CQL on 2016/12/5.
 */

public class TimeTableAdapter extends RecyclerView.Adapter {
    public static final int TYPE_DATE = 0;
    public static final int TYPE_BAGUMI = 1;

    private Context mContext;
    private List<TimeTable> mTimeTables;

    private ArrayList<Integer> mDateViewPosition;
    private ArrayList<String> mDateViewContent;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public TimeTableAdapter(Context context) {
        mContext = context;
        mDateViewPosition = new ArrayList<>();
        mDateViewContent = new ArrayList<>();
    }

    public void sortTimeTable(List<TimeTable> timeTables){
        if (timeTables == null){
            return;
        }
        mTimeTables = timeTables;
        mDateViewPosition.add(0);
        mDateViewContent.add(mTimeTables.get(0).getPubDate());
        int size = mTimeTables.size();
        int preDateItemCount = 1;
        for (int i = 0;i<size;i++){
            if (!mTimeTables.get(i).getPubDate().equals(mDateViewContent.get(mDateViewContent.size()-1))){
                mDateViewPosition.add(i+preDateItemCount);
                preDateItemCount+=1;
                mDateViewContent.add(timeTables.get(i).getPubDate());
            }
        }
    }

    private int getFrontDateViewCount(int position) {
        int count = 0;
        for (int i = 0; i < mDateViewPosition.size(); i++) {
            if (mDateViewPosition.get(i) < position) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDateViewPosition.contains(position)){
            return TYPE_DATE;
        }else {
            return TYPE_BAGUMI;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_DATE) {
            View view = inflater.inflate(R.layout.bangumi_time_table_head, parent, false);
            viewHolder = new TimeViewHolder(view);
        }else if (viewType == TYPE_BAGUMI){
            View view = inflater.inflate(R.layout.bangumi_table_grid_item,parent,false);
            viewHolder = new BangumiViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_DATE){
            TimeViewHolder viewHolder = (TimeViewHolder) holder;
            viewHolder.text2.setText(mDateViewContent.get(getFrontDateViewCount(position)));
        }else if (getItemViewType(position) == TYPE_BAGUMI){
            BangumiViewHolder viewHolder = (BangumiViewHolder) holder;
            TimeTable timeTable = mTimeTables.get(position - getFrontDateViewCount(position));
            Glide.with(mContext).load(timeTable.getCover()).into(viewHolder.iv_cover);
            viewHolder.tvUpTime.setText(timeTable.getOntime());
            viewHolder.title.setText(timeTable.getTitle());
            if (timeTable.getDelay() == 0){
                viewHolder.tv_info.setText(StringUtils.format("第%s话",timeTable.getEpIndex()));
            }else {
                viewHolder.tv_info.setText(timeTable.getDelayReason());
            }
            viewHolder.seasonId = timeTable.getSeasonId();
        }
    }

    @Override
    public int getItemCount() {
        if (mTimeTables == null){
            return 0;
        }else {
            return mTimeTables.size() + 9;
        }
    }

    public class TimeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.text1)
        TextView text1;
        @BindView(R.id.text2)
        TextView text2;

        TimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class BangumiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.grid_ser)
        ViewGroup container;
        @BindView(R.id.cover)
        ImageView iv_cover;
        @BindView(R.id.favourites)
        TextView tvUpTime;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.season)
        TextView season;
        @BindView(R.id.tv_info)
        TextView tv_info;
        private int seasonId;

        BangumiViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(seasonId);
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int seasonId);
    }
}
