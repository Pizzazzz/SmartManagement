package com.example.smartmanagement.dto;

import java.util.Date;

/**
 * 利用状況DTO
 */
public class TUseStatusDto {

    // ロッカー番号
    private String lockerNo;
    // 端末番号
    private String deviceNo;
    // 利用ステータス
    private String useStatus;
    // 貸出ユーザーID
    private String loanUserId;
    // 貸出日時
    private Date loanDate;
    // 返却ユーザーID
    private String returnUserId;
    // 返却日時
    private Date returnDate;

    public String getLockerNo() { return lockerNo; }

    public void setLockerNo(String lockerNo) {
        this.lockerNo = lockerNo;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(String useStatus) {
        this.useStatus = useStatus;
    }

    public String getLoanUserId() {
        return loanUserId;
    }

    public void setLoanuserId(String loanUserId) {
        this.loanUserId = loanUserId;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        loanDate = loanDate;
    }

    public String getReturnUserId() {
        return returnUserId;
    }

    public void setReturnUserId(String returnUserId) {
        this.returnUserId = returnUserId;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

}
