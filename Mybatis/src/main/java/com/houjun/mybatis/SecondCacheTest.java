package com.houjun.mybatis;

import com.houjun.mybatis.domain.Menu;
import com.houjun.mybatis.mapper.MenuMapper;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author HouJun
 * @date 2021-12-25 12:40
 */
public class SecondCacheTest {
    Configuration configuration;
    SqlSessionFactory sqlSessionFactory;
    Transaction transaction;

    @Before
    public void init() throws SQLException {
        InputStream resource = SecondCacheTest.class.getResourceAsStream("/mybatis-config.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resource);
        configuration = sqlSessionFactory.getConfiguration();
        DataSource dataSource = configuration.getEnvironment().getDataSource();
        Connection connection = dataSource.getConnection();
        transaction = new JdbcTransactionFactory().newTransaction(connection);
    }

    @Test
    public void cacheTest(){
        Cache cache = configuration.getCache("com.houjun.mybatis.mapper.HR2Mapper");
        String nation = "nation";
        cache.putObject(nation,"china");
        System.out.println(cache.getObject(nation));
    }
    @Test
    public void cacheTest2(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        MenuMapper mapper = sqlSession.getMapper(MenuMapper.class);
        mapper.selectByName("员工资料");
        sqlSession.commit();
        mapper.selectByName("员工资料");
        mapper.selectByName("员工资料");
//        SqlSession sqlSession1 = sqlSessionFactory.openSession();
//        HR2Mapper mapper1 = sqlSession1.getMapper(HR2Mapper.class);
//        mapper1.selectByName("张三1");
//        mapper1.selectByName("张三1");
//        mapper1.selectByName("张三1");
    }
    @Test
    public void cacheTest3(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        MenuMapper mapper = sqlSession.getMapper(MenuMapper.class);
        mapper.selectByName("初始化数据库");
        sqlSession.commit();

        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        MenuMapper mapper1 = sqlSession1.getMapper(MenuMapper.class);
        mapper1.selectById(1);
    }

    @Test
    public void cacheTest4(){
        HashMap<Object, Object> map = new HashMap<>();
        CacheKey cacheky = new CacheKey();
        CacheKey cacheky2 = new CacheKey(new Object[1]);
        map.put(cacheky, Collections.singleton("1"));
        map.put(cacheky2, Collections.singleton("2"));
    }

    @Test
    public void cacheTest6(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        MenuMapper mapper = sqlSession.getMapper(MenuMapper.class);
        Menu initDatabase = mapper.selectByName("初始化数据库");
         mapper.update(initDatabase.getId(),"初始化");
        sqlSession.commit();
        Menu initDatabase2 = mapper.selectByName("初始化数据库");
        System.out.println(initDatabase2);
    }

}
