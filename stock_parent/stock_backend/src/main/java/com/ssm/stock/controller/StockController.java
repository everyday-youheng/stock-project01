package com.ssm.stock.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.ssm.stock.pojo.domain.*;
import com.ssm.stock.service.IStockService;
import com.ssm.stock.vo.resp.PageResult;
import com.ssm.stock.vo.resp.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 股票相关的接口控制器
 * web层只做参数的接收和响应，业务逻辑的处理交给service层
 */
@Api(value = "/api/quot", tags = {"股票相关的接口控制器 web层只做参数的接收和响应，业务逻辑的处理交给service层"})
@RestController
@RequestMapping("/api/quot")
public class StockController {

    @Autowired
    private IStockService stockService;



    /**
     * 获取 最新的 国内 大盘数据
     * @return
     */
    @ApiOperation(value = "获取最新的国内大盘数据", notes = "获取 最新的 国内 大盘数据", httpMethod = "GET")
    @GetMapping("/index/all")
    public R<List<InnerMarketDomain>> innerIndexAll(){
        return stockService.innerIndexAll();
    }
    /**
     * 统计大盘 T日和T-1日成交量对比，每分钟交易量的统计
     */
    @ApiOperation(value = "统计大盘 T日和T-1日成交量对比，每分钟交易量的统计", notes = "统计大盘 T日和T-1日成交量对比，每分钟交易量的统计", httpMethod = "GET")
    @GetMapping("/stock/tradeAmt")
    public R<Map<String,List>> getComparedStockTradeAmt(){

        return stockService.getComparedStockTradeAmt();
    }

    /**
     * 获取 最新的 国内 板块数据 前10条
     * @return
     */
    @ApiOperation(value = "获取 最新的 国内 板块数据 前10条", notes = "获取 最新的 国内 板块数据 前10条", httpMethod = "GET")
    @GetMapping("/sector/all")
    public R<List<SectorMarketDomain>> sectorIndexAll(){
        return stockService.sectorIndexAll();
    }

    /**
     * 涨幅榜 分页展示数据
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation("分页降序查询最新的个股涨幅排数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @GetMapping("/stock/all")
    public R<PageResult<StockUpdownDomain>> getPageStockInfos(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        return stockService.getPageStockInfos(page, pageSize);
    }

    /**
     * 涨幅榜 当前页的数据 导出到excel表格
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "1"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "20")
    })
    @ApiOperation(value = "涨幅榜 当前页的数据 导出到excel表格", notes = "涨幅榜 当前页的数据 导出到excel表格", httpMethod = "GET")
    @GetMapping("/stock/export")
    public void exportStockUpDownInfo(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            HttpServletResponse response
    ) {

        stockService.exportStockUpDownInfo(page, pageSize,response);
    }

    /**
     * 获取最新时间点 涨幅榜前4条数据
     * @return
     */
    @GetMapping("/stock/increase")
    public R<List<StockUpdownDomain>> getPageStockInfos(){
        return stockService.getPageStockIncrease();
    }

    /**
     * 涨停和跌停 数据统计展示
     * @return
     */
    @ApiOperation("统计最新交易日下股票在各个时间点涨跌停的数量")
    @GetMapping("/stock/updown/count")
    public R<Map<String,List>> getStockUpDownCount(){
        return stockService.getStockUpDownCount();
    }

    /**
     * 统计最新交易时间点下， 个股涨幅区间的数据(柱状图数据)
     */
    @GetMapping("/stock/updown")
    public R<Map> getStockUpDown(){
        return stockService.getIncreaseRangeInfo();
    }

    /**
     * 查询单个 个股的分时行情数据，也就是统计指定股票T日每分钟的交易数据；
     *   如果当前日期不在有效时间内，则以最近的一个股票交易时间作为查询时间点
     */
    @GetMapping("/stock/screen/time-sharing")
    public R<List<Stock4MinuteDomain>> stockScreenTimeSharing(
            @RequestParam(value = "code", required = true) String stockCode){
        return stockService.stockScreenTimeSharing(stockCode);
    }

    /**
     * 单个  个股日K 数据查询 ，可以根据时间区间查询数日的K线数据
     * @param stockCode 股票编码
     */
    @RequestMapping("/stock/screen/dkline")
    public R<List<Stock4EvrDayDomain>> getStockScreenDKline(@RequestParam("code") String stockCode){
        return stockService.getStockScreenDKline(stockCode);
    }



}


























