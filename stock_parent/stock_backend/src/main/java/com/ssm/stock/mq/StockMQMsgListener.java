package com.ssm.stock.mq;

import com.github.benmanes.caffeine.cache.Cache;
import com.ssm.stock.service.IStockService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author everyDay.youHeng
 * @date 2024年08月21 20:08
 *
 * 监听股票变化消息
 */
@Component
@Slf4j
public class StockMQMsgListener {

    @Autowired
    private Cache<String,Object> caffeineCache;

    @Autowired
    private IStockService stockService;


    /**
     *
     */
    @RabbitListener(queues = "innerMarketQueue")
    public void refreshInnerMarketInfo(Date startTime)throws Exception{
        //获取时间毫秒差值
        long diffTime= DateTime.now().getMillis()-new DateTime(startTime).getMillis();
        //超过一分钟告警
        if (diffTime>60000) {
            log.error("采集国内大盘时间点：{},同步超时：{}ms",new DateTime(startTime).toString("yyyy-MM-dd HH:mm:ss"),diffTime);
        }
        // 刷新缓存
        //将缓存置为失效删除
        caffeineCache.invalidate("innerMarketKey");

        //调用服务更新缓存
        stockService.innerIndexAll();
    }

}
