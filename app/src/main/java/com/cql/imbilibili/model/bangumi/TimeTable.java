package com.cql.imbilibili.model.bangumi;

import com.google.gson.annotations.SerializedName;

/**
 * Created by CQL on 2016/12/4.
 */
//        "area_id":2,
//        "cover":"http://i0.hdslb.com/bfs/bangumi/e9cdf37e3d724a970baf73d47648d8f061f63e06.jpg",
//        "date":1480608000,
//        "delay":0,
//        "ep_id":96473,
//        "ep_index":"9",
//        "follow":0,
//        "is_published":1,
//        "ontime":"00:00",
//        "pub_date":"2016-12-02",
//        "season_id":5537,
//        "season_status":2,
//        "title":"伯纳德小姐说。"

public class TimeTable {
    @SerializedName("area_id")
    private int areaId;
    private String cover;
    private long date;
    private int delay;

    public String getDelayReason() {
        return delayReason;
    }

    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }

    @SerializedName("delay_reason")

    private String delayReason;
    @SerializedName("ep_id")
    private int epId;
    @SerializedName("ep_index")
    private String epIndex;
    private int follow;
    @SerializedName("is_published")
    private int isPublished;
    private String ontime;
    @SerializedName("pub_date")
    private String pubDate;
    @SerializedName("season_id")
    private int seasonId;
    @SerializedName("season_status")
    private int seasonStatus;
    private String title;

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getEpId() {
        return epId;
    }

    public void setEpId(int epId) {
        this.epId = epId;
    }

    public String getEpIndex() {
        return epIndex;
    }

    public void setEpIndex(String epIndex) {
        this.epIndex = epIndex;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public int getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(int isPublished) {
        this.isPublished = isPublished;
    }

    public String getOntime() {
        return ontime;
    }

    public void setOntime(String ontime) {
        this.ontime = ontime;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }

    public int getSeasonStatus() {
        return seasonStatus;
    }

    public void setSeasonStatus(int seasonStatus) {
        this.seasonStatus = seasonStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
