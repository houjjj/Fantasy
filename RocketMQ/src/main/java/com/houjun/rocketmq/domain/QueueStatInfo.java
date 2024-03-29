/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.houjun.rocketmq.domain;

import lombok.Data;
import org.apache.rocketmq.common.admin.OffsetWrapper;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.BeanUtils;

@Data
public class QueueStatInfo {
    private String brokerName;
    private int queueId;
    private long brokerOffset;
    private long consumerOffset;
    private String clientInfo;
    private long lastTimestamp;

    public static QueueStatInfo fromOffsetTableEntry(MessageQueue key, OffsetWrapper value) {
        QueueStatInfo queueStatInfo = new QueueStatInfo();
        BeanUtils.copyProperties(key, queueStatInfo);
        BeanUtils.copyProperties(value, queueStatInfo);
        return queueStatInfo;
    }
}
