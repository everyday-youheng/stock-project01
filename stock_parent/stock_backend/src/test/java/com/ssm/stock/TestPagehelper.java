package com.ssm.stock;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssm.stock.mapper.SysUserMapper;
import com.ssm.stock.pojo.entity.SysUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 测试 Pagehelper 分页插件
 */
@SpringBootTest
public class TestPagehelper {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Test
    public void test01(){
        //设置分页参数 ，开启分页功能
        Integer page=2; //当前页
        Integer pageSize=5;//每页大小
        PageHelper.startPage(page,pageSize);
        //正常的查询
        List<SysUser> data = sysUserMapper.selectSysUserAll();
        //将查询结果封装到PageInfo中，就可以获取分页的各种数据
        PageInfo<SysUser> pageInfo = new PageInfo<>(data);
        pageInfo.getPageNum();//获取当前页码
        pageInfo.getPages();//获取总页数
        pageInfo.getPageSize();//每页大小
        pageInfo.getSize();//当前页的记录数
        pageInfo.getTotal();//获取总记录数
        List<SysUser> list = pageInfo.getList();//获取当前页的具体内容

    }

}
