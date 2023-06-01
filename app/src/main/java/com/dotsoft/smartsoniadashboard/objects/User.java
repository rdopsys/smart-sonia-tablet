package com.dotsoft.smartsoniadashboard.objects;

public class User {

    private String userName, login, userId, accessToken,role;

    public User(String userName, String userId, String accessToken,
                String login,String role){
        this.userName = userName;
        this.login = login;
        this.userId = userId;
        this.accessToken = accessToken;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRole() { return role; }

    public boolean isLogin() {
        return login.equals("IN");
    }

}
