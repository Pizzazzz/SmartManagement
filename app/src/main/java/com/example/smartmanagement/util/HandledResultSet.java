package com.example.smartmanagement.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandledResultSet {

    /** 結果セットマップ */
    private static List<Map<String, Object>> rsList;

    /** カーソル */
    private static int count;

    /**
     * コンストラクタ
     * @param rs ResultSet
     * @throws SQLException
     */
    public HandledResultSet(ResultSet rs) throws SQLException {
        convert(rs);
        count = -1;
    }

    /**
     * カーソル移動
     * @return 次のレコードが存在すればtrue、なければfalse
     */
    public boolean next() {

        if (rsList.size() > count + 1) {
            count++;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 結果セットから、String型の項目値を取得
     * @param columName 項目名
     * @return 項目値
     */
    public String getString(String columName) {
        return (String) rsList.get(count).get(columName);
    }

    /**
     * ResultSetをMapへ変換する
     * @param rs ResultSet
     * @throws SQLException
     */
    private void convert(ResultSet rs) throws SQLException {

        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        rsList = new ArrayList<Map<String, Object>>();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<String, Object>(columns);
            for(int i = 1; i <= columns; i++){
                row.put(md.getColumnName(i), rs.getObject(i));
            }
            rsList.add(row);
        }
    }
}
