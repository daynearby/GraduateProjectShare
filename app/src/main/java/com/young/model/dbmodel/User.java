package com.young.model.dbmodel;

import org.litepal.annotation.Column;

/**
 * Created by Nearby Yang on 2015-11-19.
 */
public class User extends DBModel {

    private String address;
    private String avatar;
    private String qq;
    private int age;
    private boolean gender;
    private String signture;
    private String email;
    private String mobilePhoneNumber;
    private Boolean mobilePhoneNumberVerified;
    private String nickName;
    private Boolean emailVerified;
    private String username;
    private String accessToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean isMobilePhoneNumberVerified() {
        return mobilePhoneNumberVerified;
    }

    public void setMobilePhoneNumberVerified(Boolean mobilePhoneNumberVerified) {
        this.mobilePhoneNumberVerified = mobilePhoneNumberVerified;
    }

    public Boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSignture() {
        return signture;
    }

    public void setSignture(String signture) {
        this.signture = signture;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Boolean getMobilePhoneNumberVerified() {
        return mobilePhoneNumberVerified;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

}
