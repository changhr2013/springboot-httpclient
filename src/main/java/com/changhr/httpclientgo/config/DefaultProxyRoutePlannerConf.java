package com.changhr.httpclientgo.config;

import org.apache.http.HttpHost;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HttpClient 代理配置
 *
 * @author changhr
 * @create 2018-08-01 10:05
 */
@Configuration
public class DefaultProxyRoutePlannerConf {

    /**
     * 代理的 host 地址
     */
    @Value("${http.client.proxyHost}")
    private String proxyHost;

    /**
     * 代理的端口号
     */
    @Value("${http.client.proxyPort}")
    private int proxyPort = 8080;

    @Bean
    public DefaultProxyRoutePlanner defaultProxyRoutePlanner(){
        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        return new DefaultProxyRoutePlanner(proxy);
    }
}
