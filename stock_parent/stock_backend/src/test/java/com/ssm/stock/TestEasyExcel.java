package com.ssm.stock;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ssm.stock.pojo.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 测试Excel表格导出
 */
public class TestEasyExcel {
    /**
     * 导出Excel表格
     */
    @Test
    public void test01(){
        List<User> listUser = init();

        //不做任何注解处理时，表头名称与实体类属性名称一致
        EasyExcel.write(
                "F:\\data\\用户.xls",
                User.class).sheet("用户信息").doWrite(listUser);

    }


    public List<User> init(){
        //组装数据
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setAddress("上海"+i);
            user.setUserName("张三"+i);
            user.setBirthday(new Date());
            user.setAge(10+i);
            users.add(user);
        }
        return users;
    }

    /**
     * 导入excel表格
     * excel数据格式必须与实体类定义一致，否则数据读取不到
     */
    @Test
    public void readExcel(){
        ArrayList<User> users = new ArrayList<>();
        //读取数据
        EasyExcel.read("F:\\data\\用户.xls", User.class, new AnalysisEventListener<User>() {
            /**
             * 逐行读取
             * @param o
             * @param analysisContext
             */
            @Override
            public void invoke(User o, AnalysisContext analysisContext) {
                System.out.println(o);
                //业务操作
                users.add(o);
            }

            /**
             * 所有行读取完毕后，回调的方法（读取完成的通知）
             * @param analysisContext
             */
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("表格读取完成。。。。");
            }
        }).sheet("用户信息").doRead();
        //sheet指定读取表格中的哪个列表

        System.out.println(users);
    }


}
