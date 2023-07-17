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
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.BeanUtils;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Data
public class RocketmqMessageView {

    /**
     * from MessageExt
     **/
    private int queueId;
    private int storeSize;
    private long queueOffset;
    private int sysFlag;
    private long bornTimestamp;
    private SocketAddress bornHost;
    private long storeTimestamp;
    private SocketAddress storeHost;
    private String msgId;
    private long commitLogOffset;
    private int bodyCRC;
    private int reconsumeTimes;
    private long preparedTransactionOffset;
    /**from MessageExt**/

    /**
     * from Message
     **/
    private String topic;
    private int flag;
    private Map<String, String> properties;
    private String messageBody; // body

    /**
     * from Message
     **/

    public static RocketmqMessageView fromMessageExt(MessageExt messageExt) throws UnsupportedEncodingException {
        RocketmqMessageView rocketmqMessageView = new RocketmqMessageView();
        BeanUtils.copyProperties(messageExt, rocketmqMessageView);
        if (messageExt.getBody() != null) {
            rocketmqMessageView.setMessageBody(new String(messageExt.getBody(), StandardCharsets.UTF_8));
        }
        return rocketmqMessageView;
    }

}
