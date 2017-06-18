package com.example.eight.citylistdemo;

import android.content.Context;
import android.util.Log;

import com.example.eight.citylistdemo.db.City;
import com.example.eight.citylistdemo.db.County;
import com.example.eight.citylistdemo.db.Province;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by eight on 2017/6/18.
 */

public class CityListUtils {
    /**
     * 解析 city.json ,获取城市列表,存入数据库
     * @param context
     * @return
     */
    public static boolean handleCityListFromJSON(Context context) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets()
                    .open("city.json"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();

            Gson gson = new Gson();
            // 解析省级列表
            List<JsonUtils> cityListUtilList = gson.fromJson(stringBuilder.toString(),
                    new TypeToken<List<JsonUtils>>(){}.getType());
            for (JsonUtils cityListUtil : cityListUtilList){
                Province province = new Province();
                province.setProvinceName(cityListUtil.getProvinceName());
                province.setProvincePY(cityListUtil.getProvincePY());
                province.save();

                // 解析市级列表
                List<JsonUtils.CityBean> cityBeenList = cityListUtil.getCity();
                for (JsonUtils.CityBean cityBean : cityBeenList) {
                    City city = new City();
                    city.setCityName(cityBean.getCityName());
                    city.setProvinceId(cityListUtil.getProvinceName());
                    city.save();

                    // 解析县级列表
                    List<JsonUtils.CityBean.CountyBean> countyBeanList = cityBean.getCounty();
                    for (JsonUtils.CityBean.CountyBean countyBean : countyBeanList) {
                        County county = new County();
                        county.setCountyName(countyBean.getCountyName());
                        county.setCountyPY(countyBean.getCountyPY());
                        county.setCountyNo(countyBean.getCountyNo());
                        county.setCityId(cityBean.getCityName());
                        county.save();
                    }
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
