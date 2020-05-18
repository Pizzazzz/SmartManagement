package com.example.smartmanagement.dto;

import java.util.Date;

/**
 * 利用履歴DTO
 */
public class TUseHistoryDto {

    // 利用履歴ID
    private String useHistoryId;
    // ロッカー番号
    private String lockerNo;
    // 貸出ユーザー名
    private String loanUserName;
    // 貸出日時
    private Date loanDate;
    // 返却ユーザー名
    private String returnUserName;
    // 返却日時
    private String returnDate;

    public String getUseHistoryId() {
        return useHistoryId;
    }

    public void setUseHistoryId(String useHistoryId) {
        this.useHistoryId = useHistoryId;
    }

    public String getLockerNo() {
        return lockerNo;
    }

    public void setLockerNo(String lockerNo) {
        this.lockerNo = lockerNo;
    }

    public String getLoanUserName() {
        return loanUserName;
    }

    public void setLoanUserName(String loanUserName) {
        this.loanUserName = loanUserName;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public String getReturnUserName() {
        return returnUserName;
    }

    public void setReturnUserName(String returnUserName) {
        this.returnUserName = returnUserName;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

}
