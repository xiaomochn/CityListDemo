package com.example.eight.citylistdemo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by eight on 2017/6/18.
 */

public class JsonUtils {
    /**
     * provinceName : 北京
     * provincePY : beijing
     * city : [{"cityName":"北京","county":
     * [{"countyName":"北京","countyNo":"CN101010100","countyPY":"beijing"},
     * {"countyName":"海淀","countyNo":"CN101010200","countyPY":"haidian"},
     * {"countyName":"朝阳","countyNo":"CN101010300","countyPY":"chaoyang"},
     * {"countyName":"顺义","countyNo":"CN101010400","countyPY":"shunyi"},
     * {"countyName":"怀柔","countyNo":"CN101010500","countyPY":"huairou"},
     * {"countyName":"通州","countyNo":"CN101010600","countyPY":"tongzhou"},
     * {"countyName":"昌平","countyNo":"CN101010700","countyPY":"changping"},
     * {"countyName":"延庆","countyNo":"CN101010800","countyPY":"yanqing"},
     * {"countyName":"丰台","countyNo":"CN101010900","countyPY":"fengtai"},
     * {"countyName":"石景山","countyNo":"CN101011000","countyPY":"shijingshan"},
     * {"countyName":"大兴","countyNo":"CN101011100","countyPY":"daxing"},
     * {"countyName":"房山","countyNo":"CN101011200","countyPY":"fangshan"},
     * {"countyName":"密云","countyNo":"CN101011300","countyPY":"miyun"},
     * {"countyName":"门头沟","countyNo":"CN101011400","countyPY":"mentougou"},
     * {"countyName":"平谷","countyNo":"CN101011500","countyPY":"pinggu"}]}]
     */

    @SerializedName("provinceName")
    private String provinceName;
    @SerializedName("provincePY")
    private String provincePY;
    /**
     * cityName : 北京
     * county : [{"countyName":"北京","countyNo":"CN101010100","countyPY":"beijing"},
     * {"countyName":"海淀","countyNo":"CN101010200","countyPY":"haidian"},
     * {"countyName":"朝阳","countyNo":"CN101010300","countyPY":"chaoyang"},
     * {"countyName":"顺义","countyNo":"CN101010400","countyPY":"shunyi"},
     * {"countyName":"怀柔","countyNo":"CN101010500","countyPY":"huairou"},
     * {"countyName":"通州","countyNo":"CN101010600","countyPY":"tongzhou"},
     * {"countyName":"昌平","countyNo":"CN101010700","countyPY":"changping"},
     * {"countyName":"延庆","countyNo":"CN101010800","countyPY":"yanqing"},
     * {"countyName":"丰台","countyNo":"CN101010900","countyPY":"fengtai"},
     * {"countyName":"石景山","countyNo":"CN101011000","countyPY":"shijingshan"},
     * {"countyName":"大兴","countyNo":"CN101011100","countyPY":"daxing"},
     * {"countyName":"房山","countyNo":"CN101011200","countyPY":"fangshan"},
     * {"countyName":"密云","countyNo":"CN101011300","countyPY":"miyun"},
     * {"countyName":"门头沟","countyNo":"CN101011400","countyPY":"mentougou"},
     * {"countyName":"平谷","countyNo":"CN101011500","countyPY":"pinggu"}]
     */

    @SerializedName("city")
    private List<CityBean> city;

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

    public List<CityBean> getCity() {
        return city;
    }

    public void setCity(List<CityBean> city) {
        this.city = city;
    }

    public static class CityBean {
        @SerializedName("cityName")
        private String cityName;
        /**
         * countyName : 北京
         * countyNo : CN101010100
         * countyPY : beijing
         */

        @SerializedName("county")
        private List<CountyBean> county;

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public List<CountyBean> getCounty() {
            return county;
        }

        public void setCounty(List<CountyBean> county) {
            this.county = county;
        }

        public static class CountyBean {
            @SerializedName("countyName")
            private String countyName;
            @SerializedName("countyNo")
            private String countyNo;
            @SerializedName("countyPY")
            private String countyPY;

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
        }
    }

}
