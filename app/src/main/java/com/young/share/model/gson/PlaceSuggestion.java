package com.young.share.model.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 百度地图 查找地点
 * Created by Nearby Yang on 2016-03-04.
 */
public class PlaceSuggestion {

    /**
     * status : 0
     * message : ok
     * result : [{"name":"天安门","location":{"lat":39.915119,"lng":116.403963},"uid":"65e1ee886c885190f60e77ff","city":"北京市","district":"东城区","business":"","cityid":"131"},{"name":"天安门广场","location":{"lat":39.912735,"lng":116.404015},"uid":"c9b5fb91d49345bc5d0d0262","city":"北京市","district":"东城区","business":"","cityid":"131"},{"name":"天安门广场-国旗","location":{"lat":39.913279,"lng":116.403929},"uid":"4ae2adcf574bcd2b38221c66","city":"北京市","district":"东城区","business":"","cityid":"131"},{"name":"天安门广场-入口","location":{"lat":39.907253,"lng":116.405886},"uid":"9711b16c49ec39e78605bfb4","city":"北京市","district":"东城区","business":"","cityid":"131"},{"name":"天安门城楼-售票处","location":{"lat":39.915778,"lng":116.40368},"uid":"8f514d6aee1b4d262afc483a","city":"北京市","district":"东城区","business":"","cityid":"131"},{"name":"天安门东-地铁站","location":{"lat":39.91408,"lng":116.407851},"uid":"940aeb3c98d5a0218a2fb5de","city":"北京市","district":"东城区","business":"","cityid":"131"},{"name":"天安门城楼","location":{"lat":39.915291,"lng":116.403857},"uid":"8f1cf54cd1dee7b25abbbcd1","city":"北京市","district":"东城区","business":"","cityid":"131"},{"name":"天安门西-地铁站","location":{"lat":39.913776,"lng":116.39805},"uid":"002975204d3b1e9b9968b4de","city":"北京市","district":"西城区","business":"","cityid":"131"},{"name":"天安门附近酒店","uid":"","city":"","district":"","business":"","cityid":"0"},{"name":"全聚德(天安门店)","location":{"lat":39.907819,"lng":116.406449},"uid":"0ce65c2df9ca8b402d473321","city":"北京市","district":"东城区","business":"","cityid":"131"}]
     */

    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message;
    /**
     * name : 天安门
     * location : {"lat":39.915119,"lng":116.403963}
     * uid : 65e1ee886c885190f60e77ff
     * city : 北京市
     * district : 东城区
     * business :
     * cityid : 131
     */

    @SerializedName("result")
    private List<ResultEntity> result;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public static class ResultEntity {
        @SerializedName("name")
        private String name;
        /**
         * lat : 39.915119
         * lng : 116.403963
         */

        @SerializedName("location")
        private LocationEntity location;
        @SerializedName("uid")
        private String uid;
        @SerializedName("city")
        private String city;
        @SerializedName("district")
        private String district;
        @SerializedName("business")
        private String business;
        @SerializedName("cityid")
        private String cityid;

        public ResultEntity() {
        }

        public ResultEntity(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setLocation(LocationEntity location) {
            this.location = location;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public void setBusiness(String business) {
            this.business = business;
        }

        public void setCityid(String cityid) {
            this.cityid = cityid;
        }

        public String getName() {
            return name;
        }

        public LocationEntity getLocation() {
            return location;
        }

        public String getUid() {
            return uid;
        }

        public String getCity() {
            return city;
        }

        public String getDistrict() {
            return district;
        }

        public String getBusiness() {
            return business;
        }

        public String getCityid() {
            return cityid;
        }

        public static class LocationEntity {
            @SerializedName("lat")
            private double lat;
            @SerializedName("lng")
            private double lng;

            public void setLat(double lat) {
                this.lat = lat;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }

            public double getLat() {
                return lat;
            }

            public double getLng() {
                return lng;
            }
        }
    }
}
