package com.houjun.mybatis;

import com.houjun.mybatis.domain.Menu;
import com.houjun.mybatis.mapper.MenuMapper;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author HouJun
 * @date 2021-12-30 17:23
 */
public class MetaObjectTest {
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
    public void test1() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Menu menu = new Menu();
        menu.setName("类目");
        Method getName = menu.getClass().getDeclaredMethod("getName");
        Object invoke = getName.invoke(menu);
        System.out.println(invoke);
        Configuration configuration = new Configuration();
        MetaObject metaObject = configuration.newMetaObject(menu);
        System.out.println(Arrays.toString(metaObject.getGetterNames()));
        System.out.println(metaObject.getValue("name"));
        System.out.println(metaObject.getValue("id"));
        metaObject.setValue("id",1000);
        System.out.println(metaObject.getValue("id"));
    }
}
