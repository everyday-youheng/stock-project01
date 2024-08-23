package com.ssm.stock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 测试密码加密
 */
@SpringBootTest
public class TestPasswordEncoder {
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * @desc 测试密码机密匹配器
     * 测试加密
     */
    @Test
    public void testPasswordEncoder() {
        //明文密码
        String pwd="123456";
        for (int i = 0; i < 10; i++) {
            String encodePwd = passwordEncoder.encode(pwd);
            System.out.println(encodePwd);
        }
        /**
         $2a$10$CVcf71FGZasYdkQ1yEecwe1VnhRd/IEF15/0aroVqbDkohzCgUvsq
         $2a$10$Bft3WWA2WKofzaA5Owr.l.a.z/nb70di6V9WnraHtUfjHbObzOgrq
         $2a$10$zLIpoYgcJNKICIDen7qPUuNtRHGBUWYl64cT4HPizXCPBTvxiGCkm
         */
    }

    /**
     * @desc 解密：测试匹配
     * 底层原理：
     * 从密文中获取盐值（随件码，参与密文生成的运算）后，利用盐值与明文密码进行加密得到密文，
     * 这个密文与输入的密文等值匹配
     */
    @Test
    public void testDecode() {
        String encodePwd="$2a$10$kpngKp7J3q0vb1xfmzPYFOQzcWkU8YUrqNF6XpjimM7zG4l2ra9pi";
        encodePwd = "$2a$10$1q.M81.PEjzhLHXIkdkk2eYIlYfoiHq75B.K4jAHzuT4Soygo.l7i";
        encodePwd = "$2a$10$1e4tGS8LLBNBzTWQ7Pe6.emlrvryRY2udWWTT8kLBHdHKLbdrZcY2";
        String pwd="123456";
        boolean isSuccess = passwordEncoder.matches(pwd, encodePwd);
        System.out.println(isSuccess?"匹配成功":"匹配失败");
    }
}
