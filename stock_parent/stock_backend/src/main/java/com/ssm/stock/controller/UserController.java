package com.ssm.stock.controller;

import com.ssm.stock.pojo.entity.SysUser;
import com.ssm.stock.service.IUserService;
import com.ssm.stock.vo.req.LoginReqVo;
import com.ssm.stock.vo.resp.LoginRespVo;
import com.ssm.stock.vo.resp.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author everyDay.youHeng
 * @date 2024年08月14 23:50
 */
@RestController
@RequestMapping("/api")
@Api(tags = "用户相关的接口控制器", value = "/api/user")
public class UserController {
    @Autowired
    private IUserService userService;

    /**
     * 根据用户名查询用户信息
     * @param userName
     * @return
     */
    @GetMapping("/user/{userName}")
    public SysUser getUserByUserName(@PathVariable("userName") String userName){
        return userService.getSysUserByUserName(userName);
    }
    /**
     * 用户登录功能
     * @param reqVo
     * @return
     */
    @PostMapping("/login")
    public R<LoginRespVo> login(@RequestBody LoginReqVo reqVo){
        return userService.login(reqVo);
    }
    /**
     * 生成图片验证码
     */
    @GetMapping("/captcha")
    public R<Map> getCaptchaCode(){
        return userService.getCaptchaCode();
    }



}
