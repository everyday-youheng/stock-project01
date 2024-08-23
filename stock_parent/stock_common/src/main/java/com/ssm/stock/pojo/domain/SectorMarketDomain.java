package com.ssm.stock.pojo.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 *  国内 板块 数据的实体类
 */
@ApiModel(description = "国内 板块 数据的实体类")
@Data
public class SectorMarketDomain {
    /**
     * 公司数量
     */
    private String companyNum;
    /**
     * 交易量
     */
    private String tradeAmt;
    /**
     * 板块编码
     */
    private String code;
    /**
     *平均价格
     */
    private String avgPrice;
    /**
     *板块名称
     */
    private String name;
    /**
     * 当前时间
     * @JsonFormat：将 Date格式的时间，序列化成指定格式的字符串
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date curTime;
    /**
     *交易总金额
     */
    private String tradeVol;
    /**
     *涨幅
     */
    private String updownRate;
}
