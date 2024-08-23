package com.ssm.stock.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 国内大盘数据详情表
 * @TableName stock_market_index_info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMarketIndexInfo implements Serializable {
    /**
     * 主键字段（无业务意义）
     */
    private Long id;

    /**
     * 大盘编码
     */
    private String marketCode;

    /**
     * 指数名称
     */
    private String marketName;

    /**
     * 前收盘点数
     */
    private BigDecimal preClosePoint;

    /**
     * 开盘点数
     */
    private BigDecimal openPoint;

    /**
     * 当前点数
     */
    private BigDecimal curPoint;

    /**
     * 最低点数
     */
    private BigDecimal minPoint;

    /**
     * 最高点数
     */
    private BigDecimal maxPoint;

    /**
     * 成交量(手)
     */
    private Long tradeAmount;

    /**
     * 成交金额（元）
     */
    private BigDecimal tradeVolume;

    /**
     * 当前时间
     */
    private Date curTime;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        StockMarketIndexInfo other = (StockMarketIndexInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getMarketCode() == null ? other.getMarketCode() == null : this.getMarketCode().equals(other.getMarketCode()))
            && (this.getMarketName() == null ? other.getMarketName() == null : this.getMarketName().equals(other.getMarketName()))
            && (this.getPreClosePoint() == null ? other.getPreClosePoint() == null : this.getPreClosePoint().equals(other.getPreClosePoint()))
            && (this.getOpenPoint() == null ? other.getOpenPoint() == null : this.getOpenPoint().equals(other.getOpenPoint()))
            && (this.getCurPoint() == null ? other.getCurPoint() == null : this.getCurPoint().equals(other.getCurPoint()))
            && (this.getMinPoint() == null ? other.getMinPoint() == null : this.getMinPoint().equals(other.getMinPoint()))
            && (this.getMaxPoint() == null ? other.getMaxPoint() == null : this.getMaxPoint().equals(other.getMaxPoint()))
            && (this.getTradeAmount() == null ? other.getTradeAmount() == null : this.getTradeAmount().equals(other.getTradeAmount()))
            && (this.getTradeVolume() == null ? other.getTradeVolume() == null : this.getTradeVolume().equals(other.getTradeVolume()))
            && (this.getCurTime() == null ? other.getCurTime() == null : this.getCurTime().equals(other.getCurTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getMarketCode() == null) ? 0 : getMarketCode().hashCode());
        result = prime * result + ((getMarketName() == null) ? 0 : getMarketName().hashCode());
        result = prime * result + ((getPreClosePoint() == null) ? 0 : getPreClosePoint().hashCode());
        result = prime * result + ((getOpenPoint() == null) ? 0 : getOpenPoint().hashCode());
        result = prime * result + ((getCurPoint() == null) ? 0 : getCurPoint().hashCode());
        result = prime * result + ((getMinPoint() == null) ? 0 : getMinPoint().hashCode());
        result = prime * result + ((getMaxPoint() == null) ? 0 : getMaxPoint().hashCode());
        result = prime * result + ((getTradeAmount() == null) ? 0 : getTradeAmount().hashCode());
        result = prime * result + ((getTradeVolume() == null) ? 0 : getTradeVolume().hashCode());
        result = prime * result + ((getCurTime() == null) ? 0 : getCurTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", marketCode=").append(marketCode);
        sb.append(", marketName=").append(marketName);
        sb.append(", preClosePoint=").append(preClosePoint);
        sb.append(", openPoint=").append(openPoint);
        sb.append(", curPoint=").append(curPoint);
        sb.append(", minPoint=").append(minPoint);
        sb.append(", maxPoint=").append(maxPoint);
        sb.append(", tradeAmount=").append(tradeAmount);
        sb.append(", tradeVolume=").append(tradeVolume);
        sb.append(", curTime=").append(curTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}