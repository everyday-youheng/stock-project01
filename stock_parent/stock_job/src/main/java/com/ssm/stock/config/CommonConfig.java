package com.ssm.stock.config;

import com.ssm.stock.pojo.vo.StockInfoConfig;
import com.ssm.stock.utils.IdWorker;
import com.ssm.stock.utils.ParserStockInfoUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定义公共的配置类
 */
@Configuration
//@EnableConfigurationProperties:开启指定配置类的加载
//开启StockInfoConfig配置类的加载
@EnableConfigurationProperties({StockInfoConfig.class})
public class CommonConfig {

    /**
     * 雪花算法的 Bean
     * 基于雪花算法，保证生成的id唯一
     *    用于生成sessionID
     * @return
     */
    @Bean
    public IdWorker idWorker(){
        /**
         * 参数1：机器id
         * 参数2：机房id
         */
        return new IdWorker(1l,2l);
    }

    /**
     * 解析股票采集数据 的工具类 bean
     * @param idWorker
     * @return
     */
    @Bean
    public ParserStockInfoUtil parserStockInfoUtil(IdWorker idWorker){
        return new ParserStockInfoUtil(idWorker);
    }
}
