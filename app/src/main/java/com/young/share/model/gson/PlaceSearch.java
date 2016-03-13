package com.young.share.model.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * poi周边服务搜索
 * 简略信息
 *
 * Created by Nearby Yang on 2016-03-12.
 */
public class PlaceSearch implements Serializable {


    /**
     * status : 0
     * message : ok
     * total : 24
     * results : [{"name":"东森实业发展有限公司","location":{"lat":23.107385,"lng":114.398395},"address":"菱湖三路8号","street_id":"680ebd72ccaf8639a5da39c7","telephone":"0752-2390801","detail":0,"uid":"680ebd72ccaf8639a5da39c7"},{"name":"东森商行","location":{"lat":23.178632,"lng":114.309961},"address":"博罗县其他商业东街193号附近","street_id":"e5ac135bb9238537b54f546e","telephone":"(0752)6792628","detail":1,"uid":"e5ac135bb9238537b54f546e"},{"name":"东森酒家","location":{"lat":23.047672,"lng":114.428001},"address":"演达大道26号","street_id":"30f30c0cfe0b0728a8059dc2","telephone":"(0752)2533838","detail":1,"uid":"30f30c0cfe0b0728a8059dc2"},{"name":"东森制衣厂","location":{"lat":23.004883,"lng":114.337739},"address":"惠州市惠城区","street_id":"7636d121d47ffdfa9d9d6882","telephone":"15916419068","detail":0,"uid":"7636d121d47ffdfa9d9d6882"},{"name":"东森华庭","location":{"lat":23.178723,"lng":114.310191},"address":"罗阳镇商业东街（东山森林公园对面）","street_id":"3c0ea7c5b3c0b8e1faf0cbbf","telephone":"(0752)6267883","detail":1,"uid":"3c0ea7c5b3c0b8e1faf0cbbf"}]
     */

    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message;
    @SerializedName("total")
    private int total;
    /**
     * name : 东森实业发展有限公司
     * location : {"lat":23.107385,"lng":114.398395}
     * address : 菱湖三路8号
     * street_id : 680ebd72ccaf8639a5da39c7
     * telephone : 0752-2390801
     * detail : 0
     * uid : 680ebd72ccaf8639a5da39c7
     */

    @SerializedName("results")
    private List<ResultsEntity> results;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setResults(List<ResultsEntity> results) {
        this.results = results;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getTotal() {
        return total;
    }

    public List<ResultsEntity> getResults() {
        return results;
    }

    public static class ResultsEntity implements Serializable {
        @SerializedName("name")
        private String name;
        /**
         * lat : 23.107385
         * lng : 114.398395
         */

        @SerializedName("location")
        private LocationEntity location;
        @SerializedName("address")
        private String address;
        @SerializedName("street_id")
        private String streetId;
        @SerializedName("telephone")
        private String telephone;
        @SerializedName("detail")
        private int detail;
        @SerializedName("uid")
        private String uid;

        public ResultsEntity() {
        }

        public ResultsEntity(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setLocation(LocationEntity location) {
            this.location = location;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setStreetId(String streetId) {
            this.streetId = streetId;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public void setDetail(int detail) {
            this.detail = detail;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public LocationEntity getLocation() {
            return location;
        }

        public String getAddress() {
            return address;
        }

        public String getStreetId() {
            return streetId;
        }

        public String getTelephone() {
            return telephone;
        }

        public int getDetail() {
            return detail;
        }

        public String getUid() {
            return uid;
        }

        public static class LocationEntity implements Serializable {
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
