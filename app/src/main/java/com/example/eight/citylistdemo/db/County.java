package com.example.eight.citylistdemo.db;

import org.litepal.crud.DataSupport;

/**
 * Created by eight on 2017/5/25.
 */

public class County extends DataSupport {
    /**
     * "countyName": "哈尔滨",
     * "countyNo": "CN101050101",
     * "countyPY": "haerbin"
     */

    private String countyName;
    private String countyNo;
    private String countyPY;
    private String cityId;

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyNo() {
        return countyNo;
    }

    public void setCountyNo(String countyNo) {
        this.countyNo = countyNo;
    }

    public String getCountyPY() {
        return countyPY;
    }

    public void setCountyPY(String countyPY) {
        this.countyPY = countyPY;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}
