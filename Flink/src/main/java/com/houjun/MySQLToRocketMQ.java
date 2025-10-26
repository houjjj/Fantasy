package com.houjun;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class MySQLToRocketMQ {

    public static void main(String[] args) throws Exception {

        // RocketMQ Producer 初始化
        DefaultMQProducer producer = new DefaultMQProducer("flink-cdc-producer");
        producer.setNamesrvAddr("192.168.1.126:9876"); // ← 修改为你的 NameServer
        producer.start();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);

        // Flink CDC Source
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("127.0.0.1")   // ← MySQL 地址
                .port(3306)
                .username("root")
                .password("123456")
                .databaseList("test_db")  // ← 监控的库
                .tableList("test_db.user") // ← 监控的表
                .deserializer(new StringDebeziumDeserializationSchema()) // 将变更序列化为 JSON String
                .startupOptions(StartupOptions.latest()) // 从最新 binlog 开始
                .build();

        env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL-CDC-Source")
                .map(dataJson -> {
                    // 发送到 RocketMQ
                    Message msg = new Message(
                            "test_topic",               // ← 目标 Topic
                            "cdc_tag",
                            dataJson.getBytes()
                    );
                    producer.send(msg);
                    return dataJson; // return 只是为了 trace 或 debug
                })
                .print(); // 打印输出便于验证

        env.execute("Flink CDC → RocketMQ Demo");
    }
}
