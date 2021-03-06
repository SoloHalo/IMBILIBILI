package com.cql.imbilibili.data.api;

import com.cql.imbilibili.data.Constant;
import com.cql.imbilibili.model.BiliBiliResponse;
import com.cql.imbilibili.model.home.Splash;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by CQL on 2016/10/8.
 * 闪屏Api
 */

public interface SplashService {

    @GET(Constant.SPLASH_URL)
    Call<BiliBiliResponse<Splash>> getSplash(@Query("plat") String plat,
                                             @Query("build") String build,
                                             @Query("channel") String channel,
                                             @Query("width") String width,
                                             @Query("height") String height);
}
