package com.chapter3;

import com.chapter3.entity.Role;
import com.chapter3.mapper.RoleMapper;
import com.chapter3.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class Main {

    public static void main(String args[]){
        Logger log = Logger.getLogger(Main.class);
        SqlSession sqlSession = null;
        try{
            sqlSession = SqlSessionFactoryUtils.openSqlSession();
            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
            Role role = roleMapper.getRole(1L);
            log.info(role.getRoleName());
        }finally {
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
    }
}
