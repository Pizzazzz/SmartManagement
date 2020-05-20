package com.example.smartmanagement.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Mapping {

    /** パラメタリスト */
    private final ArrayList<Object> paramList = new ArrayList<>();

    /**
     * パラメタ削除
     */
    private void clearParams() {
        paramList.clear();
    }

    /**
     * パラメタ追加
     * @param param パラメタ
     */
    public void setArgment(Object param) {
        paramList.add(param);
    }

    /**
     * PreparedStatementに格納したパラメタを設定します
     * @param statement PreparedStatement
     * @throws SQLException
     */
    public void setPreparedStatementParameters(PreparedStatement statement) throws SQLException {

        for (int i = 0; i < paramList.size(); i++) {
            Object param = paramList.get(i);

             if(param instanceof String) {
                String value = (String) param;
                statement.setString(i + 1, value);

            }  else {
                clearParams();
            }
        }
    }






}
