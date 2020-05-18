package com.example.smartmanagement.dto;

/**
 * 端末マスタDTO
 */
public class MDeviceDto {

    // 端末番号
    private String deviceNo;
    // 端末名
    private String deviceName;
    // 利用不可フラグ
    private String notUseFlg;

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getNotUseFlg() {
        return notUseFlg;
    }

    public void setNotUseFlg(String notUseFlg) {
        this.notUseFlg = notUseFlg;
    }

}
