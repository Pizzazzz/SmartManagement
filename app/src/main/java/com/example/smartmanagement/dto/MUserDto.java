package com.example.smartmanagement.dto;

/**
 * ユーザマスタDTO
 */
public class MUserDto {

    // ユーザーID
    private String userId;
    // 認証ID
    private String authenticationId;
    // ユーザー名
    private String userName;
    // ユーザー区分
    private String userKbn;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(String authenticationId) {
        this.authenticationId = authenticationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserKbn() {
        return userKbn;
    }

    public void setUserKbn(String userKbn) {
        this.userKbn = userKbn;
    }

}
