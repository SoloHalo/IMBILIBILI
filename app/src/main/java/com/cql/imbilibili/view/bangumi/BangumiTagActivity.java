package com.cql.imbilibili.view.bangumi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cql.imbilibili.R;
import com.cql.imbilibili.data.BilibiliResponseHandler;
import com.cql.imbilibili.data.helper.CommonHelper;
import com.cql.imbilibili.model.BiliBiliResponse;
import com.cql.imbilibili.model.bangumi.Tag;
import com.cql.imbilibili.utils.DisposableUtils;
import com.cql.imbilibili.utils.RxCacheUtils;
import com.cql.imbilibili.utils.StatusBarUtils;
import com.cql.imbilibili.utils.ToastUtils;
import com.cql.imbilibili.view.BaseActivity;
import com.cql.imbilibili.view.adapter.bangumi.BangumiIndexItemDecoration;
import com.cql.imbilibili.view.adapter.bangumi.BangumiTagAdapter;
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
 * Created by CQL on 2016/12/9.
 * 索引
 */

public class BangumiTagActivity extends BaseActivity{

    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;

    private BangumiTagAdapter mAdapter;

    private List<Tag> mBangumiTags;

    private Disposable mDisposable;


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, BangumiTagActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangumi_tag);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    private void initView() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setTitle("番剧索引");
        StatusBarUtils.setSimpleToolbarLayout(this, mToolbar);
        mAdapter = new BangumiTagAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.addItemDecoration(new BangumiIndexItemDecoration(this));
        mRecycleView.setAdapter(mAdapter);
    }

    private void loadData() {
        Type type = TypeBuilder.newBuilder(BiliBiliResponse.class)
                .beginNestedType(List.class)
                .addParamType(Tag.class)
                .endNestedType()
                .build();

        mDisposable = CommonHelper.getInstance().getBangumiService()
                .getBangumiTags(1, 100, 0, System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .compose(RxCacheUtils.getInstance().<BiliBiliResponse<List<Tag>>>transformer("bangumi_tags", CacheStrategy.priorityCache(), type))
                .flatMap(BilibiliResponseHandler.<BiliBiliResponse<List<Tag>>, List<Tag>>handlerResult())
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Tag>>() {
                    @Override
                    public void onSuccess(List<Tag> tags) {
                        mBangumiTags = tags;
                        mAdapter.addBangumiIndexList(mBangumiTags);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToastShort(R.string.load_error);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableUtils.dispose(mDisposable);
    }
}
