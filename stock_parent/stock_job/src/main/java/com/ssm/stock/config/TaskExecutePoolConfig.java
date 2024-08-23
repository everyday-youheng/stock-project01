package com.ssm.stock.config;

import com.ssm.stock.pojo.vo.TaskThreadPoolInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author everyDay.youHeng
 * @date 2024年08月23 10:50
 *
 * 线程池配置类。维护和线程池相关的配置
 */
@Configuration
@EnableConfigurationProperties(TaskThreadPoolInfo.class)
@Slf4j
public class TaskExecutePoolConfig {
    @Autowired
    private TaskThreadPoolInfo info;

    public TaskExecutePoolConfig(TaskThreadPoolInfo info) {
        this.info = info;
    }

    /**
     * 定义任务执行器
     * @return
     */
    @Bean(name = "threadPoolTaskExecutor",destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        //构建线程池对象
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //1. 设置核心线程数
        //   核心线程数：核心线程数（获取硬件）：线程池创建时候初始化的线程数
        taskExecutor.setCorePoolSize(info.getCorePoolSize());
        //2. 这种最大线程数
        //   最大线程数：只有在缓冲队列满了之后才会申请超过核心线程数的线程
        taskExecutor.setMaxPoolSize(info.getMaxPoolSize());
        //3. 设置空闲线程最大的存活时间
        //   允许线程的空闲时间：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        taskExecutor.setKeepAliveSeconds(info.getKeepAliveSeconds());
        //4. 设置任务对列长度
        //   缓冲队列：用来缓冲执行任务的队列
        taskExecutor.setQueueCapacity(info.getQueueCapacity());
        //5. 设置拒绝策略 -  暂时先不关注
        //taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        //线程名称前缀
        taskExecutor.setThreadNamePrefix("StockThread-");

        //将上面设置的参数初始化
        taskExecutor.initialize();
        return taskExecutor;//返回线程池对象
    }
}