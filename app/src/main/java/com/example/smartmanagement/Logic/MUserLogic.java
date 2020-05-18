package com.example.smartmanagement.Logic;

import com.example.smartmanagement.dto.MUserDto;
import com.example.smartmanagement.util.BaseLogic;
import com.example.smartmanagement.util.HandledResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MUserLogic extends BaseLogic {

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
}
