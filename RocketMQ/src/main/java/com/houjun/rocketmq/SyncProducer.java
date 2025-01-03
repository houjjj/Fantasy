package com.houjun.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class SyncProducer {
	public static void main(String[] args) throws Exception {
//    	// 实例化消息生产者Producer
//        DefaultMQProducer producer = new DefaultMQProducer("houjun");
//    	// 设置NameServer的地址
//    	producer.setNamesrvAddr("172.28.95.17:9876");
//    	// 启动Producer实例
//        producer.start();
//    	for (int i = 0; i < 100; i++) {
//    	    // 创建消息，并指定Topic，Tag和消息体
//    	    Message msg = new Message("TopicTest" /* Topic */,
//        	"TagA" /* Tag */,
//        	("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
//        	);
//        	// 发送消息到一个Broker
//            SendResult sendResult = producer.send(msg);
//            // 通过sendResult返回消息是否成功送达
//            System.out.printf("%s%n", sendResult);
//    	}
//    	// 如果不再发送消息，关闭Producer实例。
//    	producer.shutdown();
		System.out.println(getPvNameByPod("rocketmq-test-broker-broker-182-19-19-19","182.19.19.19"));
	}
	private static String getPvNameByPod(String podName,String podIp) {
		String ip = podIp.replaceAll("\\.", "-");
		return podName.replace(ip,"pv-"+ip);
	}

}