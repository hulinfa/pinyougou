package com.pinyougou.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageResult implements Serializable {

    private long pages;
    private List<?> rows;

    public PageResult() {
        this.rows = new ArrayList<>();
    }

    public PageResult(long pages, List<?> rows) {
        this.pages = pages;
        this.rows = rows;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
