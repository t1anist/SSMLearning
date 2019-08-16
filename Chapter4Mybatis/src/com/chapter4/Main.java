package com.chapter4;

import com.chapter4.entity.User;
import com.chapter4.mapper.UserMapper;
import com.chapter4.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class Main {

    public static void main(String args[]){
        Logger log = Logger.getLogger(Main.class);
        SqlSession sqlSession = null;
        try{    
            sqlSession = SqlSessionFactoryUtils.openSqlSession();
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.getUser(1L);
            log.info(user.getSex().getName());
        }finally {
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
    }
}
