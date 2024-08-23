package com.ssm.stock.mapper;

import com.ssm.stock.pojo.domain.Stock4EvrDayDomain;
import com.ssm.stock.pojo.domain.Stock4MinuteDomain;
import com.ssm.stock.pojo.domain.StockUpdownDomain;
import com.ssm.stock.pojo.entity.StockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Entity com.ssm.stock.pojo.entity.StockRtInfo
 */
public interface StockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockRtInfo record);

    int insertSelective(StockRtInfo record);

    StockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockRtInfo record);

    int updateByPrimaryKey(StockRtInfo record);

    /**
     * 涨幅榜查看更多 分页展示数据
     */
    List<StockUpdownDomain> getStockUpDownInfos(@Param("lastDate") Date lastDate);
    /**
     * 涨幅榜 展示4条数据
     */
    List<StockUpdownDomain> getStockUpDownIncrease(Date lastDate);

    /**
     * 查询指定日期范围内股票的涨停或者跌停的统计数量
     * @param startTime 开始时间，一般是开盘时间
     * @param endTime 截止时间
     * @param flag 涨跌停表示，1：涨停 0：跌停
     * @return
     */
    List<Map> getStockUpdownCount(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("flag") int flag);

    /**
     * 统计最新交易时间点下， 个股涨幅区间的数据(柱状图数据)
     */
    List<Map> getIncreaseRangeInfoByDate(@Param("curDate") Date curDate);
    /**
     * 查询单个 个股的分时行情数据，也就是统计指定股票T日每分钟的交易数据；
     *   如果当前日期不在有效时间内，则以最近的一个股票交易时间作为查询时间点
     */
    List<Stock4MinuteDomain> getStock4MinuteInfo(@Param("tStartDate") Date tStartDate, @Param("tEndDate") Date tEndDate, @Param("stockCode") String stockCode);

    /**
     * 单个  个股日K 数据查询 ，可以根据时间区间查询数日的K线数据
     * @param stockCode 股票编码
     */
    List<Stock4EvrDayDomain> getStockInfo4EvrDay(@Param("stockCode") String stockCode, @Param("tStartDate") Date tStartDate, @Param("tEndDate") Date tEndDate);
}
