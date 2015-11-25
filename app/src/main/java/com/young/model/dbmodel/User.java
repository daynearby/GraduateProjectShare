package com.young.model.dbmodel;

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
    private boolean mobilePhoneNumberVerified;
    private String nickName;
    private boolean emailVerified;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isMobilePhoneNumberVerified() {
        return mobilePhoneNumberVerified;
    }

    public void setMobilePhoneNumberVerified(boolean mobilePhoneNumberVerified) {
        this.mobilePhoneNumberVerified = mobilePhoneNumberVerified;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
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

}
