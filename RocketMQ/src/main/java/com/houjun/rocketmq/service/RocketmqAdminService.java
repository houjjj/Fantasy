package com.houjun.rocketmq.service;

import com.beust.jcommander.internal.Lists;
import com.houjun.rocketmq.domain.*;
import com.houjun.rocketmq.utils.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.*;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.TopicConfig;
import org.apache.rocketmq.common.admin.ConsumeStats;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.body.*;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.apache.rocketmq.tools.admin.MQAdminExt;
import org.apache.rocketmq.tools.command.CommandUtil;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RocketmqAdminService {

    public List<String> listTopicName(String nameServAddrs, String username, String password) {
        RocketmqTopicDetail detail = new RocketmqTopicDetail();
        detail.setNameServAddrs(nameServAddrs);
        return listTopic(detail, username, password).stream().map(SimpleRocketmqTopicInfo::getTopicName).collect(Collectors.toList());
    }

    private static final ThreadLocal<String> addr = new ThreadLocal<>();

    public  static List<SimpleRocketmqTopicInfo> listTopic(RocketmqTopicDetail detail, String username, String password) {
        MQAdminExt mqAdminExt = getAdminExt(detail.getNameServAddrs(), username, password);
        try {
            List<SimpleRocketmqTopicInfo> res = new ArrayList<>();
            TopicList topicList = mqAdminExt.fetchAllTopicList();

            for (String topic : topicList.getTopicList()) {
                SimpleRocketmqTopicInfo info = new SimpleRocketmqTopicInfo();
                info.setTopicName(topic);
                info.setType(getType(topic));
                res.add(info);
            }

            Set<String> sysTopics = sysTopicList(username, password);
            for (SimpleRocketmqTopicInfo each : res) {
                if (sysTopics.contains(each.getTopicName())) {
                    each.setType("SYS");
                }
            }

            return res;
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(600, e.getMessage());
        } finally {
            addr.remove();
        }
    }

    private static Set<String> sysTopicList(String username, String password) {
        DefaultMQProducer producer;
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
            producer = new DefaultMQProducer(MixAll.SELF_TEST_PRODUCER_GROUP);
        } else {
            AclClientRPCHook auth = new AclClientRPCHook(new SessionCredentials(username, password));
            producer = new DefaultMQProducer(MixAll.SELF_TEST_PRODUCER_GROUP, auth);
        }
        producer.setMqClientApiTimeout(1500);
        producer.setNamesrvAddr(addr.get());

        try {
            producer.start();
            return producer.getDefaultMQProducerImpl().getmQClientFactory().getMQClientAPIImpl().getSystemTopicList(5_000L).getTopicList();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            producer.shutdown();
        }
        return new HashSet<>();
    }

    public RocketmqTopicDetail getTopicDetail(RocketmqTopicDetail detail, String username, String password) {
        MQAdminExt mqAdminExt = getAdminExt(detail.getNameServAddrs(), username, password);
        try {
            Set<String> clusters = mqAdminExt.getTopicClusterList(detail.getTopicName());
            mqAdminExt.getClusterList(detail.getTopicName());
            mqAdminExt.getTopicClusterList(detail.getTopicName());

            TopicConfig topicConfig = mqAdminExt.examineTopicConfig("", detail.getTopicName());
            RocketmqTopicDetail res = new RocketmqTopicDetail();
            res.setTopicName(topicConfig.getTopicName());
            res.setReadQueueNums(topicConfig.getReadQueueNums());
            res.setWriteQueueNums(topicConfig.getWriteQueueNums());
            res.setPerm(topicConfig.getPerm());
            res.setClusterName(String.join(",", clusters));
            return res;
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(600, e.getMessage());
        } finally {
            mqAdminExt.shutdown();
        }
    }

    /**
     * 必须有 clusterName 不然会删不掉
     */
    public void deleteTopic(RocketmqTopicDetail detail, String username, String password) {
        MQAdminExt mqAdminExt = getAdminExt(detail.getNameServAddrs(), username, password);
        try {
            Set<String> masterSet = CommandUtil.fetchMasterAddrByClusterName(mqAdminExt, detail.getClusterName());
            mqAdminExt.deleteTopicInBroker(masterSet, detail.getTopicName());
            Set<String> nameServerSet = new HashSet<>(Arrays.asList(detail.getNameServAddrs().split(",")));
            mqAdminExt.deleteTopicInNameServer(nameServerSet, detail.getTopicName());
        } catch (Exception e) {
            throw new CustomException(600, e.getMessage());
        } finally {
            mqAdminExt.shutdown();
        }
    }

    /**
     * 必要条件：cluster 名、主题名，nameserv 列表
     */
    public static void createTopic(RocketmqTopicDetail detail, String username, String password) {
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setTopicName(detail.getTopicName());

        if (detail.getReadQueueNums() > 0) {
            topicConfig.setReadQueueNums(detail.getReadQueueNums());
        }
        if (detail.getWriteQueueNums() > 0) {
            topicConfig.setWriteQueueNums(detail.getWriteQueueNums());
        }
        if (detail.getPerm() > 0) {
            topicConfig.setPerm(detail.getPerm());
        }

        MQAdminExt mqAdminExt = getAdminExt(detail.getNameServAddrs(), username, password);
        try {
            ClusterInfo clusterInfo = mqAdminExt.examineBrokerClusterInfo();

            Set<String> brokers = clusterInfo.getClusterAddrTable().get(detail.getClusterName());
            for (String broker : brokers) {
                String brokerAddr = clusterInfo.getBrokerAddrTable().get(broker).selectBrokerAddr();
                log.debug("creating topic {} to broker {} - addr {}", detail.getTopicName(), broker, brokerAddr);
                mqAdminExt.createAndUpdateTopicConfig(brokerAddr, topicConfig);
            }

        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(600, e.getMessage());
        } finally {
            mqAdminExt.shutdown();
        }
    }

    /**
     * rocketmq 里貌似是同一个接口，暂时这么搞
     */
    public void alterTopic(RocketmqTopicDetail detail, String username, String password) {
        createTopic(detail, username, password);
    }

    /**
     * 必传：名字服务、主题名、消费者组、时间点、是否强制（默认是）
     * 这里貌似有个状态，先不管了
     * 不行，太粗糙！
     */
    public void resetOffset(RocketmqTopicDetail detail, String username, String password) {
        MQAdminExt mqAdminExt = getAdminExt(detail.getNameServAddrs(), username, password);
        try {
            mqAdminExt.resetOffsetByTimestamp(detail.getTopicName(), detail.getConsumerGroup(), detail.getResetTimestamp(), detail.isForce());
        } catch (Exception e) {
            throw new CustomException(600, e.getMessage());
        } finally {
            mqAdminExt.shutdown();
            mqAdminExt.shutdown();
        }
    }

    /**
     * 据我观察，只是把时间设置成了 -1，就成最新的了
     */
    public void skipAccumulate(RocketmqTopicDetail detail, String username, String password) {
        detail.setResetTimestamp(-1L);
        resetOffset(detail, username, password);
    }

    public static MQAdminExt getAdminExt(String nameServAddrList, String username, String password) {
        DefaultMQAdminExt mqAdminExt = null;
        //判断是否具有用户名和密码
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
            //没有用户名密码
            mqAdminExt = new DefaultMQAdminExt(5_000);
        } else {
            AclClientRPCHook aclClientRPCHook = new AclClientRPCHook(new SessionCredentials(username, password));
            mqAdminExt = new DefaultMQAdminExt(aclClientRPCHook, 5_000);
        }

        String[] addrs = nameServAddrList.split(",");
        boolean connected = false;
        int i = 0;
        while (i < addrs.length) {
            mqAdminExt.setNamesrvAddr(addrs[i]);
            try {
                mqAdminExt.start();
                connected = true;
                addr.set(addrs[i]);
                log.info("连接到 namesrv {}", addrs[i]);
                break;
            } catch (MQClientException e) {
                log.info("{} connect failed, try next", addrs[i]);
            }
            i++;
        }
        if (!connected) {
            throw new CustomException(600, nameServAddrList + " 一个能连的都没有");
        }
        return mqAdminExt;
    }

    private static String getType(String topic) {
        if (topic.startsWith("%SYS%")) {
            return "SYS";
        } else if (topic.startsWith("%DLQ%")) {
            return "DLQ";
        } else if (topic.startsWith("%RETRY%")) {
            return "RETRY";
        } else {
            return "NORMAL";
        }
    }

    public List<RocketmqMessageView> listDlqMessage(RocketmqMessageQuery query, String username, String password) {
        return listMessage(query, username, password);
    }

    public List<RocketmqMessageView> listMessage(RocketmqMessageQuery query, String username, String password) {
        MQPullConsumer consumer = getConsumer(query, username, password);

        try {
            consumer.start();
            Collection<MessageQueue> messageQueues = consumer.fetchSubscribeMessageQueues(query.getTopicName());

            log.debug("获取到 {} 个消息队列", messageQueues.size());

            Queue<RocketmqMessageView> queue = new LinkedBlockingQueue<>();
            CountDownLatch latch = new CountDownLatch(messageQueues.size());
            for (MessageQueue messageQueue : messageQueues) {
                long offset = consumer.searchOffset(messageQueue, query.getStartTime());
                consumer.pull(messageQueue, "*", offset, 500, new PullCallback() {
                    @Override
                    public void onSuccess(PullResult pull) {
                        try {
                            if (pull.getPullStatus() == PullStatus.FOUND) {
                                for (MessageExt messageExt : pull.getMsgFoundList()) {
                                    if (messageExt.getStoreTimestamp() > query.getEndTime())
                                        break;
                                    queue.offer(RocketmqMessageView.fromMessageExt(messageExt));
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } finally {
                            latch.countDown();
                        }
                    }

                    @Override
                    public void onException(Throwable e) {
                        latch.countDown();
                        log.error("", e);
                    }
                });
            }

            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (Exception e) {
            }

            return new ArrayList<>(queue);
        } catch (Exception e) {
            throw wrap(e);
        } finally {
            consumer.shutdown();
        }
    }

    private MQPullConsumer getConsumer(RocketmqMessageQuery query, String username, String password) {
        DefaultMQPullConsumer consumer = null;
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
            //没有用户名密码
            consumer = new DefaultMQPullConsumer();
        } else {
            AclClientRPCHook aclClientRPCHook = new AclClientRPCHook(new SessionCredentials(username, password));
            consumer = new DefaultMQPullConsumer(aclClientRPCHook);
        }

        consumer.setConsumerGroup(MixAll.SELF_TEST_CONSUMER_GROUP);
        String[] addrs = query.getNameServAddrs().split(",");
        consumer.setNamesrvAddr(addrs[0]);
        return consumer;
    }

    public void resendMessage(RocketmqDlqMessageRequest request, String username, String password) {
        MQAdminExt mqAdminExt = getAdminExt(request.getNameServAddrs(), username, password);
        try {
            ConsumerConnection consumerConnection = mqAdminExt.examineConsumerConnectionInfo(request.getConsumerGroup());
            for (Connection connection : consumerConnection.getConnectionSet()) {
                if (StringUtils.isBlank(connection.getClientId())) {
                    continue;
                }
                mqAdminExt.consumeMessageDirectly(request.getConsumerGroup(), connection.getClientId(), request.getTopicName(), request.getMsgId());
            }
        } catch (Exception e) {
            throw new CustomException(600, e.getMessage());
        } finally {
            mqAdminExt.shutdown();
        }
    }

    public List<String> listConsumerGroup(String nameServAddrs, String topic, String username, String password) {
        MQAdminExt mqAdminExt = getAdminExt(nameServAddrs, username, password);
        try {
            GroupList groupList = mqAdminExt.queryTopicConsumeByWho(topic);
            List<String> res = new ArrayList<>(groupList.getGroupList());
            Collections.sort(res);
            return res;
        } catch (Exception e) {
            throw wrap(e);
        } finally {
            mqAdminExt.shutdown();
        }
    }

    public List<String> listCluster(String nameServAddrs, String username, String password) {
        MQAdminExt mqAdminExt = getAdminExt(nameServAddrs, username, password);
        try {
            ClusterInfo clusterInfo = mqAdminExt.examineBrokerClusterInfo();
            List<String> res = new ArrayList<>(clusterInfo.getClusterAddrTable().keySet());
            Collections.sort(res);
            return res;
        } catch (Exception e) {
            throw new CustomException(600, e.getMessage());
        } finally {
            mqAdminExt.shutdown();
        }
    }

    public List<ConsumeGroupInfo> listConsumerGroupInfo(String nameServAddrs, String username, String password) {
        MQAdminExt mqAdminExt = getAdminExt(nameServAddrs, username, password);
        try {
            return ConsumerGroupHelper.queryGroupList(mqAdminExt);
        } catch (Exception e) {
            throw wrap(e);
        } finally {
            mqAdminExt.shutdown();
        }
    }

    private RuntimeException wrap(Exception e) {
        throw new CustomException(600, e.getMessage());
    }

    public ConsumerConnection getConsumerConnection(String nameServAddrs, String consumerGroup, String username, String password) {
        MQAdminExt mqAdminExt = getAdminExt(nameServAddrs, username, password);
        try {
            return mqAdminExt.examineConsumerConnectionInfo(consumerGroup);
        } catch (Exception e) {
            throw wrap(e);
        } finally {
            mqAdminExt.shutdown();
        }
    }

    public List<TopicConsumerInfo> queryConsumeStatsListByGroupName(String nameServAddrs, String groupName, String username, String password) {
        return queryConsumeStatsList(nameServAddrs, null, groupName, username, password);
    }

    public List<TopicConsumerInfo> queryConsumeStatsList(String nameServAddrs, String topic, String groupName, String username, String password) {
        MQAdminExt mqAdminExt = getAdminExt(nameServAddrs, username, password);
        try {
            ConsumeStats consumeStats = mqAdminExt.examineConsumeStats(groupName, topic);
            List<MessageQueue> mqList = consumeStats.getOffsetTable().keySet().stream().filter(o -> StringUtils.isBlank(topic) || o.getTopic().equals(topic)).sorted().collect(Collectors.toList());
            List<TopicConsumerInfo> topicConsumerInfoList = Lists.newArrayList();
            TopicConsumerInfo nowTopicConsumerInfo = null;
            Map<MessageQueue, String> messageQueueClientMap = getClientConnection(mqAdminExt, groupName);
            for (MessageQueue mq : mqList) {
                if (nowTopicConsumerInfo == null || (!StringUtils.equals(mq.getTopic(), nowTopicConsumerInfo.getTopic()))) {
                    nowTopicConsumerInfo = new TopicConsumerInfo(mq.getTopic());
                    topicConsumerInfoList.add(nowTopicConsumerInfo);
                }
                QueueStatInfo queueStatInfo = QueueStatInfo.fromOffsetTableEntry(mq, consumeStats.getOffsetTable().get(mq));
                queueStatInfo.setClientInfo(messageQueueClientMap.get(mq));
                nowTopicConsumerInfo.appendQueueStatInfo(queueStatInfo);
            }
            return topicConsumerInfoList;
        } catch (Exception e) {
            throw wrap(e);
        } finally {
            mqAdminExt.shutdown();
        }
    }

    private Map<MessageQueue, String> getClientConnection(MQAdminExt mqAdminExt, String groupName) {
        Map<MessageQueue, String> results = new HashMap<>();
        try {
            ConsumerConnection consumerConnection = mqAdminExt.examineConsumerConnectionInfo(groupName);
            for (Connection connection : consumerConnection.getConnectionSet()) {
                String clinetId = connection.getClientId();
                ConsumerRunningInfo consumerRunningInfo = mqAdminExt.getConsumerRunningInfo(groupName, clinetId, false);
                for (MessageQueue messageQueue : consumerRunningInfo.getMqTable().keySet()) {
//                    results.put(messageQueue, clinetId + " " + connection.getClientAddr());
                    results.put(messageQueue, clinetId);
                }
            }
        } catch (Exception err) {
            log.error("op=getClientConnection_error", err);
        }
        return results;
    }

    public static void main(String[] args) throws Exception {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("nihaoma_");
        consumer.setMaxReconsumeTimes(1);
        consumer.setNamesrvAddr("192.168.1.70:30774");
        consumer.subscribe("test2333", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();

//        RocketmqAdminService admin = new RocketmqAdminService();
//        RocketmqTopicDetail detail = new RocketmqTopicDetail();
//        detail.setNameServAddrs("192.168.1.73:30774");
//        detail.setTopicName("test01");
//        detail.setClusterName("rocketmq02-broker");
//        detail.setBrokerName("rocketmq02-broker-0");
//
//        detail.setWriteQueueNums(11);
//        detail.setReadQueueNums(33);
//
//        MQAdminExt ext = admin.getAdminExt("192.168.1.73:30774");
//        ProducerConnection conn = ext.examineProducerConnectionInfo("producer", "test01");

//            System.out.println(admin.listTopic(detail));
//
//        RocketmqMessageQuery query = new RocketmqMessageQuery();
//        query.setStartTime(1656116040000L);
//        query.setEndTime(1656299640000L);
//        query.setTopicName("test01");
//        query.setNameServAddrs("192.168.1.73:30774");
//        System.out.println(admin.listMessage(query));

//        RocketmqMessageQuery quer = new RocketmqMessageQuery();
//        quer.setNameServAddrs(detail.getNameServAddrs());
//        quer.setConsumerGroup("RocketMQTest");
//        admin.listDlqMessage(quer);
    }

    public void deleteConsumerGroup(String nameServAddrs, String consumerGroup, String username, String password) {
        MQAdminExt admin = getAdminExt(nameServAddrs, username, password);
        try {
            ConsumerGroupHelper.deleteSubGroup(admin, consumerGroup);
        } finally {
            admin.shutdown();
        }
    }

    public List<Connection> listProducerGroup(String nameServAddrs, String producerGroup, String topic, String username, String password) {
        MQAdminExt admin = getAdminExt(nameServAddrs, username, password);
        try {
            List<Connection> res = new ArrayList<>(admin.examineProducerConnectionInfo(producerGroup, topic).getConnectionSet());
            res.sort(Comparator.comparing(Connection::getClientAddr));
            return res;
        } catch (Exception e) {
            throw wrap(e);
        } finally {
            admin.shutdown();
        }
    }

    public List<Void> consumerGroupDetail(String nameServAddrs, String consumerGroup, String username, String password) {

        return null;
    }
//    public List<ConsumerConfigInfo> examineSubscriptionGroupConfig(String group) {
//        List<ConsumerConfigInfo> consumerConfigInfoList = Lists.newArrayList();
//        try {
//            ClusterInfo clusterInfo = mqAdminExt.examineBrokerClusterInfo();
//            for (String brokerName : clusterInfo.getBrokerAddrTable().keySet()) { //foreach brokerName
//                String brokerAddress = clusterInfo.getBrokerAddrTable().get(brokerName).selectBrokerAddr();
//                SubscriptionGroupConfig subscriptionGroupConfig = mqAdminExt.examineSubscriptionGroupConfig(brokerAddress, group);
//                if (subscriptionGroupConfig == null) {
//                    continue;
//                }
//                consumerConfigInfoList.add(new ConsumerConfigInfo(Lists.newArrayList(brokerName), subscriptionGroupConfig));
//            }
//        }
//        catch (Exception e) {
//            throw propagate(e);
//        }
//        return consumerConfigInfoList;
//    }

}
