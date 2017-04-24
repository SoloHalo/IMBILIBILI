package com.cql.imbilibili.data.helper;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.cql.imbilibili.data.interceptor.BiliSignInterceptor;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by CQL on 2016/11/17.
 */

class BaseHelper {

    HttpLoggingInterceptor mLog;
    BiliSignInterceptor mSign;
    StethoInterceptor mStetho;

    BaseHelper() {
        mLog = new HttpLoggingInterceptor();
        mLog.setLevel(HttpLoggingInterceptor.Level.BODY);
        mSign = new BiliSignInterceptor();
        mStetho = new StethoInterceptor();
    }
}
