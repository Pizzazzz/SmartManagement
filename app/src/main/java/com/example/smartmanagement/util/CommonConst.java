package com.example.smartmanagement.util;

import java.util.UUID;

public class CommonConst {

    /** JDBCドライバ */
    public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    /** DBのURL */
    public static final String DB_URL = "jdbc:mysql://smartmanagement.cpuwj0tht08a.ap-northeast-1.rds.amazonaws.com/smart_management_db";

    /** DBユーザ */
    public static final String DB_USER = "admin";

    /** DBパスワード */
    public static final String DB_PASS = "+admin123";

    /**TODO Android Device Name(サーバーとなる側のデバイス名は未決) */
    public static final String BT_DEVICE = "Nexus 8";

    /**TODO UUID (ソフトウェア上でオブジェクトを一意に識別するための識別子 未決) */
    public static final UUID BT_UUID = UUID.fromString("41eb5f39-6c3a-4067-8bb9-bad64e6e0908");
}
