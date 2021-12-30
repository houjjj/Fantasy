package com.houjun.mybatis;

import com.houjun.mybatis.domain.HR;
import com.houjun.mybatis.mapper.HRMapper;
import org.apache.ibatis.executor.*;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * @author HouJun
 * @date 2021-12-25 12:40
 */
public class ExecutorTest {
    Configuration configuration;
    SqlSessionFactory sqlSessionFactory;
    Transaction transaction;

    @Before
    public void init() throws SQLException {
        InputStream resource = ExecutorTest.class.getResourceAsStream("/mybatis-config.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resource);
        configuration = sqlSessionFactory.getConfiguration();
        DataSource dataSource = configuration.getEnvironment().getDataSource();
        Connection connection = dataSource.getConnection();
        transaction = new JdbcTransactionFactory().newTransaction(connection);
    }

    @Test
    public void simpleExecutorTest() throws SQLException {
        MappedStatement ms = configuration.getMappedStatement("com.houjun.mybatis.mapper.HRMapper.selectById");
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration, transaction);
        BoundSql boundSql = ms.getBoundSql(11);
        List<Object> objects = simpleExecutor.doQuery(ms, 11, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, boundSql);
        List<Object> object1s = simpleExecutor.doQuery(ms, 11, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, boundSql);
        System.out.println(objects.get(0));
    }

    @Test
    public void reuseExecutorTest() throws SQLException {
        MappedStatement ms = configuration.getMappedStatement("com.houjun.mybatis.mapper.HRMapper.selectById");
        ReuseExecutor reuseExecutor = new ReuseExecutor(configuration, transaction);
        BoundSql boundSql = ms.getBoundSql(11);
        List<Object> objects = reuseExecutor.doQuery(ms, 11, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, boundSql);
        List<Object> object1s = reuseExecutor.doQuery(ms, 11, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, boundSql);
        System.out.println(objects.get(0));
    }

    // 批处理执行器
    // 只针对修改操作
    // 批处理必须手动提交事务
    @Test
    public void batchExecutorTest() throws SQLException {
        MappedStatement ms = configuration.getMappedStatement("com.houjun.mybatis.mapper.HRMapper.updateName");
        BatchExecutor batchExecutor = new BatchExecutor(configuration, transaction);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("id", 11);
        map.put("name", "张三1");
        batchExecutor.doUpdate(ms, map);
        batchExecutor.doUpdate(ms, map);
        batchExecutor.flushStatements();
//        batchExecutor.commit(true);
    }

    // base执行器
    @Test
    public void baseExecutorTest() throws SQLException {
        MappedStatement ms = configuration.getMappedStatement("com.houjun.mybatis.mapper.HRMapper.selectById");
        Executor baseExecutor = new ReuseExecutor(configuration, transaction);
        List<Object> query = baseExecutor.query(ms, 10, RowBounds.DEFAULT, ReuseExecutor.NO_RESULT_HANDLER);
        List<Object> query1 = baseExecutor.query(ms, 10, RowBounds.DEFAULT, ReuseExecutor.NO_RESULT_HANDLER);
        System.out.println(query.get(0));
    }

    // caching执行器
    @Test
    public void cachingExecutorTest() throws SQLException {
        MappedStatement ms = configuration.getMappedStatement("com.houjun.mybatis.mapper.HRMapper.selectById");
        Executor baseExecutor = new ReuseExecutor(configuration, transaction);
        CachingExecutor cachingExecutor = new CachingExecutor(baseExecutor);
        List<Object> query = cachingExecutor.query(ms, 10, RowBounds.DEFAULT, ReuseExecutor.NO_RESULT_HANDLER);
        cachingExecutor.commit(true);
        List<Object> query1 = cachingExecutor.query(ms, 10, RowBounds.DEFAULT, ReuseExecutor.NO_RESULT_HANDLER);
        List<Object> query2 = cachingExecutor.query(ms, 10, RowBounds.DEFAULT, ReuseExecutor.NO_RESULT_HANDLER);
        List<Object> query3 = cachingExecutor.query(ms, 10, RowBounds.DEFAULT, ReuseExecutor.NO_RESULT_HANDLER);
        System.out.println(query.get(0));
    }

    @Test
    public void sqlSessionExecutorTest() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.REUSE, true);
//        HR o = sqlSession.selectOne("com.houjun.mybatis.mapper.HRMapper.selectById", 10);
        HRMapper mapper = sqlSession.getMapper(HRMapper.class);
        HR o1 = mapper.selectById(10);
        sqlSession.commit();
        if (o1 == null) {

        }
        HR o2 = mapper.selectById(10);
    }

    @Test
    public void mapperTest() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        HRMapper mapper = sqlSession.getMapper(HRMapper.class);
        mapper.selectByName("张三1");
        mapper.selectById(1);
        sqlSession.commit();
        mapper.selectById(1);
    }


}
