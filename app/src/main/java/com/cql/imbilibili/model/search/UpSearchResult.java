package com.cql.imbilibili.model.search;

import java.util.List;

/**
 * Created by CQL on 2016/10/6.
 */

public class UpSearchResult {
    private int pages;
    private List<Up> items;

    public UpSearchResult() {
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<Up> getItems() {
        return items;
    }

    public void setItems(List<Up> items) {
        this.items = items;
    }
}
