package com.cql.imbilibili.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by CQL on 2016/10/7.
 * 懒加载的Fragment
 */

public abstract class LazyLoadFragment extends BaseFragment {

    protected boolean isViewInitiated;
    protected boolean isVisibleToUser;
    protected boolean isDataInitiated;

    protected abstract void fetchData();

    public String getTitle() {
        return "";
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewInitiated = true;
        prepareFetchData(false);
    }

    //设置Fragment可见或者不可见时会调用此方法
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        prepareFetchData(false);
    }

    public void prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            isDataInitiated = true;
            fetchData();
        }
    }
}
