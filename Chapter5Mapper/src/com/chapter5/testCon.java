package com.chapter5;

import com.chapter5.entity.Employee;
import com.chapter5.mapper.EmployeeMapper;
import com.chapter5.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class testCon {
    public static void main(String args[]){
        SqlSession sqlSession = null;
        try{
            Logger logger = Logger.getLogger(testCon.class);
            sqlSession = SqlSessionFactoryUtils.openSqlSession();
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.getEmployee(1L);
            logger.info(employee.getBirthday());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(sqlSession!=null) sqlSession.close();
        }
    }
}
