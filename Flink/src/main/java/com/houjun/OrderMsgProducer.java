package com.houjun;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.concurrent.Semaphore;

/**
 * 这套能跑到1w7，broker busy,
 */
@Slf4j
public class OrderMsgProducer {
    static String topic = "cctv";
    static String namesrvAddr = "192.168.0.165:9876";
    static int permits = 2000;

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);

        // 设置你想在 Web UI 图形上显示的算子链组数（例如 8 组，生产环境是 48 组）
        int graphCount = 8;

        for (int i = 0; i < graphCount; i++) {

            // 1. 每次循环都单独创建一个 Source 节点
            env.fromSequence(1, 2000000000)
                    .name("Sequence-Source-" + i)
                    .setParallelism(1) // 保持独立，建议与图中一样设为 1
                    .slotSharingGroup("slot-group-" + (i % 8))// 每个算子链占用一个slot
//                    .disableChaining()
                    // 2. 将数据直接流向单独的 Sink 节点
                    .addSink(new OptimizedRocketMQSink())
                    .name("RocketMQ-Async-Sink-" + i)
                    .slotSharingGroup("slot-group-" + (i % 8))
                    .setParallelism(1); // 同样可独立设置并行度
        }
        env.execute("Flink Order RocketMQ High Performance Test Job");
    }

    public static class OptimizedRocketMQSink extends RichSinkFunction<Long> {
        private transient DefaultMQProducer producer;
        // 复用 MessageQueueSelector，避免频繁 GC
        private static final MessageQueueSelector QUEUE_SELECTOR = (mqs, msg, arg) -> {
            Integer id = (Integer) arg;
            return mqs.get(Math.abs(id % mqs.size()));
        };

        // 背压控制：限制单个 Task 内最大未完成的异步发送数量，防止内存 OOM
        private transient Semaphore semaphore;
        // 异常计数器/标识
        private transient volatile Exception lastAsyncException;

        @Override
        public void open(Configuration parameters) throws Exception {
//            AclClientRPCHook auth = new AclClientRPCHook(new SessionCredentials("rocketmq", "xxxxxxxx"));
            producer = new DefaultMQProducer("flink-test-producer" );
            producer.setNamesrvAddr(namesrvAddr);

            // 核心调优参数
            producer.setSendMessageWithVIPChannel(false); // 关闭 VIP 通道（减少端口适配问题）
//            producer.setAsyncSenderExecutorThreads(Runtime.getRuntime().availableProcessors() * 2); // 异步发送线程池
            producer.setRetryTimesWhenSendAsyncFailed(2); // 异步发送失败重试次数
            producer.setMaxMessageSize(1024 * 1024); // 1MB
            producer.setSendMsgTimeout(30 * 60 * 1000);

            producer.start();

            // 限制单个 TaskTaskManager 同时处于 Fly 状态的异步请求数（如 2000）
            semaphore = new Semaphore(permits);
            log.info("Optimized RocketMQ Producer started.");
        }

        @Override
        public void invoke(Long value, Context context) throws Exception {
            // 检查异步回调是否有异常上报
            if (lastAsyncException != null) {
                throw new RuntimeException("Async send message failed in background thread", lastAsyncException);
            }

            String[] tags = new String[]{"ck_fifo"};
            String s = "{\"id\":167925538859,\"data_source_system\":\"YWJD\",\"rec_waybill_id\":\"104568426753\",\"waybill_no\":\"9818482112687\",\"waybill_no_origin\":\"9818482112687\",\"post_org_code\":\"52720008\",\"post_date\":\"2026-08-02 11:31:45\",\"post_org_name\":\"中国邮政集团有限公司广东省罗定市城区揽投部\",\"postage_other\":0.00,\"postage_standard\":8.00,\"postage_total\":1.45,\"postage_paid\":1.45,\"turn_back_cust_amount\":null,\"back_amount\":null,\"post_tic_amount\":null,\"cod_flag\":\"0\",\"cod_amount\":0.00,\"internals_type\":\"3\",\"insurance_flag\":\"1\",\"insurance_amount\":0.00,\"payment_mode\":\"0\",\"settlement_mode\":\"YCK\",\"receipt_flag\":\"1\",\"receipt_waybill_no\":\"\",\"receipt_fee_amount\":0.00,\"pre_waybill_no\":null,\"waybill_type\":\"0\",\"unit\":\"LG001\",\"quantity\":1,\"biz_product_no\":\"113124300000691\",\"biz_product_name\":\"快递包裹\",\"product_classify_code\":null,\"product_attr_group\":null,\"customer_signing_party\":\"2\",\"area_attr\":\"1\",\"ledger_account_flag\":\"B\",\"sender_cust_code\":\"91101108240222\",\"sender_business_relation_code\":\"1100219578891\",\"post_person_code\":\"5272000800014\",\"post_person_name\":\"陈作鹏\",\"pickup_person_code\":\"5272000800014\",\"pickup_person_name\":\"陈作鹏\",\"dist_range\":\"3\",\"sender_warehouse_id\":9783202,\"sender_warehouse_name\":\"匠艺美(寄递)\",\"sender_type\":null,\"sender\":\"匠艺美(寄递)\",\"sender_id\":null,\"sender_no\":\"1100219578891\",\"sender_linker\":\"|$4|PSeq75JjKoSmQShKvwjjrTVFeaarFyRoI+zAk33wRGA=\",\"sender_fixtel\":null,\"sender_mobile\":\"|$4|/Q+DT5cSDeysQdQ/Qx/F5A==\",\"sender_country_no\":\"445381\",\"sender_country_name\":\"CN\",\"sender_province_no\":\"4400\",\"sender_province_name\":\"广东省\",\"sender_city_no\":\"4453\",\"sender_city_name\":\"云浮市\",\"sender_county_no\":\"445381\",\"sender_county_name\":\"罗定市\",\"sender_district_no\":null,\"order_weight\":0.00,\"real_weight\":30.00,\"fee_weight\":30.00,\"volume_weight\":0.00,\"length\":10.00,\"width\":3.00,\"height\":3.00,\"exp_product_flag\":\"0\",\"pkg_id\":null,\"notesid\":null,\"follow_concession_rate\":null,\"follow_concession_postage\":null,\"is_follow_concession\":\"1\",\"follow_concession_state\":\"1\",\"customer_preferential_type\":null,\"fconcession_start_time\":null,\"separate_account_id\":null,\"process_code\":\"AT0101\",\"outworker_supplier_code\":null,\"allo_factor_type_code\":\"M187\",\"taxed_postage_paid\":1.367924,\"taxed_postage_other\":null,\"taxed_postage_total\":null,\"taxed_delivery_paid\":null,\"taxed_transport_paid\":null,\"delivery_paid\":null,\"transport_paid\":null,\"send_tax_rate\":null,\"carriage_tax_rate\":null,\"revision\":null,\"is_deleted\":0,\"create_date\":\"2026-08-02 11:33:24\",\"create_user\":null,\"update_date\":\"2026-09-01 14:04:32\",\"update_user\":null,\"reserved1\":null,\"reserved2\":0,\"reserved3\":null,\"reserved4\":null,\"reserved5\":null,\"reserved6\":\"1\",\"reserved7\":null,\"reserved8\":null,\"reserved9\":null,\"reserved10\":null,\"reserved11\":1,\"reserved12\":null,\"reserved13\":null,\"reserved14\":null,\"reserved15\":\"90\",\"reserved16\":null,\"reserved17\":\"2026-08-02 11:31:50\",\"reserved18\":null,\"odps_date\":null,\"reserved20\":null,\"tax_attr\":\"2\",\"post_cust_code\":null,\"post_cust_name\":null,\"post_cust_type\":null,\"last_product_code\":\"CP070102\",\"last_product_name\":\"快递包裹\",\"ledger_product_code\":\"CP070102\",\"ledger_product_name\":null,\"specialty_code\":\"BL010102\",\"specialty_name\":\"快包\",\"person_liable\":null,\"payment_days_msg\":null,\"account_book\":\"JDSD\",\"comp_code\":\"B445301\",\"dept_code\":\"B44530102\",\"comp_name\":\"云浮市速递分公司\",\"dept_name\":\"云浮罗定速递\",\"turned_account_book\":\"JDSD\",\"turned_comp_code\":\"B445301\",\"turned_dept_code\":\"B44530102\",\"turned_comp_name\":\"云浮市速递分公司\",\"turned_dept_name\":\"云浮罗定速递\",\"collection_mode\":null,\"delivery_update_date\":null,\"transport_update_date\":null,\"inc_item\":\"YCK\",\"delivery_invoice_name\":null,\"delivery_zero_tax_rate_flag\":null,\"delivery_category_code\":null,\"transport_zero_tax_rate_flag\":null,\"transport_category_code\":null,\"transport_invoice_name\":null,\"person_in_charge\":\"4004129354\",\"payment_term\":\"0\",\"delivery_is_income_tax\":null,\"transport_is_income_tax\":null,\"is_income_tax\":\"Y\",\"tax_rate\":6.00,\"zero_tax_rate_flag\":\"\",\"category_code\":\"3040409040000000000\",\"invoice_name\":\"*生产生活服务*快递费\",\"is_tax_split\":\"0\",\"postage_update_date\":null,\"receiver\":null,\"receiver_id\":null,\"receiver_no\":null,\"receiver_linker\":\"|$4|pjTEHAaVVaRw+kowr5OAyQ==\",\"receiver_fixtel\":null,\"receiver_moblie\":\"|$4|EOhoryghV4ahXG/7Xd0OUA==\",\"receiver_country_no\":\"CHN\",\"receiver_country_name\":\"中国\",\"receiver_province_no\":\"610000\",\"receiver_province_name\":\"陕西省\",\"receiver_city_no\":\"610100\",\"receiver_city_name\":\"西安市\",\"receiver_county_no\":\"610112\",\"receiver_county_name\":\"未央区\",\"receiver_district_no\":\"610112\",\"cust_bill_status\":\"1\",\"cust_bill_no\":\"ACMDZ1660289419648123\",\"statement_status\":\"0\",\"statement_no\":null,\"cust_bill_status_origin\":\"0\",\"cust_bill_no_origin\":null,\"statement_status_origin\":\"0\",\"statement_no_origin\":\"需要推送新一代\",\"sender_address\":\"|$4|t13u6IegxRpQyN+46kq7ozInN/d4OcsQc2frGMsWml5XtAT8Oo3Qjo6gNHKONJNc\",\"receiver_address\":\"|$4|xUVgeiZg0X/gfFHLtb3w+8VMatJVD5aGe9U5JsAWppFnU8hKDuQiXBkF4KjBB348SWgOWlJ9WSKIVTRWH7Wu1zd4WPnMI6aIOt4d9SecisSretnbuC3r4ukdBWVskItPPV1muVeckVGbauiW22iMsQ==\",\"currency\":\"CNY\",\"remark\":null,\"settled_org_code\":\"52720008\",\"settled_org_name\":\"中国邮政集团有限公司广东省罗定市城区揽投部\",\"pkg_code\":null,\"is_gjxb_flag\":\"0\",\"follow_concession_date\":null,\"deliver_date\":null,\"req_time\":\"2026-08-02 11:31:45\",\"is_invoice\":\"Y\",\"delivery_is_invoice\":null,\"transport_is_invoice\":null,\"biz_collection_mode\":null,\"data_type\":\"0\",\"write_off_amount\":null,\"inc_type\":\"JDSR\",\"sub_account_no\":null,\"sub_account_name\":null,\"sub_department_name\":null,\"sub_address_group_name\":null,\"sorted_value_card_no\":null,\"channel\":\"PINDUODUO\",\"is_pre_sale_face_list\":\"0\",\"pay_channel\":null,\"business_type\":null,\"project_no\":null,\"ysmd_amount\":0.00,\"reserved21\":null,\"reserved22\":null,\"reserved23\":null,\"coupon_type\":null,\"coupon_cust_sub_code\":null,\"reserved26\":null,\"reserved27\":null,\"reserved28\":null,\"sell_org_name\":null,\"coupon_cust_name\":null,\"reserved31\":null,\"reserved32\":null,\"reserved33\":null,\"reserved34\":null,\"reserved35\":null,\"reserved36\":null,\"collection_amount\":1.45,\"postage_paid_amount\":1.45,\"coupon_amount\":null,\"total_amount\":1.45,\"coupon_code\":null,\"coupon_batch_no\":null,\"cost_attr\":null,\"coupon_cust_code\":null,\"coupon_settle_method\":null,\"sell_org_code\":null,\"old_waybill_no\":null,\"reserved48\":null,\"reserved49\":null,\"ecommerce_no\":null,\"send_bigdata_time\":\"2026-09-01 23:30:57\"}";
            byte[] bodyBytes = (s + value).getBytes(RemotingHelper.DEFAULT_CHARSET);

            // 批量/连续产生 100 条消息
//            for (int i = 0; i < 100; i++) {
                int i = 2;
                int orderId = i % 10;
                Message msg = new Message(topic, tags[i % tags.length], "KEY" + i, bodyBytes);

                // 获取许可，进行流量控制（阻塞防止发送过快导致内存溢出）
                semaphore.acquire();

                try {
                    // 改为异步发送 send(msg, selector, arg, callback)
                    producer.send(msg, QUEUE_SELECTOR, orderId, new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            semaphore.release(); // 释放许可
                        }

                        @Override
                        public void onException(Throwable e) {
                            semaphore.release(); // 释放许可
                            log.error("Send MQ async exception", e);
                            lastAsyncException = new Exception(e);
                        }
                    });
                } catch (Exception e) {
                    semaphore.release();
                    throw e;
                }
//            }
        }

        @Override
        public void close() {
            // 优雅停机：等待所有正在发送的异步消息完成
            if (semaphore != null) {
                semaphore.acquireUninterruptibly(permits);
            }
            if (producer != null) {
                producer.shutdown();
            }
            log.info("Optimized RocketMQ Producer shutdown.");
        }
    }
}