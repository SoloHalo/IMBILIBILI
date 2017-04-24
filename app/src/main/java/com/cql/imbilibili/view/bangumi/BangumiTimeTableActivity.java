package com.cql.imbilibili.view.bangumi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.cql.imbilibili.R;
import com.cql.imbilibili.data.BilibiliResponseHandler;
import com.cql.imbilibili.data.helper.CommonHelper;
import com.cql.imbilibili.model.BiliBiliResponse;
import com.cql.imbilibili.model.bangumi.TimeTable;
import com.cql.imbilibili.utils.DisposableUtils;
import com.cql.imbilibili.utils.RxCacheUtils;
import com.cql.imbilibili.utils.StatusBarUtils;
import com.cql.imbilibili.utils.ToastUtils;
import com.cql.imbilibili.view.BaseActivity;
import com.cql.imbilibili.view.adapter.bangumi.BangumiIndexItemDecoration;
import com.cql.imbilibili.view.adapter.bangumi.TimeTableAdapter;
import com.cql.imbilibili.widget.LoadMoreRecyclerView;
import com.lh.cachelibrary.strategy.CacheStrategy;
import com.lh.cachelibrary.utils.TypeBuilder;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by CQL on 2016/12/5.
 */

public class BangumiTimeTableActivity extends BaseActivity implements TimeTableAdapter.OnItemClickListener {

    @BindView(R.id.recycle_view)
    LoadMoreRecyclerView mRecycleView;
    @BindView(R.id.loading)
    ProgressBar mProgressBar;
    @BindView(R.id.nav_top_bar)
    Toolbar toolbar;

    private List<TimeTable> mTimeTables;
    private TimeTableAdapter adapter;
    private Disposable mDisposable;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, BangumiTimeTableActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangumi_time_table);
        ButterKnife.bind(this);
        initView();
        loadBangumi();
    }

    private void initView() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle("番剧时间表");
        StatusBarUtils.setSimpleToolbarLayout(this, toolbar);
        adapter = new TimeTableAdapter(this);
        adapter.setOnItemClickListener(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, LinearLayout.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mRecycleView.getItemViewType(position);
                if (type == TimeTableAdapter.TYPE_DATE) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });
        mRecycleView.addItemDecoration(new BangumiIndexItemDecoration(this));
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setLoadingViewState(LoadMoreRecyclerView.STATE_NO_MORE);
        mRecycleView.setAdapter(adapter);
    }

    private void loadBangumi() {
        Type type = TypeBuilder.newBuilder(BiliBiliResponse.class)
                .beginNestedType(List.class)
                .addParamType(TimeTable.class)
                .endNestedType()
                .build();
        mDisposable = CommonHelper.getInstance().getBangumiService().getBangumiTimeTable("2",
                "0", System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
//                .subscribeWith(new DisposableSingleObserver<BiliBiliResponse<List<TimeTable>>>() {
//                    @Override
//                    public void onSuccess(BiliBiliResponse<List<TimeTable>> listBiliBiliResponse) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//                });
                .compose(RxCacheUtils.getInstance().<BiliBiliResponse<List<TimeTable>>>transformer("time_table", CacheStrategy.priorityRemote(),type))
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<List<TimeTable>>, List<TimeTable>>handlerResult())
                .observeOn(AndroidSchedulers.mainThread())
                .firstOrError()
                .subscribeWith(new DisposableSingleObserver<List<TimeTable>>() {
                    @Override
                    public void onSuccess(List<TimeTable> timeTables) {
                        mProgressBar.setVisibility(View.GONE);
                        mTimeTables = timeTables;
                        adapter.sortTimeTable(mTimeTables);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToastShort(R.string.load_error);
                    }
                });
    }

    @Override
    public void onItemClick(int seasonId) {
        BangumiDetailActivity.startActivity(this, seasonId + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mDisposable);
    }
}
