package com.young.share.model.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nearby Yang on 2016-03-15.
 */
public class tes {

    /**
     * updatedAt : 2016-03-15 21:02:29
     * dtWantedNum : ["e64d7f659d"]
     * geographic : {"__type":"GeoPoint","longitude":114.422067,"latitude":23.038623}
     * dtLocation : 惠州市惠城区YG59
     * dtImgs : ["http://newfile.codenow.cn:8080/2c36767460334ea9bd8712bb03ce17a3.jpg","http://newfile.codenow.cn:8080/a6fd4f2169a24bb8940c91b129962d3d.jpg","http://newfile.codenow.cn:8080/03faff7ba6584841b2982c90f932a160.jpg","http://newfile.codenow.cn:8080/a746793953404451902341244016a796.jpg"]
     * objectId : 01647a3ecb
     * createdAt : 2016-01-03 17:54:17
     * userId : {"avatar":"http://newfile.codenow.cn:8080/d47a30d3acd44a899983a09408d72b50.jpeg","mobilePhoneNumber":"15018672947","emailVerified":true,"updatedAt":"2016-02-16 22:23:30","__type":"Object","username":"ff6220c","nickName":"song_guo","email":"6237556@qq.com","address":"汕头市 濠江区","objectId":"e64d7f659d","createdAt":"2015-10-20 21:26:24","age":20,"gender":true,"className":"_User","qq":"6237556"}
     * dtTag : 购物天堂
     * dtContent : [生病][生病][生病][生病]买买买
     * dtVisitedNum : ["e64d7f659d"]
     */

    @SerializedName("results")
    private List<ResultsEntity> results;

    public void setResults(List<ResultsEntity> results) {
        this.results = results;
    }

    public List<ResultsEntity> getResults() {
        return results;
    }

    public static class ResultsEntity {
        @SerializedName("updatedAt")
        private String updatedAt;
        /**
         * __type : GeoPoint
         * longitude : 114.422067
         * latitude : 23.038623
         */

        @SerializedName("geographic")
        private GeographicEntity geographic;
        @SerializedName("dtLocation")
        private String dtLocation;
        @SerializedName("objectId")
        private String objectId;
        @SerializedName("createdAt")
        private String createdAt;
        /**
         * avatar : http://newfile.codenow.cn:8080/d47a30d3acd44a899983a09408d72b50.jpeg
         * mobilePhoneNumber : 15018672947
         * emailVerified : true
         * updatedAt : 2016-02-16 22:23:30
         * __type : Object
         * username : ff6220c
         * nickName : song_guo
         * email : 6237556@qq.com
         * address : 汕头市 濠江区
         * objectId : e64d7f659d
         * createdAt : 2015-10-20 21:26:24
         * age : 20
         * gender : true
         * className : _User
         * qq : 6237556
         */

        @SerializedName("userId")
        private UserIdEntity userId;
        @SerializedName("dtTag")
        private String dtTag;
        @SerializedName("dtContent")
        private String dtContent;
        @SerializedName("dtWantedNum")
        private List<String> dtWantedNum;
        @SerializedName("dtImgs")
        private List<String> dtImgs;
        @SerializedName("dtVisitedNum")
        private List<String> dtVisitedNum;

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setGeographic(GeographicEntity geographic) {
            this.geographic = geographic;
        }

        public void setDtLocation(String dtLocation) {
            this.dtLocation = dtLocation;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public void setUserId(UserIdEntity userId) {
            this.userId = userId;
        }

        public void setDtTag(String dtTag) {
            this.dtTag = dtTag;
        }

        public void setDtContent(String dtContent) {
            this.dtContent = dtContent;
        }

        public void setDtWantedNum(List<String> dtWantedNum) {
            this.dtWantedNum = dtWantedNum;
        }

        public void setDtImgs(List<String> dtImgs) {
            this.dtImgs = dtImgs;
        }

        public void setDtVisitedNum(List<String> dtVisitedNum) {
            this.dtVisitedNum = dtVisitedNum;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public GeographicEntity getGeographic() {
            return geographic;
        }

        public String getDtLocation() {
            return dtLocation;
        }

        public String getObjectId() {
            return objectId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public UserIdEntity getUserId() {
            return userId;
        }

        public String getDtTag() {
            return dtTag;
        }

        public String getDtContent() {
            return dtContent;
        }

        public List<String> getDtWantedNum() {
            return dtWantedNum;
        }

        public List<String> getDtImgs() {
            return dtImgs;
        }

        public List<String> getDtVisitedNum() {
            return dtVisitedNum;
        }

        public static class GeographicEntity {
            @SerializedName("__type")
            private String type;
            @SerializedName("longitude")
            private double longitude;
            @SerializedName("latitude")
            private double latitude;

            public void setType(String type) {
                this.type = type;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public String getType() {
                return type;
            }

            public double getLongitude() {
                return longitude;
            }

            public double getLatitude() {
                return latitude;
            }
        }

        public static class UserIdEntity {
            @SerializedName("avatar")
            private String avatar;
            @SerializedName("mobilePhoneNumber")
            private String mobilePhoneNumber;
            @SerializedName("emailVerified")
            private boolean emailVerified;
            @SerializedName("updatedAt")
            private String updatedAt;
            @SerializedName("__type")
            private String type;
            @SerializedName("username")
            private String username;
            @SerializedName("nickName")
            private String nickName;
            @SerializedName("email")
            private String email;
            @SerializedName("address")
            private String address;
            @SerializedName("objectId")
            private String objectId;
            @SerializedName("createdAt")
            private String createdAt;
            @SerializedName("age")
            private int age;
            @SerializedName("gender")
            private boolean gender;
            @SerializedName("className")
            private String className;
            @SerializedName("qq")
            private String qq;

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public void setMobilePhoneNumber(String mobilePhoneNumber) {
                this.mobilePhoneNumber = mobilePhoneNumber;
            }

            public void setEmailVerified(boolean emailVerified) {
                this.emailVerified = emailVerified;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public void setObjectId(String objectId) {
                this.objectId = objectId;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public void setAge(int age) {
                this.age = age;
            }

            public void setGender(boolean gender) {
                this.gender = gender;
            }

            public void setClassName(String className) {
                this.className = className;
            }

            public void setQq(String qq) {
                this.qq = qq;
            }

            public String getAvatar() {
                return avatar;
            }

            public String getMobilePhoneNumber() {
                return mobilePhoneNumber;
            }

            public boolean isEmailVerified() {
                return emailVerified;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public String getType() {
                return type;
            }

            public String getUsername() {
                return username;
            }

            public String getNickName() {
                return nickName;
            }

            public String getEmail() {
                return email;
            }

            public String getAddress() {
                return address;
            }

            public String getObjectId() {
                return objectId;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public int getAge() {
                return age;
            }

            public boolean isGender() {
                return gender;
            }

            public String getClassName() {
                return className;
            }

            public String getQq() {
                return qq;
            }
        }
    }
}
