package com.ssm.stock.mapper;

import com.ssm.stock.pojo.entity.SysRole;

/**
 * @Entity com.ssm.stock.pojo.entity.SysRole
 */
public interface SysRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

}
