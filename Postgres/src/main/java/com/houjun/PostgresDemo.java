package com.houjun;

import java.sql.*;

public class PostgresDemo {
    /**
     * CREATE DATABASE demo;
     * \c demo
     * CREATE TABLE users (
     *   id SERIAL PRIMARY KEY,
     *   name TEXT,
     *   age INT
     * );
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/demo";
        String user = "postgres";
        String password = "postgres";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            // 插入
            String insertSql = "INSERT INTO users(name, age) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, "Tom");
                ps.setInt(2, 25);
                ps.executeUpdate();
            }

            // 查询
            String querySql = "SELECT id, name, age FROM users";
            try (PreparedStatement ps = conn.prepareStatement(querySql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    System.out.println(
                            rs.getInt("id") + " "
                                    + rs.getString("name") + " "
                                    + rs.getInt("age")
                    );
                }
            }
        }
    }
}
