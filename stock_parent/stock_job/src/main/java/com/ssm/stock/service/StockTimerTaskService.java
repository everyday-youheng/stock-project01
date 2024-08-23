package com.ssm.stock.service;

/**
 * @Description 定义股票采集的服务接口  定时任务
 */
public interface StockTimerTaskService {
    /**
     * 大盘 数据的采集
     * 方法内采集完数据，直接存入数据库。使用不需要返回。
     */
    void getInnerMarketInfo();
    /**
     * 个股 数据的采集
     * 方法内采集完数据，直接存入数据库。使用不需要返回。
     */
    void getStockRtIndex();
}
