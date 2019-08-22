package com.chapter8.plugin;

import com.chapter8.entity.PageParams;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Intercepts(
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class,Integer.class}
        )
)
public class PagePlugin implements Interceptor {
    /**
     * 插件默认参数，可配置默认值
     */
    private Integer defaultPage; //默认页码
    private Integer defaultPageSize;//默认每页条数
    private Boolean defaultUseFlag;//默认是否启用插件
    private Boolean defaultCheckFlag;//默认是否检测页码参数
    private Boolean defaultCleanOrderBy;//默认是否清除最后一个order by后面的语句

    /**
     * 生成代理对象
     * @param target 被代理对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target,this);
    }

    /**
     * 设置插件配置参数
     * @param properties 配置参数
     */
    @Override
    public void setProperties(Properties properties) {
        String strDefaultPage = properties.getProperty("default.page","1");
        String strDefaultPageSize = properties.getProperty("default.pageSize","50");
        String strDefaultUseFlag = properties.getProperty("default.useFlag","false");
        String strDefaultCheckFlag = properties.getProperty("default.checkFlag","false");
        String strDefaultCleanOrderBy = properties.getProperty("default.cleanOrderBy","false");
        this.defaultPage = Integer.parseInt(strDefaultPage);
        this.defaultPageSize = Integer.parseInt(strDefaultPageSize);
        this.defaultUseFlag = Boolean.parseBoolean(strDefaultUseFlag);
        this.defaultCheckFlag = Boolean.parseBoolean(strDefaultCheckFlag);
        this.defaultCleanOrderBy = Boolean.parseBoolean(strDefaultCleanOrderBy);
    }

    /**
     * 插件拦截方法
     * @param invocation 拦截对象
     * @return 返回拦截处理后对象
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) getUnProxyObject(invocation.getTarget());
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        String sql = (String)metaStatementHandler.getValue("delegate.boundSql.sql");
        MappedStatement mapperStatement = (MappedStatement)metaStatementHandler.getValue("delegate.mappedStatement");
        //不是select语句
        if(!CheckSelect(sql)){
            return invocation.proceed();
        }
        BoundSql boundSql = (BoundSql)metaStatementHandler.getValue("delegate.boundSql");
        Object parameterObject = boundSql.getParameterObject();
        PageParams pageParams = getPageParamsForParamObj(parameterObject);
        if(pageParams==null){
            return invocation.proceed();
        }
        //获取配置中是否启用分页功能
        Boolean useFlag = pageParams.getUseFlag()==null?this.defaultUseFlag:pageParams.getUseFlag();
        if(!useFlag) {//不使用分页插件
            return invocation.proceed();
        }
        //获取相关配置参数
        Integer pageNum = pageParams.getPage()==null?defaultPage:pageParams.getPage();
        Integer pageSize = pageParams.getPageSize()==null?defaultPageSize:pageParams.getPageSize();
        Boolean checkFlag = pageParams.getCheckFlag()==null?defaultCheckFlag:pageParams.getCheckFlag();
        Boolean cleanOrderBy = pageParams.getCleanOrderBy()==null?defaultCleanOrderBy:pageParams.getCleanOrderBy();
        //计算总条数
        int total = getTotal(invocation,metaStatementHandler,boundSql,cleanOrderBy);
        //回填总条数到分页参数
        pageParams.setTotal(total);
        //计算总页数
        int totalPage = total%pageSize ==0?total/pageSize:total/pageSize+1;
        //回填总页数到分页参数
        pageParams.setTotalPage(totalPage);
        //检查当前页码的有效性
        checkPage(checkFlag,pageNum,totalPage);
        //修改Sql
        return prepareSQL(invocation,metaStatementHandler,boundSql,pageNum,pageSize);


    }

    /**
     * 预编译改写后的SQL并设置分页参数
     * @param invocation 入参
     * @param metaStatementHandler MetaObject绑定的StatementHandler
     * @param boundSql boundSql对象
     * @param pageNum 当前页
     * @param pageSize 最大页面数量
     * @return 返回编译改写后的SQL
     * @throws InvocationTargetException 异常
     * @throws IllegalAccessException 异常
     */
    private Object prepareSQL(Invocation invocation, MetaObject metaStatementHandler, BoundSql boundSql, Integer pageNum, Integer pageSize) throws InvocationTargetException, IllegalAccessException, SQLException {
        //获取当前需要执行的SQL
        String sql = boundSql.getSql();
        String newSql = "select * from("+sql+") $_paging_table limit ?,?";
        //修改当前需要执行的SQL
        metaStatementHandler.setValue("delegate.boundSql.sql",newSql);
        //执行编译，相当于StatementHandler执行了prepared()方法，这个时候，就剩下两个分页参数没有设置
        Object statementObj = invocation.proceed();
        //设置两个分页参数
        this.preparePageDataParams((PreparedStatement) statementObj,pageNum,pageSize);
        return statementObj;
    }

    /**
     * 设置分页参数
     * @param statementObj 编译后的SQL，未设置分页参数
     * @param pageNum 当前页
     * @param pageSize 页面最大显示数量
     * @exception SQLException 异常
     */
    private void preparePageDataParams(PreparedStatement statementObj, Integer pageNum, Integer pageSize) throws SQLException {
        //获取需要设置的参数个数，由于参数是最后两个，因此很容易得到其位置
        int idx = statementObj.getParameterMetaData().getParameterCount();
        //最后两个是分页参数
        statementObj.setInt(idx-1,(pageNum-1)*pageSize); //开始行
        statementObj.setInt(idx,pageSize); //限制条数
    }

    /**
     * 检查当前页码的有效性
     * @param checkFlag 检测标志
     * @param pageNum 当前页码
     * @param totalPage 最大页码
     * @throws Throwable
     */
    private void checkPage(Boolean checkFlag, Integer pageNum, int totalPage) throws Throwable{
        if(checkFlag){
            if(pageNum>totalPage){
                throw new Exception("查询失败，查询页码【"+pageNum+"】大于总页数【"+totalPage+"】");
            }
        }
    }

    /**
     * 获取总条数
     * @param invocation 入参
     * @param metaStatementHandler statementHandler
     * @param boundSql sql
     * @param cleanOrderBy 是否清除order by子句
     * @return 查询总数
     * @throws SQLException 异常
     */
    private int getTotal(Invocation invocation, MetaObject metaStatementHandler, BoundSql boundSql, Boolean cleanOrderBy) throws SQLException {
        //获取当前mappedStatement
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        //配置对象
        Configuration cfg = mappedStatement.getConfiguration();
        //当前需要执行的SQL
        String sql = (String)metaStatementHandler.getValue("delegate.boundSql.sql");
        //去掉最后的order by语句
        if(cleanOrderBy){
            sql = this.cleanOrderByForSql(sql);
        }
        //改写为统计总数的SQL
        String countsql = "select count(*) as total from("+sql+") $_paging";
        //获取拦截方法参数，根据插件签名，知道是Connection对象
        Connection connection = (Connection) invocation.getArgs()[0];
        PreparedStatement ps = null;
        int total = 0;
        try{
            //预编译统计总数SQL
            ps = connection.prepareStatement(countsql);
            //构建统计总数BoundSql
            BoundSql countBoundSql = new BoundSql(cfg,countsql,boundSql.getParameterMappings(),boundSql.getParameterObject());
            //构建Mybatis的ParameterHandler用来设置总数sql的参数
            ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement,boundSql.getParameterObject(),countBoundSql);
            //设置总数SQL参数
            parameterHandler.setParameters(ps);
            //执行查询
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                total = rs.getInt("total");
            }
        }finally {
            if(ps!=null){
                ps.close();
            }
        }
        return total;
    }

    /**
     * 清除order by子句
     * @param sql sql语句
     * @return 处理后的sql语句
     */
    private String cleanOrderByForSql(String sql) {
        StringBuilder sb = new StringBuilder(sql);
        String newSql = sql.toLowerCase();
        //如果没有order语句则直接返回
        if(newSql.indexOf("order")==-1){
            return sql;
        }
        int idx = newSql.lastIndexOf("order");
        return newSql.substring(0,idx).toString();
    }

    /**
     * 分离分页参数
     * @param parameterObject 执行参数
     * @return 分页参数
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private PageParams getPageParamsForParamObj(Object parameterObject) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        PageParams pageParams = null;
        if(parameterObject==null){
            return null;
        }
        //处理map参数，多个匿名参数和@Param注解参数，都是map
        if(parameterObject instanceof Map){
            @SuppressWarnings("unchecked")
            Map<String,Object> paramMap = (Map<String, Object>)parameterObject;
            Set<String> keySet = paramMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                Object value = paramMap.get(key);
                if(value instanceof PageParams){
                    return (PageParams)value;
                }
            }
        }else if(parameterObject instanceof PageParams){
            return (PageParams)parameterObject;
        }else{//从POJO属性尝试读取分页参数
            Field[] fields = parameterObject.getClass().getDeclaredFields();
            //尝试从POJO中获得类型为PageParams的属性
            for (Field field: fields){
                if(field.getType()==PageParams.class){
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(),parameterObject.getClass());
                    Method method = pd.getReadMethod();
                    return (PageParams) method.invoke(parameterObject);
                }
            }
        }
        return pageParams;
    }

    /**
     * 判断是否为查询语句
     * @param sql sql
     * @return 是否为查询语句
     */
    private boolean CheckSelect(String sql) {
        String trimSql  = sql.trim();
        int idx = trimSql.toLowerCase().indexOf("select");
        return idx==0;
    }

    /**
     * 从代理对象中分离出真实对象
     * @param target 入参
     * @return 非代理StatementHandler对象
     */
    private Object getUnProxyObject(Object target){
        MetaObject metaStatementHandler = SystemMetaObject.forObject(target);
        Object object = null;
        while(metaStatementHandler.hasGetter("h")){
            object = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        if(object == null){
            return target;
        }
        return object;
    }
}
