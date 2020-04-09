package com.lagou.edu.utils;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Component;

import java.sql.SQLException;

/**
 * @author xsq
 * <p>
 * 事务管理器类：负责手动事务的开启、提交、回滚
 */
@Component
public class TransactionManager {
    @Autowired("connectionUtils")
    private ConnectionUtils connectionUtils;


    // 开启手动事务控制
    public void beginTransaction() throws SQLException {
        connectionUtils.getCurrentThreadConn().setAutoCommit(false);
    }


    // 提交事务
    public void commit() throws SQLException {
        connectionUtils.getCurrentThreadConn().commit();
    }


    // 回滚事务
    public void rollback() throws SQLException {
        connectionUtils.getCurrentThreadConn().rollback();
    }
}
