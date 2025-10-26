package com.houjun;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WordCount {
    public static void main(String[] args) throws Exception {
        String jarPath = WordCount.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        // 1. 创建执行环境
        final StreamExecutionEnvironment env =  StreamExecutionEnvironment.createRemoteEnvironment("192.168.1.73", 32013, "C:\\Users\\houju\\Documents\\WeChat Files\\wxid_rtcmhuf4zj6621\\FileStorage\\File\\2025-06\\TopSpeedWindowing.jar");
        // 2. 定义数据源（从集合读取测试数据）
        DataStream<String> text = env.fromElements(
            "Hello Flink",
            "Hello Java",
            "Flink is awesome"
        );

        // 3. 数据处理
        DataStream<Tuple2<String, Integer>> counts = text
            .flatMap(new Tokenizer())
                .uid("222")
            .keyBy(value -> value.f0)

            .sum(1);


        // 4. 输出结果（控制台打印）
        counts.print();

        // 5. 启动作业
        env.execute("Flink WordCount Example");
    }

    // 自定义分词函数
    public static class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            String[] words = value.toLowerCase().split("\\W+");
            for (String word : words) {
                if (!word.isEmpty()) {
                    out.collect(new Tuple2<>(word, 1));
                }
            }
        }
    }
}