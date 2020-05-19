package com.example.smartmanagement.Logic;

import com.example.smartmanagement.dto.MUserDto;
import com.example.smartmanagement.util.BaseLogic;
import com.example.smartmanagement.util.HandledResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * 認証ロジッククラス
 */
public class AuthenticationLogic extends BaseLogic {

    /**
     * ユーザーマスタ取得(全件)
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<MUserDto> selectAllMUserData() throws SQLException, ClassNotFoundException {

        createConnection();
        StringBuilder sql = new StringBuilder();

        // SQL生成
        sql.append("select * from m_user");

        HandledResultSet rs = executeQuery(sql.toString());

        List<MUserDto> resultList = new ArrayList<MUserDto>();

        while (rs.next()){
            MUserDto mUser = new MUserDto();
            mUser.setUserId(rs.getString("USER_ID"));
            mUser.setAuthenticationId(rs.getString("AUTHENTICATION_ID"));
            mUser.setUserName(rs.getString("USER_NAME"));
            mUser.setUserKbn(rs.getString("USER_KBN"));
            resultList.add(mUser);
        }

        closeConnection();

        return resultList;
    }


    /**
     * 貸出/返却処理
     * @param transition 遷移先(貸出/返却)
     * @param lockerNo ロッカー番号
     * @return
     */
    public Boolean unlock(String transition, String lockerNo) {

        // Bluetoothモジュール接続処理

        // マイコンに下記情報を連携する(ロッカー番号,1(貸出))

        // ロッカーマスタ取得

        // テーブル更新(利用状況、ロッカーマスタ、利用履歴)


        return false;
    }
}