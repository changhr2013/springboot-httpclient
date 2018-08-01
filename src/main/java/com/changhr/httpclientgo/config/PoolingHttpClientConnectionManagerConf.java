package com.changhr.httpclientgo.config;

import org.apache.http.config.Registry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * HttpClient 连接池管理配置
 *
 * @author changhr
 * @create 2018-08-01 9:40
 */
@Configuration
public class PoolingHttpClientConnectionManagerConf {

    /**
     * 连接池最大连接数
     */
    @Value("${http.client.maxTotal}")
    private Integer maxTotal = 100;

    /**
     * 并发数
     */
    @Value("${http.client.defaultMaxPerRoute}")
    private Integer defaultMaxPerRoute = 20;

    /**
     * 连接存活时间，单位：s
     */
    @Value("${http.client.timeToLive}")
    private Integer timeToLive = 60;

    /**
     * 可用空闲连接过期时间，重用空闲连接时会先检查是否空闲时间超过这个时间（单位：ms）
     */
    @Value("${http.client.validateAfterInactivity}")
    private Integer validateAfterInactivity = 10000;

    /**
     * 实例化一个连接池管理器，设置最大连接数、并发连接数
     * @return
     */
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(
            @Qualifier("socketFactoryRegistry")Registry<ConnectionSocketFactory> socketFactoryRegistry){
        PoolingHttpClientConnectionManager httpClientConnectionManager =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry, null, null, null,
                        timeToLive, TimeUnit.SECONDS);
        //最大连接数
        httpClientConnectionManager.setMaxTotal(maxTotal);
        //并发数
        httpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        //定义不活动的时间段（单位：ms），之后必须在租用给使用者之前重新验证持久连接。
        //传递给此方法的非正值会禁用连接验证。
        //此检查有助于检测已经变为陈旧（半关闭）的连接，同时在池中保持不活动状态。
        httpClientConnectionManager.setValidateAfterInactivity(validateAfterInactivity);

        return httpClientConnectionManager;
    }
}
