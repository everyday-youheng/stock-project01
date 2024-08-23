package com.ssm.stock.service;

import com.ssm.stock.pojo.entity.SysUser;
import com.ssm.stock.vo.req.LoginReqVo;
import com.ssm.stock.vo.resp.LoginRespVo;
import com.ssm.stock.vo.resp.R;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface IUserService {
    /**
     * 根据用户的名字，查询用户的信息
     * @param userName
     * @return
     */
    SysUser getSysUserByUserName(String userName);

    /**
     * 用户登录功能
     */
    R<LoginRespVo> login(LoginReqVo reqVo);

    /**
     * 生成图片验证码
     */
    R<Map> getCaptchaCode();
}
