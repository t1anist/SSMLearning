package com.chapter8.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args={Connection.class,Integer.class}
        )
})
public class MyPlugin implements Interceptor {
    private Logger log = Logger.getLogger(MyPlugin.class);
    private Properties properties = null;

    /**
     * 插件方法，它将代替StatementHandler的prepare方法
     * @param invocation 入参
     * @return 返回预编译后的PreparedStatement
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //进行绑定
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        Object object = null;
        //分离代理对象链（由于目标类可能被多个拦截器【插件】拦截，从而形成多次代理，通过循环可以分离出最原始的目标类）
        while(metaStatementHandler.hasGetter("h")){
            object = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        statementHandler = (StatementHandler) object;
        String sql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        Long parameterObject = (Long) metaStatementHandler.getValue("delegate.boundSql.parameterObject");
        log.info("执行的SQL：【"+sql+"】");
        log.info("参数：【"+parameterObject+"】");
        log.info("before ......");
        //如果当前代理的是一个非代理对象，那么它就会调用真实拦截对象的方法；如果不是，那么他会调度下个插件代理对象的invoke方法
        Object obj = invocation.proceed();
        log.info("after.......");
        return obj;
    }

    /**
     * 生成代理对象
     * @param target 被拦截对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        //采用系统默认的方法Plugin.wrap生成
        return Plugin.wrap(target,this);
    }

    /**
     * 设置参数，Mybatis初始化时就会生成插件实例并调用这个方法
     * @param properties 配置参数
     */
    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
        log.info("dbType = "+this.properties.get("dbType"));
    }
}
