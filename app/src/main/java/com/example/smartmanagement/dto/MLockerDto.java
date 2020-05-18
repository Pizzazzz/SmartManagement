package com.example.smartmanagement.dto;

/**
 * ロッカーマスタDTO
 */
public class MLockerDto {

    // ロッカー番号
    private String lockerNo;
    // 端末番号
    private String deviceNo;
    // 利用不可フラグ
    private String notUseFlg;

    public String getLockerNo() {
        return lockerNo;
    }

    public void setLockerNo(String lockerNo) {
        this.lockerNo = lockerNo;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getNotUseFlg() {
        return notUseFlg;
    }

    public void setNotUseFlg(String notUseFlg) {
        this.notUseFlg = notUseFlg;
    }
}
