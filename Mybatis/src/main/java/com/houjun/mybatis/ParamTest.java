package com.houjun.mybatis;

import com.houjun.mybatis.domain.Menu;
import com.houjun.mybatis.mapper.MenuMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.sql.SQLException;

/**
 * @author HouJun
 * @date 2021-12-30 14:17
 */
public class ParamTest {
    private SqlSession sqlSession;

    @Before
    public void init() throws SQLException {
        InputStream resource = SecondCacheTest.class.getResourceAsStream("/mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resource);
        sqlSession = sqlSessionFactory.openSession();
    }

    @After
    public void close() {
        sqlSession.close();
    }

    @Test
    public void singleTest(){
        MenuMapper mapper = sqlSession.getMapper(MenuMapper.class);
        Menu menu = mapper.selectByName("备份恢复数据库");
    }
}
