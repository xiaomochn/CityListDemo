package com.example.eight.citylistdemo.db;

import org.litepal.crud.DataSupport;

/**
 * Created by eight on 2017/5/25.
 */

public class Province extends DataSupport {
    private String provinceName;
    private String provincePY;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvincePY() {
        return provincePY;
    }

    public void setProvincePY(String provincePY) {
        this.provincePY = provincePY;
    }
}
