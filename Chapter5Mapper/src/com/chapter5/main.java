package com.chapter5;

import com.chapter5.entity.Role;
import com.chapter5.mapper.RoleMapper;
import com.chapter5.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class main {
    public static void main(String args[]){
        Logger log = Logger.getLogger(main.class);
        SqlSession sqlSession = null;
        try{
            sqlSession = SqlSessionFactoryUtils.openSqlSession();
            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
            Map<String,Object> parameterMap = new HashMap<>();
            parameterMap.put("roleName",1);
            parameterMap.put("note",1);
            List<Role> roles = roleMapper.findRolesByMap(parameterMap);
            for(Role role : roles){
                System.out.print(role.getId());
                System.out.print(role.getRoleName());
                System.out.println(role.getNote());
            }
        }finally {
            if(sqlSession!=null){
                sqlSession.close();
            }
        }
    }
}
