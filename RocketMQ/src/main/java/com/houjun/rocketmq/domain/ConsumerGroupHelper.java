package com.houjun.rocketmq.domain;


import com.beust.jcommander.internal.Lists;
import com.houjun.rocketmq.utils.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.MQVersion;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.Pair;
import org.apache.rocketmq.common.admin.ConsumeStats;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.common.protocol.body.ConsumerConnection;
import org.apache.rocketmq.common.protocol.body.SubscriptionGroupWrapper;
import org.apache.rocketmq.common.protocol.route.BrokerData;
import org.apache.rocketmq.common.subscription.SubscriptionGroupConfig;
import org.apache.rocketmq.tools.admin.MQAdminExt;

import java.util.*;


@Slf4j
public class ConsumerGroupHelper {
    
    private static final Set<String> SYSTEM_GROUP_SET = new HashSet<>();
    static {
        SYSTEM_GROUP_SET.add(MixAll.TOOLS_CONSUMER_GROUP);
        SYSTEM_GROUP_SET.add(MixAll.FILTERSRV_CONSUMER_GROUP);
        SYSTEM_GROUP_SET.add(MixAll.SELF_TEST_CONSUMER_GROUP);
        SYSTEM_GROUP_SET.add(MixAll.ONS_HTTP_PROXY_GROUP);
        SYSTEM_GROUP_SET.add(MixAll.CID_ONSAPI_PULL_GROUP);
        SYSTEM_GROUP_SET.add(MixAll.CID_ONSAPI_PERMISSION_GROUP);
        SYSTEM_GROUP_SET.add(MixAll.CID_ONSAPI_OWNER_GROUP);
        SYSTEM_GROUP_SET.add(MixAll.CID_SYS_RMQ_TRANS);
    }
    
    public static List<ConsumeGroupInfo> queryGroupList(MQAdminExt mqAdminExt) {
        
        Map<String, Set<String>> consumerGroupMap = new HashMap<>();
        try {
            ClusterInfo clusterInfo = mqAdminExt.examineBrokerClusterInfo();
            for (BrokerData brokerData : clusterInfo.getBrokerAddrTable().values()) {
                SubscriptionGroupWrapper subscriptionGroupWrapper = mqAdminExt.getAllSubscriptionGroup(brokerData.selectBrokerAddr(), 3000L);
                consumerGroupMap.computeIfAbsent(brokerData.selectBrokerAddr(), k -> new HashSet<>()).addAll(subscriptionGroupWrapper.getSubscriptionGroupTable().keySet());
            }
        }
        catch (Exception e) {
            throw wrap(e);
        }
        List<ConsumeGroupInfo> consumeGroupInfoList = Lists.newArrayList();
        for (Map.Entry<String, Set<String>> entry : consumerGroupMap.entrySet()) {
            for (String group : entry.getValue()) {
                consumeGroupInfoList.add(queryGroup(mqAdminExt, entry.getKey(), group));
            }
        }
        
        consumeGroupInfoList.forEach(group -> {
            if (SYSTEM_GROUP_SET.contains(group.getGroup())) {
                group.setSysConsumerGroup(true);
            }
        });
        Collections.sort(consumeGroupInfoList);
        return consumeGroupInfoList;
    }
    
    private static ConsumeGroupInfo queryGroup(MQAdminExt mqAdminExt, String broker, String consumerGroup) {
        ConsumeGroupInfo groupConsumeInfo = new ConsumeGroupInfo();
        try {
            ConsumeStats consumeStats = null;
            try {
                consumeStats = mqAdminExt.examineConsumeStats(consumerGroup);
            }
            catch (Exception e) {
                log.warn("examineConsumeStats exception to consumerGroup {}, response [{}]", consumerGroup, e.getMessage());
            }
        
            ConsumerConnection consumerConnection = null;
            try {
                consumerConnection = mqAdminExt.examineConsumerConnectionInfo(consumerGroup);
            }
            catch (Exception e) {
                log.warn("examineConsumeStats exception to consumerGroup {}, response [{}]", consumerGroup, e.getMessage());
            }
        
            groupConsumeInfo.setBroker(broker);
            groupConsumeInfo.setGroup(consumerGroup);
        
            if (consumeStats != null) {
                groupConsumeInfo.setConsumeTps((int)consumeStats.getConsumeTps());
                groupConsumeInfo.setDiffTotal(consumeStats.computeTotalDiff());
            }
        
            if (consumerConnection != null) {
                groupConsumeInfo.setCount(consumerConnection.getConnectionSet().size());
                groupConsumeInfo.setMessageModel(consumerConnection.getMessageModel());
                groupConsumeInfo.setConsumeType(consumerConnection.getConsumeType());
                groupConsumeInfo.setVersion(MQVersion.getVersionDesc(consumerConnection.computeMinVersion()));
            }
        }
        catch (Exception e) {
            log.warn("examineConsumeStats or examineConsumerConnectionInfo exception, "
                + consumerGroup, e);
        }
        return groupConsumeInfo;
    }
    
    public static Set<String> fetchBrokerNameSetBySubscriptionGroup(MQAdminExt admin, String group) {
        Set<String> brokerNameSet = new HashSet<>();
        try {
            ClusterInfo clusterInfo = admin.examineBrokerClusterInfo();
            for (String brokerName : clusterInfo.getBrokerAddrTable().keySet()) { //foreach brokerName
                String brokerAddress = clusterInfo.getBrokerAddrTable().get(brokerName).selectBrokerAddr();
                SubscriptionGroupConfig subscriptionGroupConfig = admin.examineSubscriptionGroupConfig(brokerAddress, group);
                if (subscriptionGroupConfig == null) {
                    continue;
                }
                brokerNameSet.add(brokerName);
            }
            return brokerNameSet;
        }
        catch (Exception e) {
            throw wrap(e);
        }
    }
    
    public static void deleteSubGroup(MQAdminExt admin, String groupName) {
        Set<String> brokers = fetchBrokerNameSetBySubscriptionGroup(admin, groupName);
        try {
            ClusterInfo clusterInfo = admin.examineBrokerClusterInfo();
            for (String brokerName : brokers) {
                admin.deleteSubscriptionGroup(clusterInfo.getBrokerAddrTable().get(brokerName).selectBrokerAddr(), groupName, true);
            }
        }
        catch (Exception e) {
            throw wrap(e);
        }
    }
    
    
    private static RuntimeException wrap(Exception e) {
        throw new CustomException(600, e.getMessage());
    }
}
