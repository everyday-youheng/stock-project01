package com.ssm.stock.service.impl;

import com.google.common.collect.Lists;
import com.ssm.stock.mapper.StockBusinessMapper;
import com.ssm.stock.mapper.StockMarketIndexInfoMapper;
import com.ssm.stock.pojo.entity.StockMarketIndexInfo;
import com.ssm.stock.pojo.entity.StockRtInfo;
import com.ssm.stock.pojo.vo.StockInfoConfig;
import com.ssm.stock.pojo.vo.TaskThreadPoolInfo;
import com.ssm.stock.service.StockTimerTaskService;
import com.ssm.stock.utils.DateTimeUtil;
import com.ssm.stock.utils.IdWorker;
import com.ssm.stock.utils.ParseType;
import com.ssm.stock.utils.ParserStockInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author everyDay.youHeng
 * @date 2024年08月20 12:24
 */
@Service("stockTimerTaskService")
@Slf4j
public class StockTimerTaskServiceImpl implements StockTimerTaskService {

    @Autowired
    private RestTemplate restTemplate; //访问远程接口，采集数据

    @Autowired
    private StockInfoConfig stockInfoConfig; //股票信息

    @Autowired
    private IdWorker idWorker; //工具类：雪花算法，生成唯一的id

    @Autowired
    private ParserStockInfoUtil parserStockInfoUtil;//工具类：解析JS格式数据

    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;//大盘

    @Autowired
    StockBusinessMapper  stockBusinessMapper; //个股
    @Autowired
    RabbitTemplate rabbitTemplate;//消息对列 RabbitTemplate对象

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor; //线程池对象

    private HttpEntity<Object> httpEntity;//http请求实体对象

    /**
     * 使用线程池
     */
    @Override
    public void getInnerMarketInfo(){
        threadPoolTaskExecutor.execute(()->{

        //1. 采集原始数据
        //1.1 组装url地址 https://hq.sinajs.cn/list=sh000001,sz399001
        String url=stockInfoConfig.getMarketUrl() + String.join(",",stockInfoConfig.getInner());
        //1.2 组装请求头，添加防盗链和用户客户端标识。（否则不能访问。）
        HttpHeaders headers = new HttpHeaders();
        //必须填写，否则数据采集不到
        headers.add("Referer","https://finance.sina.com.cn/stock/");//防盗链
        headers.add(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");//用户客户端标识
        //1.3 组装http请求实体对象
        HttpEntity<Object> httpEntity2 = new HttpEntity<>(headers);

        //1.4 使用ResetTemplate发起请求，远程访问接口
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity2, String.class);
        //通过状态码判断当前请求是否成功
        int statusCodeValue=responseEntity.getStatusCodeValue();
        if (statusCodeValue!=200) {
            //当前请求失败
            log.error("当前时间点：{}，采集数据失败。http状态码是：{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),statusCodeValue);
            //其它操作：发送邮件、信息给相关的运营人员

            return;
        }
        //1.5 获取原始的JS格式的数据
        String jsData = responseEntity.getBody();

        log.info("当前时间点：{}，采集的原始数据：{}",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),jsData);

        //2. 使用正则解析原始数据
        /**
         * 原始数据
         * var hq_str_sh000001="上证指数,3267.8103,3283.4261,3236.6951,3290.2561,3236.4791,0,0,402626660,398081845473,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2022-04-07,15:01:09,00,";
         * var hq_str_sz399001="深证成指,12101.371,12172.911,11972.023,12205.097,11971.334,0.000,0.000,47857870369,524892592190.995,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,2022-04-07,15:00:03,00";
         */
        //定义正则表达式
        String reg="var hq_str_(.+)=\"(.+)\";";
        //编译正则表达式,获取编译对象
        Pattern pattern = Pattern.compile(reg);
        //匹配字符串,获取匹配对象
        Matcher matcher = pattern.matcher(jsData);
        List<StockMarketIndexInfo> entityList = new ArrayList<>();
        //逐行
        while (matcher.find()){
            //获取第1组  大盘的编码
            String marketCode = matcher.group(1);
            //获取第2组  大盘的其它信息
            String otherInfo=matcher.group(2);

            //将otherInfo以逗号切割，获取每一个单独的信息
            String[] splitArr = otherInfo.split(",");
            //大盘名称
            String marketName=splitArr[0];
            //获取当前大盘的开盘点数
            BigDecimal openPoint=new BigDecimal(splitArr[1]);
            //前收盘点
            BigDecimal preClosePoint=new BigDecimal(splitArr[2]);
            //获取大盘的当前点数
            BigDecimal curPoint=new BigDecimal(splitArr[3]);
            //获取大盘最高点
            BigDecimal maxPoint=new BigDecimal(splitArr[4]);
            //获取大盘的最低点
            BigDecimal minPoint=new BigDecimal(splitArr[5]);
            //获取成交量
            Long tradeAmt=Long.valueOf(splitArr[8]);
            //获取成交金额
            BigDecimal tradeVol=new BigDecimal(splitArr[9]);
            //时间
            Date curTime = DateTimeUtil.getDateTimeWithoutSecond(splitArr[30] + " " + splitArr[31]).toDate();

            //3. 将解析后的数据封装到实体对象
            StockMarketIndexInfo info = StockMarketIndexInfo.builder()
                    .id(idWorker.nextId()).marketCode(marketCode)
                    .marketName(marketName).curPoint(curPoint)
                    .openPoint(openPoint).preClosePoint(preClosePoint)
                    .maxPoint(maxPoint).minPoint(minPoint)
                    .tradeVolume(tradeVol).tradeAmount(tradeAmt)
                    .curTime(curTime).build();
            //收集封装的对象，方便批量插入
            entityList.add(info);
        }

        //4.调用mapper接口,批量入库
        int count=stockMarketIndexInfoMapper.insertBatch(entityList);
        if (count>0) {
            //5. 数据采集完毕，并且入库之后。发送消息 给其它工程
            //通知后台终端刷新本地缓存，
            // 发送的日期数据 是告知对方当前更新的股票数据所在时间点
            //将毫秒值转换成日期：select FROM_UNIXTIME(1602668106,'%Y-%m-%d %h:%i:%s')
            rabbitTemplate.convertAndSend("stockExchange","inner.market",new Date());

            log.info("当前时间点：{}，插入大盘数据成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        }else{
            log.error("当前时间点：{}，插入大盘数据失败！",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));

        }
        });
    }

    /**
     * 没有使用线程池
     */
    //@Override
    public void getInnerMarketInfo_old() {
        //1. 采集原始数据
        //1.1 组装url地址 https://hq.sinajs.cn/list=sh000001,sz399001
        String url=stockInfoConfig.getMarketUrl() + String.join(",",stockInfoConfig.getInner());
        /*//1.2 组装请求头，添加防盗链和用户客户端标识。（否则不能访问。）
        HttpHeaders headers = new HttpHeaders();
        //必须填写，否则数据采集不到
        headers.add("Referer","https://finance.sina.com.cn/stock/");//防盗链
        headers.add(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");//用户客户端标识
        //1.3 组装http请求实体对象
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        *///1.4 使用ResetTemplate发起请求，远程访问接口
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        //通过状态码判断当前请求是否成功
        int statusCodeValue=responseEntity.getStatusCodeValue();
        if (statusCodeValue!=200) {
            //当前请求失败
            log.error("当前时间点：{}，采集数据失败。http状态码是：{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),statusCodeValue);
            //其它操作：发送邮件、信息给相关的运营人员

            return;
        }
        //1.5 获取原始的JS格式的数据
        String jsData = responseEntity.getBody();
        log.info("当前时间点：{}，采集的原始数据：{}",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),jsData);

        //2. 使用正则解析原始数据
        /**
         * 原始数据
         * var hq_str_sh000001="上证指数,3267.8103,3283.4261,3236.6951,3290.2561,3236.4791,0,0,402626660,398081845473,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2022-04-07,15:01:09,00,";
         * var hq_str_sz399001="深证成指,12101.371,12172.911,11972.023,12205.097,11971.334,0.000,0.000,47857870369,524892592190.995,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,2022-04-07,15:00:03,00";
         */
        //定义正则表达式
        String reg="var hq_str_(.+)=\"(.+)\";";
        //编译正则表达式,获取编译对象
        Pattern pattern = Pattern.compile(reg);
        //匹配字符串,获取匹配对象
        Matcher matcher = pattern.matcher(jsData);
        List<StockMarketIndexInfo> entityList = new ArrayList<>();
        //逐行
        while (matcher.find()){
            //获取第1组  大盘的编码
            String marketCode = matcher.group(1);
            //获取第2组  大盘的其它信息
            String otherInfo=matcher.group(2);

            //将otherInfo以逗号切割，获取每一个单独的信息
            String[] splitArr = otherInfo.split(",");
            //大盘名称
            String marketName=splitArr[0];
            //获取当前大盘的开盘点数
            BigDecimal openPoint=new BigDecimal(splitArr[1]);
            //前收盘点
            BigDecimal preClosePoint=new BigDecimal(splitArr[2]);
            //获取大盘的当前点数
            BigDecimal curPoint=new BigDecimal(splitArr[3]);
            //获取大盘最高点
            BigDecimal maxPoint=new BigDecimal(splitArr[4]);
            //获取大盘的最低点
            BigDecimal minPoint=new BigDecimal(splitArr[5]);
            //获取成交量
            Long tradeAmt=Long.valueOf(splitArr[8]);
            //获取成交金额
            BigDecimal tradeVol=new BigDecimal(splitArr[9]);
            //时间
            Date curTime = DateTimeUtil.getDateTimeWithoutSecond(splitArr[30] + " " + splitArr[31]).toDate();

            //3. 将解析后的数据封装到实体对象
            StockMarketIndexInfo info = StockMarketIndexInfo.builder()
                                        .id(idWorker.nextId()).marketCode(marketCode)
                                        .marketName(marketName).curPoint(curPoint)
                                        .openPoint(openPoint).preClosePoint(preClosePoint)
                                        .maxPoint(maxPoint).minPoint(minPoint)
                                        .tradeVolume(tradeVol).tradeAmount(tradeAmt)
                                        .curTime(curTime).build();
            //收集封装的对象，方便批量插入
            entityList.add(info);
        }

         //4.调用mapper接口,批量入库
        int count=stockMarketIndexInfoMapper.insertBatch(entityList);
        if (count>0) {
            //5. 数据采集完毕，并且入库之后。发送消息 给其它工程
            //通知后台终端刷新本地缓存，
            // 发送的日期数据 是告知对方当前更新的股票数据所在时间点
            //将毫秒值转换成日期：select FROM_UNIXTIME(1602668106,'%Y-%m-%d %h:%i:%s')
            rabbitTemplate.convertAndSend("stockExchange","inner.market",new Date());

            log.info("当前时间点：{}，插入大盘数据成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        }else{
            log.error("当前时间点：{}，插入大盘数据失败！",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));

        }
    }

    @Override
    public void getStockRtIndex() {
        //1.获取所有个股 股票编码的集合 （个股大概有3000多个）
        List<String> allCodes=stockBusinessMapper.getAllStockCodes();
        //编码添加前缀
        allCodes = allCodes.stream()
                .map(code -> {return code.startsWith("6") ? "sh" + code : "sz" + code;})
                .collect(Collectors.toList());
        //将大集合拆分成若干个小的集合，每份大小最多15个
        Lists.partition(allCodes,15).forEach(codes->{
            //System.out.println("size: "+codes+" : "+codes);

            //2. 组装url地址
            //注意：不能一次性将所有数据都加在url地址后面，
            // 需要先进行分片处理，每次最多查询20条股票数据
            // 分批次拉取数据
            //2.1 拼接股票url地址
            String stockUrl=stockInfoConfig.getMarketUrl()+String.join(",",codes);


            //1.2 组装请求头，添加防盗链和用户客户端标识。（否则不能访问。）
            HttpHeaders headers = new HttpHeaders();
            //必须填写，否则数据采集不到
            headers.add("Referer","https://finance.sina.com.cn/stock/");//防盗链
            headers.add(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");//用户客户端标识
            //1.3 组装http请求实体对象
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
            //1.4 使用ResetTemplate发起请求，远程访问接口
            ResponseEntity<String> responseEntity = restTemplate.exchange(stockUrl, HttpMethod.GET, httpEntity, String.class);
            //通过状态码判断当前请求是否成功
            int statusCodeValue=responseEntity.getStatusCodeValue();
            if (statusCodeValue!=200) {
                //当前请求失败
                log.error("当前时间点：{}，采集数据失败。http状态码是：{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),statusCodeValue);
                //其它操作：发送邮件、信息给相关的运营人员
                return;
            }

            //3. 解析JS格式的数据
            //获取原始的JS格式的数据
            String jsData = responseEntity.getBody();
            //调用工具类解析JS格式的数据
            List<StockRtInfo> list = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.ASHARE);

            //4.调用mapper接口，批量插入数据库
            int count=stockBusinessMapper.insertBatch(list);
            if (count>0) {
                log.info("当前时间点：{}，插入个股数据成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            }else{
                log.error("当前时间点：{}，插入个股数据失败！",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            }
        });
    }

    /**
     * bean声明周期 初始化完成后 会调用此方法
     */
    @PostConstruct
    public void initData(){
        //1.2 组装请求头，添加防盗链和用户客户端标识。（否则不能访问。）
        HttpHeaders headers = new HttpHeaders();
        //必须填写，否则数据采集不到
        headers.add("Referer","https://finance.sina.com.cn/stock/");//防盗链
        headers.add(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");//用户客户端标识
        //1.3 组装http请求实体对象
        httpEntity = new HttpEntity<>(headers); //这里的httpEntity对象，就是上面定义的私有属性
    }
}
