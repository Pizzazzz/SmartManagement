package com.example.smartmanagement.util;

import com.mysql.jdbc.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseLogic {

    /** DBコネクション */
    protected Connection conn;

    /**
     * DBコネクションを取得します。
     * @return DBコネクション
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    protected void createConnection() throws SQLException, ClassNotFoundException {

        // JDBCドライバを読み込み
        Class.forName(CommonConst.JDBC_DRIVER);
        // データベース接続
        conn = (Connection) DriverManager.getConnection(CommonConst.DB_URL, CommonConst.DB_USER, CommonConst.DB_PASS);
    }

    /**
     * トランザクションを開始します
     * @param commitFlg コミットフラグ
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    protected void startTransaction(boolean commitFlg) throws SQLException, ClassNotFoundException {
        createConnection();
        conn.setAutoCommit(commitFlg);
    }

    /**
     * トランザクションを終了します
     * @param commitFlg コミットフラグ
     * @throws SQLException
     */
    protected void endTransaction(boolean commitFlg) throws SQLException {

        try {
            if (commitFlg) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } finally {
            closeConnection();
        }
    }


    /**
     * DB接続を終了します。
     * @throws SQLException
     */
    protected void closeConnection() throws SQLException {

        if (conn != null) {
            conn.close();
        }
    }

    /**
     * executeQueryラッパー
     * @param sql SQL
     * @return 結果
     * @throws SQLException
     */
    protected HandledResultSet executeQuery(String sql) throws SQLException {

//        PreparedStatement pStmt = null;
        Statement pStmt = null;

        ResultSet rs = null;
        try {

//            pStmt = conn.prepareStatement(sql);
            pStmt = conn.createStatement();
            rs = pStmt.executeQuery(sql);
            HandledResultSet handledResultSet = new HandledResultSet(rs);
            return handledResultSet;

        } catch (SQLException e) {

            if (rs != null) {
                rs.close();
            }
            if (pStmt != null) {
                pStmt.close();
            }
            if (conn != null) {
                conn.close();
            }
            throw e;
        } finally {

            if (rs != null) {
                rs.close();
            }
            if (pStmt != null) {
                pStmt.close();
            }
        }

    }

    /**
     * executeUpdateラッパー
     * @param sql SQL
     * @return 更新・登録件数
     * @throws SQLException
     */
    protected int executeUpdate(String sql, Mapping mapping) throws SQLException {

        PreparedStatement pStmt = null;
        try {
            pStmt = conn.prepareStatement(sql);
            mapping.setPreparedStatementParameters(pStmt);
            return pStmt.executeUpdate();

        } catch (SQLException e) {

            if (pStmt != null) {
                pStmt.close();
            }
            if (conn != null) {

                conn.rollback();
                conn.close();
            }
            throw e;
        } finally {

            if (pStmt != null) {
                pStmt.close();
            }
        }
    }
}
