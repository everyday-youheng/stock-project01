package com.ssm.stock.mapper;

import com.ssm.stock.pojo.entity.SysLog;

/**
 * @Entity com.ssm.stock.pojo.entity.SysLog
 */
public interface SysLogMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

}
