package com.ssm.stock.jobhandler;


import com.ssm.stock.service.StockTimerTaskService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author everyDay.youHeng
 * @date 2024年08月22 15:31
 * 定义股票相关数据的定时任务
 */
@Component
public class StockJob {

 /**
  * 注入股票定时任务服务bean
  */
 @Autowired
 private StockTimerTaskService stockTimerTaskService; //数据采集的接口
    /**
     * 自己编写的定时任务 测试集成是否成功
     */
     @XxlJob("stock_job_test")
     public void jobTest(){
     System.out.println("StockJob当前时间："+ new Date().toLocaleString());
     }
    /**
     * 定时采集 国内大盘数据
     */
    @XxlJob("getInnerMarketInfo")
    public void getInnerMarketInfo(){
     stockTimerTaskService.getInnerMarketInfo();
    }

    /**
     * 定时采集 个股数据
     */
    @XxlJob("getStockRtIndex")
    public void getStockRtIndex(){
        stockTimerTaskService.getStockRtIndex();
    }

 }