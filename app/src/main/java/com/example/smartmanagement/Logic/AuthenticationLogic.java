package com.example.smartmanagement.Logic;

import com.example.smartmanagement.dto.MUserDto;
import com.example.smartmanagement.util.BaseLogic;
import com.example.smartmanagement.util.HandledResultSet;
import com.example.smartmanagement.util.Mapping;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * 認証ロジッククラス
 */
public class AuthenticationLogic extends BaseLogic {

    /**
     * テーブル更新処理
     * @param transition 遷移先(貸出/返却)
     * @param lockerNo ロッカー番号
     * @param deviceNo 端末番号
     * @param userId ユーザーID
     * @param userName ユーザー名
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void execute(String transition, String lockerNo, String deviceNo, String userId, String userName) throws SQLException, ClassNotFoundException {

        // コミットフラグ
        boolean commitFlg = false;
        startTransaction(commitFlg);

        // 利用状況登録、又は更新件数
        int numOfRegTUseStatus = 0;
        // ロッカーマスタ更新件数
        int numOfMLocker = 0;
        // 利用履歴登録、又は更新件数
        int numOfTuseHistory = 0;

        if (transition.equals("貸出")) {
            // 貸出の場合、レコード追加
            // 利用状況登録
            numOfRegTUseStatus = insertTUseStatus(lockerNo, deviceNo, userId);
            // 利用履歴登録
            numOfTuseHistory = insertTUseHistory(lockerNo, userName);
        } else if (transition.equals("返却")) {
            // 返却の場合、レコード更新
            // 利用状況更新
            numOfRegTUseStatus = updateTUseStatus(lockerNo, deviceNo, userId);
            // 利用履歴更新
            numOfTuseHistory = updateTUseHistory(lockerNo, userName);
        }

        // ロッカーマスタ更新
        numOfMLocker = updateMLocker(transition, lockerNo, deviceNo);

        // 利用状況、利用履歴、ロッカーマスタの登録/更新件数がそれぞれ1件の場合コミット
        if (numOfRegTUseStatus == 1 && numOfTuseHistory == 1 && numOfTuseHistory == 1) {
            commitFlg = true;
        }

        endTransaction(commitFlg);

    }

    /**
     * ユーザーマスタ取得(全件)
     * @return ユーザーマスタ(全件)
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<MUserDto> selectAllMUserData() throws SQLException, ClassNotFoundException {

        StringBuilder sql = new StringBuilder();

        // SQL生成
        sql.append("SELECT * FROM m_user");

        createConnection();
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
     * 利用状況登録
     * @param lockerNo ロッカー番号
     * @param deviceNo 端末番号
     * @param userId ユーザーID
     * @return 登録件数
     * @throws SQLException
     */
    private int insertTUseStatus(String lockerNo, String deviceNo, String userId) throws SQLException {

        StringBuilder sql = new StringBuilder();

        // SQL生成
        sql.append("INSERT INTO t_use_status (  ");
        sql.append("  LOCKER_NO                 ");
        sql.append(" ,DEVICE_NO                 ");
        sql.append(" ,USE_STATUS                ");
        sql.append(" ,LOAN_USER_ID              ");
        sql.append(" ,LOAN_DATE                 ");
        sql.append(" ,RETURN_USER_ID            ");
        sql.append(" ,RETURN_DATE )             ");
        sql.append(" VALUES (?, ?, 0, ?, CURRENT_TIMESTAMP, NULL, NULL)    ");

        Mapping mapping = new Mapping();
        mapping.setArgment(lockerNo);
        mapping.setArgment(deviceNo);
        mapping.setArgment(userId);

        return executeUpdate(sql.toString(), mapping);

    }

    /**
     * 利用状況更新
     * @param lockerNo ロッカー番号
     * @param deviceNo 端末番号
     * @param userId ユーザーID
     * @return 更新件数
     * @throws SQLException
     */
    private int updateTUseStatus(String lockerNo, String deviceNo ,String userId) throws SQLException {

        StringBuilder sql = new StringBuilder();

        // SQL生成
        sql.append("UPDATE t_use_status         ");
        sql.append("SET                         ");
        sql.append("  USE_STATUS = 1            ");
        sql.append(" ,RETURN_USER_ID = ?        ");
        sql.append(" ,RETURN_DATE = CURRENT_TIMESTAMP  ");
        sql.append("WHERE                              ");
        sql.append("  LOCKER_NO = ?                    ");
        sql.append("  AND DEVICE_NO = ?                ");
        sql.append("  AND LOAN_USER_ID = ?             ");
        sql.append("  AND RETURN_USER_ID = NULL        ");

        Mapping mapping = new Mapping();
        mapping.setArgment(userId);
        mapping.setArgment(lockerNo);
        mapping.setArgment(deviceNo);
        mapping.setArgment(userId);

        return executeUpdate(sql.toString(), mapping);
    }

    /**
     * ロッカーマスタ更新
     * @param transition 遷移先(貸出 or 返却)
     * @param lockerNo ロッカー番号
     * @param deviceNo 端末番号
     * @return ロッカーマスタ更新件数
     * @throws SQLException
     */
    private int updateMLocker(String transition, String lockerNo, String deviceNo) throws SQLException {

        StringBuilder sql = new StringBuilder();

        String notUseFlg = transition.equals("貸出")? "1": "0";

        // SQL生成
        sql.append("UPDATE m_locker　　　         ");
        sql.append("SET                         ");
        sql.append("  NOT_USE_FLG = ?           ");
        sql.append("WHERE                       ");
        sql.append("  LOCKER_NO = ?             ");
        sql.append("  AND DEVICE_NO = ?         ");

        Mapping mapping = new Mapping();
        mapping.setArgment(notUseFlg);
        mapping.setArgment(lockerNo);
        mapping.setArgment(deviceNo);

        return executeUpdate(sql.toString(), mapping);
    }

    /**
     * 利用履歴更新
     * @param lockerNo ロッカー番号
     * @param loanUserName 貸出ユーザー名
     * @return 利用履歴登録件数
     * @throws SQLException
     */
    private int insertTUseHistory(String lockerNo, String loanUserName) throws SQLException {

        StringBuilder sql = new StringBuilder();

        // SQL生成
        sql.append("INSERT INTO t_use_history ( ");
        sql.append("  USE_HISTORY_ID            ");
        sql.append(" ,LOCKER_NO                 ");
        sql.append(" ,LOAN_USER_NAME            ");
        sql.append(" ,LOAN_DATE            　　　 ");
        sql.append(" ,RETURN_USER_NAME　　        ");
        sql.append(" ,RETURN_DATE )              ");
        sql.append(" VALUES (?, ?, ?, CURRENT_TIMESTAMP, NULL, NULL)    ");

        Mapping mapping = new Mapping();
        // TODO 利用履歴IDに登録する値が不明(下記は仮の値)
        mapping.setArgment(UUID.randomUUID().toString());
        mapping.setArgment(lockerNo);
        mapping.setArgment(loanUserName);

        return executeUpdate(sql.toString(), mapping);

    }

    /**
     * 利用履歴更新
     * @param lockerNo ロッカー番号
     * @param userName (貸出/返却)ユーザー名
     * @return 利用履歴更新件数
     * @throws SQLException
     */
    private int updateTUseHistory(String lockerNo, String userName) throws SQLException {

        StringBuilder sql = new StringBuilder();

        // SQL生成
        sql.append("UPDATE t_use_history              ");
        sql.append("SET                               ");
        sql.append("  RETURN_USER_NAME = ?            ");
        sql.append(" ,RETURN_DATE = CURRENT_TIMESTAMP  ");
        sql.append("WHERE                              ");
        sql.append("  LOCKER_NO = ?                    ");
        sql.append("  AND LOAN_USER_ID = ?             ");
        sql.append("  AND RETURN_USER_NAME = NULL      ");

        Mapping mapping = new Mapping();
        mapping.setArgment(userName);
        mapping.setArgment(lockerNo);
        mapping.setArgment(userName);

        return executeUpdate(sql.toString(), mapping);
    }
}
