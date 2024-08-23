package com.ssm.stock.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author everyDay.youHeng
 * @date 2024年08月21 15:29
 *
 * 定义交换机、队列、消息转化资源bean
 */
@Configuration
public class MqConfig {
    /**
     * 基于json格式的序列化和反序列化
     *      MQ默认使用的是JDK方式的序列化。需要重新定义消息序列化的方式为json格式
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    /**
     * 定义交换机：路由股票信息的交换机
     * @return
     */
    @Bean
    public TopicExchange stockExchange(){
        return new TopicExchange("stockExchange",true,false);
    }
    /**
     * 定义队列：国内大盘信息队列
     * @return
     */
    @Bean
    public Queue innerMarketQueue(){
        return new Queue("innerMarketQueue",true);
    }

    /**
     * 绑定队列到指定交换机
     * 绑定innerMarketQueue这个对列到stockExchange这个交换机
     * @return
     */
    @Bean
    public Binding bindingInnerMarketExchange(){
        return BindingBuilder
                .bind(innerMarketQueue()).to(stockExchange())
                .with("inner.market");
    }
}