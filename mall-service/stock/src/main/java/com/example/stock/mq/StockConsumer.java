package com.example.stock.mq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "rocketmq.enabled", havingValue = "true")
@RocketMQMessageListener(topic = "order-topic", consumerGroup = "stock-order-consumer-group")
public class StockConsumer implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(StockConsumer.class);

    @Override
    public void onMessage(String message) {
        log.info("=== 库存服务收到订单消息: {} ===", message);
    }
}
