package com.young.share.model.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 坐标转换成位置信息
 * Created by Nearby Yang on 2016-03-03.
 */
public class Longitude2Location implements Serializable{

    /**
     * status : 0
     * result : {"location":{"lng":116.43212998566,"lat":38.766230070088},"formatted_address":"河北省沧州市任丘市","business":"","addressComponent":{"adcode":"130982","city":"沧州市","country":"中国","direction":"","distance":"","district":"任丘市","province":"河北省","street":"","street_number":"","country_code":0},"poiRegions":[],"sematic_description":"","cityCode":149}
     */

    @SerializedName("status")
    private int status;
    /**
     * location : {"lng":116.43212998566,"lat":38.766230070088}
     * formatted_address : 河北省沧州市任丘市
     * business :
     * addressComponent : {"adcode":"130982","city":"沧州市","country":"中国","direction":"","distance":"","district":"任丘市","province":"河北省","street":"","street_number":"","country_code":0}
     * poiRegions : []
     * sematic_description :
     * cityCode : 149
     */

    @SerializedName("result")
    private ResultEntity result;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public ResultEntity getResult() {
        return result;
    }

    public static class ResultEntity implements Serializable {
        /**
         * lng : 116.43212998566
         * lat : 38.766230070088
         */

        @SerializedName("location")
        private LocationEntity location;
        @SerializedName("formatted_address")
        private String formattedAddress;
        @SerializedName("business")
        private String business;
        /**
         * adcode : 130982
         * city : 沧州市
         * country : 中国
         * direction :
         * distance :
         * district : 任丘市
         * province : 河北省
         * street :
         * street_number :
         * country_code : 0
         */

        @SerializedName("addressComponent")
        private AddressComponentEntity addressComponent;
        @SerializedName("sematic_description")
        private String sematicDescription;
        @SerializedName("cityCode")
        private int cityCode;
        @SerializedName("poiRegions")
        private List<?> poiRegions;

        public void setLocation(LocationEntity location) {
            this.location = location;
        }

        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }

        public void setBusiness(String business) {
            this.business = business;
        }

        public void setAddressComponent(AddressComponentEntity addressComponent) {
            this.addressComponent = addressComponent;
        }

        public void setSematicDescription(String sematicDescription) {
            this.sematicDescription = sematicDescription;
        }

        public void setCityCode(int cityCode) {
            this.cityCode = cityCode;
        }

        public void setPoiRegions(List<?> poiRegions) {
            this.poiRegions = poiRegions;
        }

        public LocationEntity getLocation() {
            return location;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public String getBusiness() {
            return business;
        }

        public AddressComponentEntity getAddressComponent() {
            return addressComponent;
        }

        public String getSematicDescription() {
            return sematicDescription;
        }

        public int getCityCode() {
            return cityCode;
        }

        public List<?> getPoiRegions() {
            return poiRegions;
        }

        public static class LocationEntity  implements Serializable{
            @SerializedName("lng")
            private double lng;
            @SerializedName("lat")
            private double lat;

            public void setLng(double lng) {
                this.lng = lng;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLng() {
                return lng;
            }

            public double getLat() {
                return lat;
            }
        }

        public static class AddressComponentEntity  implements Serializable {
            @SerializedName("adcode")
            private String adcode;
            @SerializedName("city")
            private String city;
            @SerializedName("country")
            private String country;
            @SerializedName("direction")
            private String direction;
            @SerializedName("distance")
            private String distance;
            @SerializedName("district")
            private String district;
            @SerializedName("province")
            private String province;
            @SerializedName("street")
            private String street;
            @SerializedName("street_number")
            private String streetNumber;
            @SerializedName("country_code")
            private int countryCode;

            public void setAdcode(String adcode) {
                this.adcode = adcode;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public void setDirection(String direction) {
                this.direction = direction;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public void setStreet(String street) {
                this.street = street;
            }

            public void setStreetNumber(String streetNumber) {
                this.streetNumber = streetNumber;
            }

            public void setCountryCode(int countryCode) {
                this.countryCode = countryCode;
            }

            public String getAdcode() {
                return adcode;
            }

            public String getCity() {
                return city;
            }

            public String getCountry() {
                return country;
            }

            public String getDirection() {
                return direction;
            }

            public String getDistance() {
                return distance;
            }

            public String getDistrict() {
                return district;
            }

            public String getProvince() {
                return province;
            }

            public String getStreet() {
                return street;
            }

            public String getStreetNumber() {
                return streetNumber;
            }

            public int getCountryCode() {
                return countryCode;
            }
        }
    }
}
