package com.ssm.stock.mapper;

import com.ssm.stock.pojo.domain.InnerMarketDomain;
import com.ssm.stock.pojo.entity.StockMarketIndexInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Entity com.ssm.stock.pojo.entity.StockMarketIndexInfo
 */
public interface StockMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockMarketIndexInfo record);

    int insertSelective(StockMarketIndexInfo record);

    StockMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockMarketIndexInfo record);

    int updateByPrimaryKey(StockMarketIndexInfo record);

    /**
     * 获取国内大盘的实时数据
     * @return
     */
    List<InnerMarketDomain> selectMarketInfo(@Param("mCodes") List<String> mCodes, @Param("curDate") Date curDate);

    /**
     * 统计大盘 T日和T-1日成交量对比，每分钟交易量的统计
     * @param tStartDate  起始时间  T日的开盘时间
     * @param tEndDate   截止时间   与起始时间同一天
     * @param inner  大盘编码合集
     * @return
     */
    List<Map> getStockTradeAmtInfo(@Param("openDate") Date tStartDate, @Param("endDate") Date tEndDate, @Param("marketCodes") List<String> inner);

    /**
     * 批量插入 大盘采集的数据
     * entityList：大盘实体对象集合
     */
    int insertBatch(@Param("infos") List<StockMarketIndexInfo> entityList);
}
