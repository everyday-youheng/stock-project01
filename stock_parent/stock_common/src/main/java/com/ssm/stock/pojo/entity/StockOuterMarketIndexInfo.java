package com.ssm.stock.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 外盘详情信息表
 * @TableName stock_outer_market_index_info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockOuterMarketIndexInfo implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 大盘编码
     */
    private String marketCode;

    /**
     * 大盘名称
     */
    private String marketName;

    /**
     * 大盘当前点
     */
    private BigDecimal curPoint;

    /**
     * 大盘涨跌值
     */
    private BigDecimal updown;

    /**
     * 大盘涨幅
     */
    private BigDecimal rose;

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
        StockOuterMarketIndexInfo other = (StockOuterMarketIndexInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getMarketCode() == null ? other.getMarketCode() == null : this.getMarketCode().equals(other.getMarketCode()))
            && (this.getMarketName() == null ? other.getMarketName() == null : this.getMarketName().equals(other.getMarketName()))
            && (this.getCurPoint() == null ? other.getCurPoint() == null : this.getCurPoint().equals(other.getCurPoint()))
            && (this.getUpdown() == null ? other.getUpdown() == null : this.getUpdown().equals(other.getUpdown()))
            && (this.getRose() == null ? other.getRose() == null : this.getRose().equals(other.getRose()))
            && (this.getCurTime() == null ? other.getCurTime() == null : this.getCurTime().equals(other.getCurTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getMarketCode() == null) ? 0 : getMarketCode().hashCode());
        result = prime * result + ((getMarketName() == null) ? 0 : getMarketName().hashCode());
        result = prime * result + ((getCurPoint() == null) ? 0 : getCurPoint().hashCode());
        result = prime * result + ((getUpdown() == null) ? 0 : getUpdown().hashCode());
        result = prime * result + ((getRose() == null) ? 0 : getRose().hashCode());
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
        sb.append(", curPoint=").append(curPoint);
        sb.append(", updown=").append(updown);
        sb.append(", rose=").append(rose);
        sb.append(", curTime=").append(curTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}