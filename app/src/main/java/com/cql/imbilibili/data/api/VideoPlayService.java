package com.cql.imbilibili.data.api;

import com.cql.imbilibili.data.Constant;
import com.cql.imbilibili.model.video.VideoPlayData;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by CQL on 2016/10/8.
 */

public interface VideoPlayService {

    @GET(Constant.PLAY_URL)
    Observable<VideoPlayData> getPlayData(@Query("_aid") String aid,
                                          @Query("_tid") int tid,
                                          @Query("_p") int p,
                                          @Query("_down") int down,
                                          @Query("cid") String cid,
                                          @Query("quality") int quality,
                                          @Query("otype") String otype);
}
