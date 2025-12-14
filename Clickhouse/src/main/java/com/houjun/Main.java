package com.houjun;

import com.clickhouse.jdbc.ClickHouseDataSource;
import com.clickhouse.data.ClickHouseOutputStream;
import com.clickhouse.data.ClickHouseWriter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private final static String YOUR_INSTANCE_PROTOCOL = "http";
    private final static String YOUR_INSTANCE_ENDPOINT = "127.0.0.1:8123"; // YOUR CONFIG HERE
    private final static String DATABASE = "default"; // YOUR CONFIG HERE
    private final static String YOUR_INSTANCE_USER = "default"; // YOUR CONFIG HERE
    private final static String YOUR_INSTANCE_PASSWORD = ""; // YOUR CONFIG HERE
    private final static String JDBC_URL = "jdbc:clickhouse:%s://%s/%s";
    private final static Integer INSERT_BATCH_SIZE = 10000;
    private final static Integer INSERT_BATCH_NUM = 10;
    private final static boolean ENTERPRISE = true; // YOUR CONFIG HERE
    // 对于clickhouse-jdbc 0.8.0 之前是v1 driver，之后是v2 driver
    // 对于clickhouse-jdbc 0.7.2版本支持等级2、3，但是查询操作不支持（配置compress_algorithm=none后等级1和查询正常），最佳实践
    // 对于clickhouse-jdbc 0.9.4版本支持等级1
    private final static Integer INSERT_OPTIMIZE_LEVEL = 3;

    public static void main(String[] args) {
        try {
            HikariConfig conf = buildHikariDataSource();
            try(HikariDataSource ds = new HikariDataSource(conf)) {
                // create table
                Connection conn = ds.getConnection();
                createTable(conn);
                conn.close();

                // concurrently insert data
                int concurrentNum = 5;
                CountDownLatch countDownLatch = new CountDownLatch(concurrentNum);
                ExecutorService executorService = Executors.newFixedThreadPool(concurrentNum);
                for (int i = 0; i < concurrentNum; i++) {
                    executorService.submit(() -> {
                        System.out.printf("[%d] Thread start inserting\n", Thread.currentThread().getId());
                        try(Connection connection = ds.getConnection()) {
                            batchInsert(connection, INSERT_OPTIMIZE_LEVEL);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            System.out.printf("[%d] Thread stop inserting\n", Thread.currentThread().getId());
                            countDownLatch.countDown();
                        }
                    });
                }
                // wait for all threads to finish
                countDownLatch.await();

                // count table
                conn = ds.getConnection();
                count(conn);
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * generate JDBC URL
     * @param protocol support http, https, grpc
     * @param endpoint endpoint
     * @return JDBC URL
     */
    public static String getJdbcUrl(String protocol, String endpoint, String database) {
        return String.format(JDBC_URL, protocol, endpoint, database);
    }

    /**
     * build HikariDataSource
     * @return HikariConfig
     */
    public static HikariConfig buildHikariDataSource() throws Exception {
        HikariConfig conf = new HikariConfig();

        // property
        Properties properties = new Properties();
        /// socket keepalive
        properties.setProperty("socket_keepalive", "true");
//        properties.setProperty("http_connection_provider", "APACHE_HTTP_CLIENT");
        /// socket timeout
        properties.setProperty("socket_timeout", "300000");
        /// timezone
        properties.setProperty("use_server_time_zone", "true");
        properties.setProperty("compress_algorithm", "none");// 至关重要 https://github.com/ClickHouse/clickhouse-java/issues/1449

        // datasource config
        conf.setDataSource(new ClickHouseDataSource(getJdbcUrl(YOUR_INSTANCE_PROTOCOL, YOUR_INSTANCE_ENDPOINT, DATABASE), properties));
        conf.setUsername(YOUR_INSTANCE_USER);
        conf.setPassword(YOUR_INSTANCE_PASSWORD);

        // connection pool config
        conf.setMaximumPoolSize(10);
        conf.setMinimumIdle(5);
        conf.setIdleTimeout(30000);
        conf.setMaxLifetime(60000);
        conf.setConnectionTimeout(30000);
        conf.setPoolName("HikariPool");

        return conf;
    }

    /**
     * create table
     * @param conn ClickHouse connection
     * @throws Exception
     */
    public static void createTable(Connection conn) throws Exception {
        try(Statement statement = conn.createStatement()) {
            if (ENTERPRISE) {
                statement.execute("CREATE TABLE IF NOT EXISTS `default`.`test`   (id Int64, name String) ENGINE = MergeTree() ORDER BY id;");
            } else {
                // create local table
                statement.execute("CREATE TABLE IF NOT EXISTS `default`.`test_local` ON CLUSTER default (id Int64, name String) ENGINE = MergeTree() ORDER BY id;");
                // create distributed table
                statement.execute("CREATE TABLE IF NOT EXISTS `default`.`test` ON CLUSTER default (id Int64, name String) ENGINE = Distributed(default, default, test_local, rand());");
            }
        }
    }

    /**
     * batch insert
     * @param conn ClickHouse connection
     * @param optimizeLevel insert optimize level, 3 is faster than 2, 2 is faster than 1<br/>
     *                      1: insert into `default`.`test` (id, name) values(?, ?) -- with additional query for getting table structure.
     *                         It's portable.<br/>
     *                      2: insert into `default`.`test` select id, name from input('id Int64, name String') -- effectively convert and insert data sent to the server
     *                         with given structure to the table with another structure. It's NOT portable(as it's limited to ClickHouse).<br/>
     *                      3: insert into `default`.`test` format RowBinary -- fastest(close to Java client) with streaming mode but requires manual serialization and it's
     *                         NOT portable(as it's limited to ClickHouse).
     * @throws Exception
     */
    public static void batchInsert(Connection conn, int optimizeLevel) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            // prepared statement
            switch (optimizeLevel) {
                case 1:
                    preparedStatement = conn.prepareStatement("insert into `default`.`test` (id, name) values(?, ?)");
                    break;
                case 2:
                    preparedStatement = conn.prepareStatement("insert into `default`.`test` select id, name from input('id Int64, name String')");
                    break;
                case 3:
                    preparedStatement = conn.prepareStatement("insert into `default`.`test` format RowBinary");
                    break;
                default:
                    throw new IllegalArgumentException("optimizeLevel must be 1, 2 or 3");
            }

            // insert data
            long randBase = (long) (Math.random() * 1000000); // random number, prevent data duplicate and lost
            for (int i = 0; i < INSERT_BATCH_NUM; i++) {
                long insertStartTime = System.currentTimeMillis();
                switch (optimizeLevel) {
                    case 1:
                    case 2:
                        for (int j = 0; j < INSERT_BATCH_SIZE; j++) {
                            long id = (long) i * INSERT_BATCH_SIZE + j + randBase;
                            preparedStatement.setLong(1, id);
                            preparedStatement.setString(2, "name" + id);
                            preparedStatement.addBatch();
                        }
                        preparedStatement.executeBatch();
                        break;
                    case 3:
                        class MyClickHouseWriter implements ClickHouseWriter {
                            int batchIndex = 0;
                            public MyClickHouseWriter(int batchIndex) {
                                this.batchIndex = batchIndex;
                            }
                            @Override
                            public void write(ClickHouseOutputStream clickHouseOutputStream) throws IOException {
                                for (int j = 0; j < INSERT_BATCH_SIZE; j++) {
                                    long id = (long) batchIndex * INSERT_BATCH_SIZE + j + randBase;
                                    // write id(Int64)
                                    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                                    buffer.order(ByteOrder.LITTLE_ENDIAN);
                                    buffer.putLong(id);
                                    clickHouseOutputStream.write(buffer.array());
                                    // write name(String)
                                    clickHouseOutputStream.writeUnicodeString("name" + id);
                                }
                            }
                        }
                        preparedStatement.setObject(1, new MyClickHouseWriter(i));
                        preparedStatement.executeUpdate();
                        break;
                }

                System.out.printf("[%d] optimizeLevel=%d, insert batch [%d/%d] success, cost %d ms\n",
                        Thread.currentThread().getId(), optimizeLevel, i + 1, INSERT_BATCH_NUM, System.currentTimeMillis() - insertStartTime);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }

    }

    /**
     * count table
     * @param conn ClickHouse connection
     * @throws Exception
     */
    public static void count(Connection conn) throws Exception {
        try(Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT count() as cnt FROM `default`.`test`");
            if (resultSet.next()) {
                System.out.printf("table `default`.`test` has %d rows\n", resultSet.getInt("cnt"));
            } else {
                throw new RuntimeException("failed to count table `default`.`test`");
            }
        }
    }
}