package com.cql.imbilibili.data.api;

import com.cql.imbilibili.data.Constant;
import com.cql.imbilibili.model.BiliBiliResponse;
import com.cql.imbilibili.model.bangumi.BangumiDetail;
import com.cql.imbilibili.model.bangumi.BangumiIndex;
import com.cql.imbilibili.model.bangumi.BangumiIndexCond;
import com.cql.imbilibili.model.bangumi.SeasonGroup;
import com.cql.imbilibili.model.bangumi.SeasonRecommend;
import com.cql.imbilibili.model.bangumi.Tag;
import com.cql.imbilibili.model.bangumi.TimeTable;
import com.cql.imbilibili.model.home.IndexBangumiRecommend;
import com.cql.imbilibili.model.home.IndexPage;
import com.cql.imbilibili.model.video.SourceData;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by CQL on 2016/10/8.
 * 番剧api
 */

public interface BangumiService {


    @GET(Constant.BANGUMI_RECOMMEND)
    Observable<BiliBiliResponse<List<IndexBangumiRecommend>>> getBangumiRecommend(@Query("cursor") String cursor,
                                                                                  @Query("pagesize") int pagesize,
                                                                                  @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.INDEX_PAGE)
    Observable<BiliBiliResponse<IndexPage>> getIndexPage(@Query(Constant.QUERY_TS) long ts);

    @GET(Constant.BANGUMI_DETAIL)
    Observable<BiliBiliResponse<BangumiDetail>> getBangumiDetail(@Query(Constant.QUERY_SEASON_ID) String seasonId,
                                                                 @Query(Constant.QUERY_TS) long ts,
                                                                 @Query(Constant.QUERY_TYPE) String type);

    @GET(Constant.SEASON_RECOMMEND)
    Observable<BiliBiliResponse<SeasonRecommend>> getSeasonRecommend(@Path(Constant.QUERY_SEASON_ID) String seasonId,
                                                                     @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.GET_SOURCES)
    Observable<BiliBiliResponse<List<SourceData>>> getSource(@Query(Constant.QUERY_EPISODE_ID) String episodeId,
                                                             @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.SEASON_GROUP)
    Observable<BiliBiliResponse<List<SeasonGroup>>> getSeasonGroup(@Query(Constant.QUERY_TS) long ts);

    @GET(Constant.BANGUMI_INDEX)
    Observable<BiliBiliResponse<BangumiIndex>> getBangumiIndex(@Query("index_sort") int indexSort,
                                                               @Query("index_type") int indexType,
                                                               @Query("initial") String initial,
                                                               @Query("is_finish") String isFinish,
                                                               @Query("page") int page,
                                                               @Query("page_size") int pageSize,
                                                               @Query("quarter") int quarter,
                                                               @Query("start_year") int startYear,
                                                               @Query("tag_id") String tagId,
                                                               @Query(Constant.QUERY_TS) long ts,
                                                               @Query("update_period") int updatePeriod,
                                                               @Query("version") String version);

    @GET(Constant.BANGUMI_INDEX_COND)
    Observable<BiliBiliResponse<BangumiIndexCond>> getBangumiIndexCond(@Query(Constant.QUERY_TS) long ts,
                                                                       @Query(Constant.QUERY_TYPE) int type);

    @GET(Constant.TIME_TABLE)
    Observable<BiliBiliResponse<List<TimeTable>>> getBangumiTimeTable(@Query(value = "area_id",encoded = true) String areaId,
                                                                @Query("see_mine") String seeMine,
                                                                @Query(Constant.QUERY_TS) long ts);

    @GET(Constant.BANGUMI_TAG)
    Observable<BiliBiliResponse<List<Tag>>> getBangumiTags(@Query("page") int page,
                                                           @Query("pagesize") int pagesize,
                                                           @Query(Constant.QUERY_TYPE) int type,
                                                           @Query(Constant.QUERY_TS) long ts);
}
