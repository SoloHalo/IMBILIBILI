package com.cql.imbilibili.view.history;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.cql.imbilibili.R;
import com.cql.imbilibili.data.helper.CommonHelper;
import com.cql.imbilibili.model.BiliBiliResponse;
import com.cql.imbilibili.model.history.History;
import com.cql.imbilibili.utils.CallUtils;
import com.cql.imbilibili.utils.ToastUtils;
import com.cql.imbilibili.utils.UserManagerUtils;
import com.cql.imbilibili.view.BaseFragment;
import com.cql.imbilibili.view.adapter.LinearLayoutItemDecoration;
import com.cql.imbilibili.view.adapter.history.HistoryAdapter;
import com.cql.imbilibili.view.common.LoginActivity;
import com.cql.imbilibili.view.home.IDrawerLayoutActivity;
import com.cql.imbilibili.view.video.VideoDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CQL on 2016/10/8.
 * 历史记录界面
 */

public class HistoryRecordFragment extends BaseFragment {

    public static final String TAG = "HistoryRecordFragment";

    @BindView(R.id.root_view)
    ViewGroup mRootView;
    @BindView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.loading_view)
    ProgressBar mPb;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    private boolean mNeedToLoad;

    private HistoryAdapter mAdapter;

    private Call<BiliBiliResponse<List<History>>> mHistoryCall;

    public static HistoryRecordFragment newInstance() {
        return new HistoryRecordFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mNeedToLoad && UserManagerUtils.getInstance().getCurrentUser() != null) {
            mNeedToLoad = false;
            mRecyclerView.setVisibility(View.VISIBLE);
            mPb.setVisibility(View.VISIBLE);
            mBtnLogin.setVisibility(View.GONE);
            loadHistory();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mNeedToLoad = true;
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof IDrawerLayoutActivity) {
                    ((IDrawerLayoutActivity) getActivity()).openDrawer();
                }
            }
        });
        mToolbar.setTitle("历史记录");
        initRecyclerView();
        if (UserManagerUtils.getInstance().getCurrentUser() != null) {
            mNeedToLoad = false;
            mRecyclerView.setVisibility(View.VISIBLE);
            mPb.setVisibility(View.VISIBLE);
            mBtnLogin.setVisibility(View.GONE);
            loadHistory();
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mPb.setVisibility(View.GONE);
            mBtnLogin.setVisibility(View.VISIBLE);
            mBtnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginActivity.startActivity(getContext());
                }
            });
        }
    }

    private void initRecyclerView() {
        mAdapter = new HistoryAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnHistoryItemClickLisstener(new HistoryAdapter.OnHistoryItemClickLisstener() {
            @Override
            public void onHistoryItemClick(String aid) {
                VideoDetailActivity.startActivity(getContext(), aid);
            }
        });
    }

    private void loadHistory() {
        mHistoryCall = CommonHelper.getInstance().getHistoryService().getHistory(1, 200);
        mHistoryCall.enqueue(new Callback<BiliBiliResponse<List<History>>>() {
            @Override
            public void onResponse(Call<BiliBiliResponse<List<History>>> call, Response<BiliBiliResponse<List<History>>> response) {
                if (response.body().isSuccess()) {
                    mAdapter.setData(response.body().getResult());
                    mAdapter.notifyDataSetChanged();
                    mPb.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<BiliBiliResponse<List<History>>> call, Throwable t) {
                ToastUtils.showToastShort(R.string.load_error);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CallUtils.cancelCall(mHistoryCall);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_history_record;
    }
}
