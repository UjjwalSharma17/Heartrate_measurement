package com.example.hp.cameracv2;

public class User {

    private String mName;
    private String mUserId;
    private int mAge;
    private String mEmail;
    private String mPassword;

    User(String userId, String name, int age, String email, String password){
        mUserId = userId;
        mName = name;
        mAge = age;
        mEmail = email;
        mPassword = password;
    }

    public String getmName() {
        return mName;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmAge() {
        return mAge;
    }

    public void setmAge(int mAge) {
        this.mAge = mAge;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }
}
