package com.ssm.stock.mapper;

import com.ssm.stock.pojo.domain.SectorMarketDomain;
import com.ssm.stock.pojo.entity.StockBlockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Entity com.ssm.stock.pojo.entity.StockBlockRtInfo
 */
public interface StockBlockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockBlockRtInfo record);

    int insertSelective(StockBlockRtInfo record);

    StockBlockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBlockRtInfo record);

    int updateByPrimaryKey(StockBlockRtInfo record);

    /**
     * 获取 最新的 国内 板块数据 前10条
     * @return
     */
    List<SectorMarketDomain> selectSectorInfo(@Param("curDate") Date curDate);




}
