package com.example.eight.citylistdemo.db;

import org.litepal.crud.DataSupport;

/**
 * Created by eight on 2017/5/25.
 */

public class City extends DataSupport {
    private String cityName;
    private String provinceId;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }
}
