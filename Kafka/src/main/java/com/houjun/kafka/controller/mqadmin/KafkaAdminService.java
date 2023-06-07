package com.houjun.kafka.controller.mqadmin;

import com.houjun.kafka.controller.domain.KafkaTopicDetail;
import com.houjun.kafka.controller.domain.SimpleKafkaTopicInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class KafkaAdminService {

    public List<SimpleKafkaTopicInfo> listTopic(KafkaTopicDetail detail, String username, String password) {
        AdminClient client = getClient(detail.getBootstrapServers(), username, password);
        ListTopicsResult topics = client.listTopics();
        try {
            List<SimpleKafkaTopicInfo> res = new ArrayList<>();
            Set<String> names = topics.names().get();
            Map<String, TopicDescription> map = new TreeMap<>(client.describeTopics(names).all().get());
            for (Map.Entry<String, TopicDescription> each : map.entrySet()) {
                SimpleKafkaTopicInfo info = new SimpleKafkaTopicInfo();
                res.add(info);
                info.setName(each.getKey());
                // 虽然点了很多点 但其实很安全，都至少有一个元素
                info.setPartitions(each.getValue().partitions().size());
                info.setReplicationFactor(each.getValue().partitions().get(0).replicas().size());
            }
            return res;
        } catch (Exception e) {

        } finally {
            Closer.doFinal(client::close);
        }
        return null;
    }

    public KafkaTopicDetail getTopicDetail(KafkaTopicDetail detail, String username, String password) {
        AdminClient client = getClient(detail.getBootstrapServers(), username, password);
        DescribeTopicsResult descRes = client.describeTopics(Collections.singletonList(detail.getName()));

        try {
            Map<String, TopicDescription> descMap = descRes.all().get();
            TopicDescription desc = descMap.get(detail.getName());

            KafkaTopicDetail res = new KafkaTopicDetail();
            res.setName(detail.getName());
            res.setPartitions(desc.partitions().size());
            res.setReplicationFactor(desc.partitions().get(0).replicas().size());

            List<ConfigResource> resources = new ArrayList<>();
            ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, detail.getName());
            resources.add(resource);
            DescribeConfigsResult confDescRes = client.describeConfigs(resources);

            Map<ConfigResource, Config> confMap = confDescRes.all().get();
            Config config = confMap.get(resource);

            res.setRetentionMs(Long.parseLong(config.get(TopicConfig.RETENTION_MS_CONFIG).value()));
            res.setRetentionBytes(Long.parseLong(config.get(TopicConfig.RETENTION_BYTES_CONFIG).value()));

            return res;
        } catch (Exception e) {
        } finally {
            Closer.doFinal(client::close);
        }
        return null;
    }

    public void deleteTopic(KafkaTopicDetail detail, String username, String password) {
        AdminClient client = getClient(detail.getBootstrapServers(), username, password);
        DeleteTopicsResult res = client.deleteTopics(Collections.singletonList(detail.getName()));
        try {
            res.all().get();
        } catch (Exception e) {
        } finally {
            Closer.doFinal(client::close);
        }
    }

    public void createTopic(KafkaTopicDetail detail, String username, String password) {
        AdminClient client = getClient(detail.getBootstrapServers(), username, password);
        NewTopic newTopic = new NewTopic(detail.getName(), detail.getPartitions(), (short) detail.getReplicationFactor());
        Map<String, String> configs = new HashMap<>();
        if (detail.getRetentionMs() != 0) {
            configs.put(TopicConfig.RETENTION_MS_CONFIG, detail.getRetentionMs() + "");
        }
        if (detail.getRetentionBytes() != 0) {
            configs.put(TopicConfig.RETENTION_BYTES_CONFIG, detail.getRetentionBytes() + "");
        }
        if (!CollectionUtils.isEmpty(configs)) {
            newTopic.configs(configs);
        }
        CreateTopicsResult res = client.createTopics(Collections.singletonList(newTopic));
        try {
            res.all().get();
        } catch (Exception e) {
        } finally {
            Closer.doFinal(client::close);
        }
    }

    public void alterTopic(KafkaTopicDetail detail, String username, String password) {
        AdminClient client = getClient(detail.getBootstrapServers(), username, password);

        try {
            // 再改分区数
            if ("partition".equals(detail.getAlterType())) {
                NewPartitions np = NewPartitions.increaseTo(detail.getPartitions());
                Map<String, NewPartitions> partitionsMap = new HashMap<>();
                partitionsMap.put(detail.getName(), np);
                CreatePartitionsResult partitions = client.createPartitions(partitionsMap);

                try {
                    partitions.all().get();
                } catch (Exception e) {
                }
            }
            // 改其他配置
            else if ("config".equals(detail.getAlterType())) {
                Map<ConfigResource, Config> configMap = new HashMap<>();

                ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, detail.getName());
                Config config = new Config(Arrays.asList(new ConfigEntry(TopicConfig.RETENTION_MS_CONFIG, detail.getRetentionMs() + ""),
                        new ConfigEntry(TopicConfig.RETENTION_BYTES_CONFIG, detail.getRetentionBytes() + "")
                ));
                configMap.put(resource, config);

                try {
                    AlterConfigsResult res = client.alterConfigs(configMap);
                    res.all().get();
                } catch (Exception e) {
                }
            } else {
            }
        } finally {
            Closer.doFinal(client::close);
        }

    }

    private static final Pattern BROKER_PATTERN = Pattern.compile("(\\d{1,3}(.(\\d{1,3})){3}:\\d+)(,(\\d{1,3}(.(\\d{1,3})){3}:\\d+))*");

    public static AdminClient getClient(String brokers, String username, String password) {

        if (!BROKER_PATTERN.matcher(brokers).matches()) {
//            throw new CustomException(600, "broker 列表是不是逗号分隔的 ip:port 格式？");
        }
        String jaasTemplate = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";
        String jaasCfg = String.format(jaasTemplate, username, password);

        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "10000");
        if (!StringUtils.isEmpty(username)) {
            log.info("[getClient] username:{} ", username);
            props.put("security.protocol", SecurityProtocol.PLAINTEXT.name);
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasCfg);
            props.put("max.block.ms", "5000");
            props.put("transaction.timeout.ms", "5000");
        }
        try {
            AdminClient client = AdminClient.create(props);
            return client;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
