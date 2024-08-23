package com.ssm.stock.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 股票大盘编码 信息的封装
 */
//@ConfigurationProperties注解：将该类作为配置类，加载到Spring容器中
@ConfigurationProperties(prefix = "stock")
@Data
@ApiModel(description = "大盘编码和股票涨跌区间的类")
public class StockInfoConfig {
    /**
     *  A股大盘ID集合
     */
    @ApiModelProperty(value = "A股大盘ID集合")
    private List<String> inner;
    /**
     *  外盘ID集合
     */
    @ApiModelProperty(value = "外盘ID集合")
    private List<String> outer;

    /**
     *  股票涨跌区间标题的集合
     */
    @ApiModelProperty(value = "外盘ID集合")
    private List<String> upDownRange;

    //大盘和外盘 个股采集的url地址
    private String marketUrl;

    //板块采集的 url地址
    private String blockUrl;
}
