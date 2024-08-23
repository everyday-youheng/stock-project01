package com.ssm.stock.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.ssm.stock.constant.StockConstant;
import com.ssm.stock.mapper.SysUserMapper;
import com.ssm.stock.pojo.entity.SysUser;
import com.ssm.stock.service.IUserService;
import com.ssm.stock.utils.IdWorker;
import com.ssm.stock.vo.req.LoginReqVo;
import com.ssm.stock.vo.resp.LoginRespVo;
import com.ssm.stock.vo.resp.R;
import com.ssm.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author everyDay.youHeng
 * @date 2024年08月14 23:46
 */
@Service("userService")
//@Slf4j 日志输出、调试
@Slf4j
public class UserServiceImpl implements IUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    /**
     * 注入密码加密匹配器
     */
    private PasswordEncoder passwordEncoder;
    /**
     * 雪花算法工具类
     *  生成唯一的SessionID，保证分布式环境下id唯一
     */
    @Autowired
    private IdWorker idWorker;
    /**
     * redis模板对象
     */
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public SysUser getSysUserByUserName(String userName) {
        SysUser user = sysUserMapper.selectSysUserByUserName(userName);
        return user;
    }

    @Override
    public R<LoginRespVo> login(LoginReqVo reqVo) {
        //1. 判断参数是否合法 - 非空判断
        //用户名、密码是否为空
        if (reqVo==null || StringUtils.isBlank(reqVo.getUsername()) || StringUtils.isBlank(reqVo.getPassword())) {
            //请求参数有问题
            return R.error(ResponseCode.DATA_ERROR);
        }
        //验证码 或 SessionID 是否为空
        if(StringUtils.isBlank(reqVo.getCode())||StringUtils.isBlank(reqVo.getSessionId())){
            return R.error(ResponseCode.CHECK_CODE_NOT_EMPTY);
        }

        //2. 判断redis中的验证码 和 输入的验证码是否匹配
        //从redis中获取验证码
        ValueOperations stringOperations = redisTemplate.opsForValue();
        String checkCode=(String)stringOperations.get(StockConstant.CHECK_PREFIX+reqVo.getSessionId());
        if(StringUtils.isBlank(checkCode)){
            //如果为空，说明已经过期
            return R.error(ResponseCode.CHECK_CODE_OUT_TIME);
        }
        //匹配验证码
        if(!checkCode.equalsIgnoreCase(reqVo.getCode())){
            //验证码不匹配
            return R.error(ResponseCode.CHECK_CODE_ERROR);
        }

        //3. 根据用户名查询用户信息
        SysUser dbUser = sysUserMapper.selectSysUserByUserName(reqVo.getUsername());
        //判断用户是否存在、密码是否匹配
        if (dbUser==null || ! passwordEncoder.matches(reqVo.getPassword(),dbUser.getPassword())) {
            //用户名不存在 或 密码错误
            return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }

        //4.响应数据给前端
        //构建响应对象
        LoginRespVo respVo = new LoginRespVo();
        // 不使用工具类操作
        // respVo.setId(dbUser.getId());
        // respVo.setNickName(dbUser.getNickName());

        //使用工具类
        //作用：将一个对象的所有属性值 复制给另一个不同类的对象
        //要求：实体类的属性名称和类型 与另一个实体类完全一致，使用工具类复制
        //我们发现respVo与dbUser下具有相同的属性，所以直接复制即可

        BeanUtils.copyProperties(dbUser,respVo);
        return R.ok(respVo);
    }

    @Override
    public R<Map> getCaptchaCode() {
        //1. 生成验证码图片对象。使用hutool工具包。
        /**
         * 参数1：图片宽度
         * 参数2：图片高度
         * 参数3：图片包含验证码的长度
         * 参数4：干扰线的数量
         */
        //验证码的 图片对象
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(250, 40, 4, 5);
        captcha.setBackground(Color.LIGHT_GRAY);//设置图片的背景颜色
        //自定义生成校验码的规则
        //captcha.getGenerator()  需要重写方法

        //通过 图片对象 获取验证码
        String checkCode = captcha.getCode();
        //获取经过base64 编码 处理过的图片数据 (就是将图片以字符串形式表示)
        String imageData = captcha.getImageBase64();

        //2. 生成唯一的SessionID。使用雪花算法。
        //SessionID转换成字符串，避免前端精度丢失
        String sessionId = String.valueOf(idWorker.nextId());
        log.info("当前生成的校验码：{}, SessionId：{}",checkCode,sessionId);

        //3. 保存到redis中
        /**
         *  将SessionID作为key,验证码作为value,保存到redis中
         *  使用redis模拟session的行为，过期时间位5分钟
         */
        ValueOperations stringOperations = redisTemplate.opsForValue();
        //key：StockConstant.CHECK_PREFIX+sessionId
        //value：checkCode
        //过期时间：5分钟
        //时间单位：TimeUnit.MINUTES表示分钟
        stringOperations.set(StockConstant.CHECK_PREFIX+sessionId,checkCode,5, TimeUnit.MINUTES);

        //4. 封装响应数据
        Map<String,String> data=new HashMap();
        data.put("imageData",imageData);
        data.put("sessionId",sessionId);
        //5. 返回响应数据
        return R.ok(data);
    }
}
