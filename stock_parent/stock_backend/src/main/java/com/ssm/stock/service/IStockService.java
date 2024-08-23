package com.ssm.stock.service;

import com.ssm.stock.pojo.domain.*;
import com.ssm.stock.vo.resp.PageResult;
import com.ssm.stock.vo.resp.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 股票服务的接口
 */
public interface IStockService {
    /**
     * 获取国内大盘的实时数据
     * @return
     */
    R<List<InnerMarketDomain>> innerIndexAll();

    /**
     * 获取 最新的 国内 板块数据 前10条
     * @return
     */
    R<List<SectorMarketDomain>> sectorIndexAll();

    /**
     * 涨幅榜 分页展示数据
     * @param page
     * @param pageSize
     * @return
     */
    R<PageResult<StockUpdownDomain>> getPageStockInfos(Integer page, Integer pageSize);

    /**
     * 获取最新时间点 涨幅榜前4条数据
     * @return
     */
    R<List<StockUpdownDomain>> getPageStockIncrease();

    /**
     * 最新时间点 涨停和跌停 数据统计展示
     * @return
     */
    R<Map<String, List>> getStockUpDownCount();

    /**
     * 涨幅榜数据 导出当前页 到excel表格
     */
    void exportStockUpDownInfo(Integer page, Integer pageSize, HttpServletResponse response);

    /**
     * 统计大盘 T日和T-1日成交量对比，每分钟交易量的统计
     */
    R<Map<String, List>> getComparedStockTradeAmt();
    /**
     * 统计最新交易时间点下， 个股涨幅区间的数据(柱状图数据)
     */
    R<Map> getIncreaseRangeInfo();
    /**
     * 查询单个 个股的分时行情数据，也就是统计指定股票T日每分钟的交易数据；
     *   如果当前日期不在有效时间内，则以最近的一个股票交易时间作为查询时间点
     */
    R<List<Stock4MinuteDomain>> stockScreenTimeSharing(String stockCode);
    /**
     * 单个  个股日K 数据查询 ，可以根据时间区间查询数日的K线数据
     * @param stockCode 股票编码
     */
    R<List<Stock4EvrDayDomain>> getStockScreenDKline(String stockCode);
}