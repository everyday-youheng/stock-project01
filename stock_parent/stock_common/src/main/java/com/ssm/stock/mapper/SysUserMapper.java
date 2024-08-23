package com.ssm.stock.mapper;

import com.ssm.stock.pojo.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.ssm.stock.pojo.entity.SysUser
 */
public interface SysUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    /**
     * 根据用户的名字，查询用户的信息
     * @param userName
     * @return
     */
    SysUser selectSysUserByUserName(@Param("userName") String userName);

    /**
     * 查询所有的用户的息
     * @return
     */
    List<SysUser> selectSysUserAll();

}
