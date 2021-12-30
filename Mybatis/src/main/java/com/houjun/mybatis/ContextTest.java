package com.houjun.mybatis;

import com.houjun.mybatis.mapper.MenuMapper;
import org.apache.ibatis.session.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HouJun
 * @date 2021-12-30 14:17
 */
public class ContextTest {
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
    public void resultHandlerTest() {
        List<Object> list = new ArrayList<>();
        ResultHandler resultContext = resultContext1 -> {
            if (resultContext1.getResultCount() >= 15) {
                resultContext1.stop();
            }
            list.add(resultContext1.getResultObject());
        };

        sqlSession.select("com.houjun.mybatis.mapper.MenuMapper.selectList",resultContext);
        list.forEach(System.out::println);
    }
}
