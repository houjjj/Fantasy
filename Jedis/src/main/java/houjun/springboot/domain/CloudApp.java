package houjun.springboot.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class CloudApp implements Serializable {
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private Integer id;

    private String name;

    private String crName;

    private Integer kubeId;

    private String namespace;

    private String kind;

    private String ipList;

    /**
     * 即创建人
     */
    private Integer ownerUser;

    /**
     * 创建人姓名
     */
    private String ownerName;

    /**
     * 创建人隶属租户ID
     */
    private Integer ownerTenant;

    /**
     * DB参数模板主键 (MySQL-mysqld.cnf)
     */
    private Integer dbParamTemplateId;

    @Data
    public static class IpNode {
        private String node;
        private String ip;

        public IpNode() {
        }

        public IpNode(String node, String ip) {
            this.node = node;
            this.ip = ip;
        }
    }

    public IpNode[] getIpNode() {
        if (StringUtils.isEmpty(ipList)) {
            return null;
        }
        IpNode[] ipNodes = null;
        try {
            ipNodes = objectMapper.readValue(ipList, IpNode[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(ipNodes, "cannot parse param:ipList");
    }

    private Integer readPort;

    private Integer writePort;

    /**
     * db容器资源申请量, 带单位
     */
    private String cpu;
    /**
     * db容器资源申请量, 带单位
     */
    private String memory;
    /**
     * db容器资源申请量, 带单位
     */
    private String disk;

    @JsonIgnore
    private String cr;
    @JsonIgnore
    private String crRun;

    private String version;

    private String arch;

    private Boolean deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 告警状态
     */
    private String alertStatus;

    /**
     * 操作状态
     */
    private String status;

    /**
     *持久化存储类名称
     */
    private String storageClassName;

    /**
     * 副本个数
     */
    private Integer members;

    /**
     * 分片个数
     */
    private Integer masterSize;

    /**
     * 备库个数
     */
    private Integer spareSize;

    /**
     * 纳管使用的nodeport集合
     */
    private String nodeportList;

    /**
     * kafka安装依赖的zookeeper
     */
    private Integer zookeeperId;
}