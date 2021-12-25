package com.houjun.mybatis;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

/**
 * @author HouJun
 * @date 2021-12-25 12:40
 */
public class ExecutorTest {
    Configuration configuration;
    SqlSessionFactory sqlSessionFactory;

    @Before
    public void init() {
        InputStream resource = ExecutorTest.class.getResourceAsStream("/mybatis-config.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resource);
        configuration = sqlSessionFactory.getConfiguration();
    }

    @Test
    public void simpleExecutorTest(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        HR o = sqlSession.selectOne("com.houjun.mybatis.mapper.HRMapper.selectById",10);
        System.out.println(o);
    }
}
