package com.ssm.stock.mapper;

import com.ssm.stock.pojo.entity.StockBusiness;
import com.ssm.stock.pojo.entity.StockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.ssm.stock.pojo.entity.StockBusiness
 */
public interface StockBusinessMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockBusiness record);

    int insertSelective(StockBusiness record);

    StockBusiness selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBusiness record);

    int updateByPrimaryKey(StockBusiness record);

    /**
     * 从数据库中 获取所有股票的编码
     * @return
     */
    List<String> getAllStockCodes();
    /**
     * 将个股采集的数据 批量入库
     */
    int insertBatch(@Param("list") List<StockRtInfo> list);



}
