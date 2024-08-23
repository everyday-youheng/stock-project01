package com.ssm.stock.mapper;

import com.ssm.stock.pojo.entity.StockOuterMarketIndexInfo;

/**
 * @Entity com.ssm.stock.pojo.entity.StockOuterMarketIndexInfo
 */
public interface StockOuterMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockOuterMarketIndexInfo record);

    int insertSelective(StockOuterMarketIndexInfo record);

    StockOuterMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockOuterMarketIndexInfo record);

    int updateByPrimaryKey(StockOuterMarketIndexInfo record);

}
