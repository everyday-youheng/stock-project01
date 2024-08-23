package com.ssm.stock.config;

import com.ssm.stock.pojo.vo.StockInfoConfig;
import com.ssm.stock.utils.IdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 定义公共的配置类
 */
@Configuration
//@EnableConfigurationProperties:开启指定配置类的加载
//开启StockInfoConfig配置类的加载
@EnableConfigurationProperties({StockInfoConfig.class})
public class CommonConfig {
    /**
     * 密码加密匹配器的 bean
     * 作用：加密密码，然后存入数据库
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

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
}
