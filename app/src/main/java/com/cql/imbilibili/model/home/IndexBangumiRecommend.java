package com.cql.imbilibili.model.home;

import com.cql.imbilibili.model.bangumi.Bangumi;

/**
 * Created by home on 2016/8/1.
 */
public class IndexBangumiRecommend extends Bangumi {

    private String cursor;
    private String desc;
    private int id;
    private String link;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
