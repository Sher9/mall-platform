package com.example.common.config;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * RocketMQ 条件配置入口
 * <p>
 * 通过 rocketmq.enabled 控制是否启用 RocketMQ，默认关闭。
 * 关闭时不导入 RocketMQAutoConfiguration，不创建 RocketMQTemplate、不启动消费者容器，
 * 这样在没有 RocketMQ 服务时子系统也能正常启动。
 * </p>
 * <p>
 * 需要在各子系统的 AutoConfiguration.imports 中注册本类，
 * 并从 imports 中移除 RocketMQAutoConfiguration。
 * </p>
 */
@Configuration
@ConditionalOnProperty(name = "rocketmq.enabled", havingValue = "true", matchIfMissing = false)
@Import(RocketMQAutoConfiguration.class)
public class RocketMQConditionAutoConfiguration {
}
