package io.github.yeyuhl.novel.core.config;

import io.github.yeyuhl.novel.core.constant.AmqpConsts;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AMQP配置类
 *
 * @author yeyuhl
 * @date 2023/5/8
 */
@Configuration
public class AmqpConfig {

    /**
     * 小说信息改变交换机
     */
    @Bean
    public FanoutExchange bookChangeExchange() {
        return new FanoutExchange(AmqpConsts.BookChangeMq.EXCHANGE_NAME);
    }

    /**
     * Elasticsearch book 索引更新队列
     */
    @Bean
    public Queue esBookUpdateQueue() {
        return new Queue(AmqpConsts.BookChangeMq.QUEUE_ES_UPDATE);
    }

    /**
     * Elasticsearch book 索引更新队列绑定到小说信息改变交换机
     */
    @Bean
    public Binding esBookUpdateQueueBinding() {
        return BindingBuilder.bind(esBookUpdateQueue()).to(bookChangeExchange());
    }

}
