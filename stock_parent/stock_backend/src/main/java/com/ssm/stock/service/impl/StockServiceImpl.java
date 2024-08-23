package com.ssm.stock.service.impl;

import com.alibaba.excel.EasyExcel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssm.stock.mapper.StockBlockRtInfoMapper;
import com.ssm.stock.mapper.StockMarketIndexInfoMapper;
import com.ssm.stock.mapper.StockRtInfoMapper;
import com.ssm.stock.pojo.domain.*;
import com.ssm.stock.pojo.vo.StockInfoConfig;
import com.ssm.stock.service.IStockService;
import com.ssm.stock.utils.DateTimeUtil;
import com.ssm.stock.vo.resp.PageResult;
import com.ssm.stock.vo.resp.R;
import com.ssm.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author everyDay.youHeng
 * @date 2024年08月05 11:31
 */
@Service("stockService")
@Slf4j
public class StockServiceImpl implements IStockService {

    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;//大盘

    @Autowired
    private StockInfoConfig stockInfoConfig;//股票大盘编码 信息封装的对象

    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;//板块 对象

    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;//涨幅 对象
    @Autowired
    private Cache<String,Object> caffeineCache;//本地缓存


    /**
     * 获取国内大盘的实时数据
     */
    @Override
    public R<List<InnerMarketDomain>> innerIndexAll() {
        R<List<InnerMarketDomain>> result=(R<List<InnerMarketDomain>>)caffeineCache.get("innerMarketKey",key->{

            //1. 获取最新的校验时间点（精确到分钟，秒置为0）
            //DateTime.now()：当前时间
            DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
            //转换成jdbc的date
            Date curDate =curDateTime.toDate();
            //TODO mock测试数据，后期数据通过第三方接口动态获取实时数据 可删除
            //curDate=DateTime.parse("2022-07-07 14:52:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();


            //2. 获取A股大盘编码集合(上证和深证)
            List<String> mCodes = stockInfoConfig.getInner();

            //3. 调用mapper查询数据
            List<InnerMarketDomain> list= stockMarketIndexInfoMapper.selectMarketInfo(mCodes,curDate);

            //4. 封装查询的结果 并 响应数据
            return R.ok(list);

        });

        return result;



    }

    @Override
    public R<List<SectorMarketDomain>> sectorIndexAll() {
        //1. 获取最新的校验时间点（精确到分钟，秒置为0）
        //DateTime.now()：当前时间
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        //转换成jdbc的date
        Date curDate =curDateTime.toDate();
        //TODO mock测试数据，后期数据通过第三方接口动态获取实时数据 可删除
        curDate=DateTime.parse("2021-12-21 14:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //2. 调用mapper查询数据
        List<SectorMarketDomain> list= stockBlockRtInfoMapper.selectSectorInfo(curDate);

        //3. 封装查询的结果 并 响应数据
        return R.ok(list);
    }

    @Override
    public R<PageResult<StockUpdownDomain>> getPageStockInfos(Integer page, Integer pageSize) {
        //1.获取最新的股票交易时间
        Date lastDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //TODO 伪造数据，后续删除
        lastDate=DateTime.parse("2022-07-07 14:43:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        //2.设置分页参数
        PageHelper.startPage(page,pageSize);
        //3.调用mapper查询数据
        List<StockUpdownDomain> infos=stockRtInfoMapper.getStockUpDownInfos(lastDate);
        //判断数据是否为空
        if (CollectionUtils.isEmpty(infos)) {
            return R.error(ResponseCode.NO_RESPONSE_DATA);
        }
        //3.组装数据
        //转化成PageInfo对象
        //PageInfo<StockUpdownDomain> pageInfo = new PageInfo<>(infos);
        PageResult<StockUpdownDomain> pageResult = new PageResult<>(new PageInfo<>(infos));
        //4.响应数据
        return R.ok(pageResult);
    }

    @Override
    public R<List<StockUpdownDomain>> getPageStockIncrease() {
        //1.获取最新的股票交易时间
        Date lastDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
        //TODO 伪造数据，后续删除
        lastDate=DateTime.parse("2022-07-07 14:43:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //3.调用mapper查询数据
        List<StockUpdownDomain> list=stockRtInfoMapper.getStockUpDownIncrease(lastDate);

        //4.响应数据
        return R.ok(list);

    }

    @Override
    public R<Map<String, List>> getStockUpDownCount() {
        //1. 交易时间范围 openTime  curTime
        // 最新股票交易对应的 - 截止时间
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        //假时间
        curDateTime=DateTime.parse("2022-01-06 14:25:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date endDate = curDateTime.toDate();
        // 最新交易时间对应的 - 开盘时间
        Date startDate = DateTimeUtil.getOpenDate(curDateTime).toDate();

        //2.统计涨停数据
        //约定mapper中flag入参： 1-》涨停数据 0：跌停
        List<Map> upCounts= stockRtInfoMapper.getStockUpdownCount(startDate,endDate,1);
        //3.查询跌停数据
        List<Map> dwCounts=stockRtInfoMapper.getStockUpdownCount(startDate,endDate,0);

        //4.组装数据
        HashMap<String, List> data = new HashMap<>();
        data.put("upList",upCounts);
        data.put("downList",dwCounts);
        //5.返回结果
        return R.ok(data);
    }

    @Override
    public void exportStockUpDownInfo(Integer page, Integer pageSize, HttpServletResponse response) {
        //1. 获取分页数据。直接调用上面的方法
        R<PageResult<StockUpdownDomain>> r = getPageStockInfos(page, pageSize);
        List<StockUpdownDomain> rows = r.getData().getRows();

        try {
            //2.判断分页数据是否为空，为空则响应json格式的提示信息
            if (CollectionUtils.isEmpty(rows)) {
                R<Object> error = R.error(ResponseCode.NO_RESPONSE_DATA);
                //将error转化成json格式字符串
                String jsonData = new ObjectMapper().writeValueAsString(error);
                //设置响应的数据格式 告知浏览器传入的数据格式
                response.setContentType("application/json");
                //设置编码格式
                response.setCharacterEncoding("utf-8");

                //响应数据
                response.getWriter().write(jsonData);
                return;
            }

            //3. 将数据导出到Excel表格中
            response.setContentType("application/vnd.ms-excel");
            //URLEncoder.encode可以防止中文乱码
            String fileName = URLEncoder.encode("股票涨幅数据表格", "UTF-8");
            //指定excel导出时默认的文件名称，告诉浏览器下载文件时默认的名称为：股票涨幅数据表格
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), StockUpdownDomain.class).sheet("股票涨幅信息").doWrite(rows);
        }catch (Exception e) {
            //后端异常显示
            log.error("导出时间：{},当初页码：{}，导出数据量：{}，发生异常信息：{}",DateTime.now().toString("yyyy-MM--dd HH:mm:ss"),page,pageSize,e.getMessage());

            //前端异常通知
            try {
                R<Object> error = R.error(ResponseCode.NO_EXPORT_DATA);
                //将error转化成json格式字符串
                String jsonData = new ObjectMapper().writeValueAsString(error);
                //设置响应的数据格式 告知浏览器传入的数据格式
                response.setContentType("application/json");
                //设置编码格式
                response.setCharacterEncoding("utf-8");

                //响应数据
                response.getWriter().write(jsonData);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public R<Map<String, List>> getComparedStockTradeAmt() {
        //1. 获取T日 获取最新股票交易日的日期范围
        DateTime tEndDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date tEndDate = tEndDateTime.toDate();//转化成java中Date,这样jdbc默认识别
        DateTime tStartDateTime = DateTimeUtil.getOpenDate(tEndDateTime);
        Date tStartDate = tStartDateTime.toDate();//转化成java中Date,这样jdbc默认识别
        //TODO  mock测试数据
        tStartDate=DateTime.parse("2022-01-03 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        tEndDate=DateTime.parse("2022-01-03 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //2. 获取T-1日的区间范围
        DateTime preTEndDateTime = DateTimeUtil.getPreviousTradingDay(tEndDateTime);
        Date preTEndDate = preTEndDateTime.toDate();//转化成java中Date,这样jdbc默认识别
        DateTime preTStartDateTime = DateTimeUtil.getOpenDate(tStartDateTime);
        Date preTStartDate = preTStartDateTime.toDate();//转化成java中Date,这样jdbc默认识别
        //TODO  mock测试数据
        preTStartDate=DateTime.parse("2022-01-02 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        preTEndDate=DateTime.parse("2022-01-02 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //3.调用mapper接口查询
        //统计T日
        List<Map> tData = stockMarketIndexInfoMapper.getStockTradeAmtInfo(tStartDate,tEndDate,stockInfoConfig.getInner());
        //统计T-1日
        List<Map> preTData = stockMarketIndexInfoMapper.getStockTradeAmtInfo(preTStartDate,preTEndDate,stockInfoConfig.getInner());

        //4. 组装数据
        HashMap<String, List> data = new HashMap<>();
        data.put("amtList",tData);
        data.put("yesAmtList",preTData);

        //5.响应数据给前端
        return R.ok(data);
    }

    @Override
    public R<Map> getIncreaseRangeInfo() {
        //1. 获取当前股票最新的交易时间点
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date curDate = curDateTime.toDate();//转化成java中Date,这样jdbc默认识别
        //TODO  mock数据
        curDate=DateTime.parse("2022-01-06 09:55:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //2. 调用mapper获取数据
        List<Map> infos=stockRtInfoMapper.getIncreaseRangeInfoByDate(curDate);

        // 获取配置文件中的涨幅区间 有序的标题集合
        List<String> upDownRange = stockInfoConfig.getUpDownRange();
        //将List集合的每个元素转化为Map对象
        List<Map> orderMaps =new ArrayList<>();

        List<Map> allInfos = new ArrayList<>();
        for (String title : upDownRange){
            Map tmp = null;
            for(Map info : infos){
                if(info.containsValue(title)){
                    tmp=info;
                    break;
                }
            }

            //内存循序结束，说明该区间没有数据
            if(tmp==null){
                //区间不存在数据，则补齐
                tmp = new HashMap();
                tmp.put("title", title);
                tmp.put("count", 0);
            }

            // 收集map
            allInfos.add(tmp);
        }

        //3. 组装数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("time",curDateTime.toString("yyyy-MM-dd HH:mm:ss"));
        data.put("infos",allInfos);

        return R.ok(data);
    }

    @Override
    public R<List<Stock4MinuteDomain>> stockScreenTimeSharing(String stockCode) {
        //1. 获取T日 获取最新股票交易日的日期范围
        DateTime tEndDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date tEndDate = tEndDateTime.toDate();//转化成java中Date,这样jdbc默认识别
        DateTime tStartDateTime = DateTimeUtil.getOpenDate(tEndDateTime);
        Date tStartDate = tStartDateTime.toDate();//转化成java中Date,这样jdbc默认识别
        //TODO  mock测试数据
        tStartDate=DateTime.parse("2022-01-02 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        tEndDate=DateTime.parse("2022-01-02 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //2. 调用mapper接口获取数据
        List<Stock4MinuteDomain> data=stockRtInfoMapper.getStock4MinuteInfo(tStartDate,tEndDate,stockCode);

        //3. 响应数据
        return R.ok(data);
    }

    @Override
    public R<List<Stock4EvrDayDomain>> getStockScreenDKline(String stockCode) {
        //1. 截止时间
        DateTime tEndDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        Date tEndDate = tEndDateTime.toDate();//转化成java中Date,这样jdbc默认识别
        tEndDate=DateTime.parse("2022-04-06 14:25:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //起始时间  就是截止时间往前退 1个月
        DateTime tStartDateTime=tEndDateTime.minusMonths(1);
        Date tStartDate = tStartDateTime.toDate();//转化成java中Date,这样jdbc默认识别
        //TODO  mock测试数据
        tStartDate=DateTime.parse("2022-01-01 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        //2. 调用mapper接口获取数据
        List<Stock4EvrDayDomain> data= stockRtInfoMapper.getStockInfo4EvrDay(stockCode,tStartDate,tEndDate);
        //3. 响应数据
        return R.ok(data);
    }


}
