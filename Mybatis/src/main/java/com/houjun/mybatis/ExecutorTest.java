package com.houjun.mybatis;

import com.houjun.mybatis.mapper.HRMapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

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
    public void simpleExecutorTest() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        HR o = sqlSession.selectOne("com.houjun.mybatis.mapper.HRMapper.selectById", 10);
        HRMapper mapper = sqlSession.getMapper(HRMapper.class);
        HR o1 = mapper.selectById(10);
        System.out.println(o == o1);
    }

    @Test
    public void mapperTest() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        HRMapper mapper = sqlSession.getMapper(HRMapper.class);
        HR o1 = mapper.selectById(10);
        System.out.println(o1);
    }
}
