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
import org.apache.rocketmq.common.protocol.heartbeat.ConsumeType;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

@Data
public class ConsumeGroupInfo implements Comparable<ConsumeGroupInfo> {
    private String group;
    private String version;
    private int count;
    private ConsumeType consumeType;
    private MessageModel messageModel;
    private int consumeTps;
    private long diffTotal = -1;
    
    private boolean sysConsumerGroup;
    
    private String broker;

    @Override
    public int compareTo(ConsumeGroupInfo o) {
        if (this.count != o.count) {
            return Integer.compare(o.count, this.count);
        }
        return Long.compare(o.diffTotal, this.diffTotal);
    }
}
