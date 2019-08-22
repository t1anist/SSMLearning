package com.chapter8;

import com.chapter8.entity.PageParams;
import com.chapter8.entity.Role;
import com.chapter8.mapper.RoleMapper;
import com.chapter8.plugin.PagePlugin;
import com.chapter8.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import java.util.List;

public class Main {

    public static void main(String args[]){
        Logger log = Logger.getLogger(Main.class);
        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionFactoryUtils.openSqlSession();
            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
            PageParams pageParams = new PageParams();
            pageParams.setPageSize(10);
            List<Role> roleList = roleMapper.findRoles(pageParams, "");
            log.info(roleList.size());
        }catch (Exception ex){
            ex.printStackTrace();
            sqlSession.rollback();
        }finally {
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
    }
}
