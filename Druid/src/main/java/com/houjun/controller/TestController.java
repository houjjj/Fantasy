package com.houjun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.*;

@RestController
public class TestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/test")
    public String test() throws SQLException {
        return "Database: " + dataSource.getConnection().getMetaData().getDatabaseProductName();
    }

    @GetMapping("/query")
    public String query(@RequestParam("num") int num) throws SQLException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < num; i++) {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
//            String sql = "select SQL_NO_CACHE  thread_id,user,current_allocated from sys.memory_by_thread_by_current_bytes;";
            String sql = "select SQL_NO_CACHE * from nsf_nacos.config_info";
            ResultSet resultSet = statement.executeQuery(sql);
            // 3. 处理结果集
            while (resultSet.next()) {
                int id = resultSet.getInt("thread_id");
                String name = resultSet.getString("user");
                String email = resultSet.getString("current_allocated");

                result.append(id);
                System.out.printf("thread_id: %d, user: %s, current_allocated: %s%n", id, name, email);
            }

        }
        return result.toString();
    }
}