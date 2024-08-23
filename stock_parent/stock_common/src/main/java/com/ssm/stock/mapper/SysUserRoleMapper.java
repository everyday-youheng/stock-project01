package com.ssm.stock.mapper;

import com.ssm.stock.pojo.entity.SysUserRole;

/**
 * @Entity com.ssm.stock.pojo.entity.SysUserRole
 */
public interface SysUserRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);

}
