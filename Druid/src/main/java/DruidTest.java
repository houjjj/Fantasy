import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DruidTest {
    public static void main(String[] args) {
        try {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl("jdbc:mysql://192.168.12.76:30138/shindb?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8");
            dataSource.setUsername("root");
            dataSource.setPassword("Hello123");

            // 开启 testWhileIdle 参数
             dataSource.setTestWhileIdle(true);
            // 开启 keepalive 参数,不开启的话，池中的空闲连接会被清理，哪怕minIdle=1
            dataSource.setKeepAlive(true);
            // 设置检测时间为 10s
            dataSource.setTimeBetweenEvictionRunsMillis(10 * 1000);
            dataSource.setMinEvictableIdleTimeMillis(120000);
            dataSource.setValidationQuery("select 'x'");
            dataSource.setMinIdle(1);
            dataSource.setMaxActive(10);

            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("select 1");
            ps.close();
            conn.close();

            // 第一次结果
            System.out.println(dataSource.dump());
            Thread.sleep(150000);
            // 第二次结果
            System.out.println(dataSource.dump());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
