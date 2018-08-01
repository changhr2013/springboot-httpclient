package com.changhr.httpclientgo.config;

import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 请求配置类配置
 *
 * @author changhr
 * @create 2018-08-01 10:22
 */
@Configuration
public class RequestConfigConf {

    /**
     * 连接超时时间
     */
    @Value("${http.client.connectTimeout}")
    private Integer connectTimeout = 2000;

    /**
     * 从连接池中取连接的超时时间
     */
    @Value("${http.client.connectionRequestTimeout}")
    private Integer connectionRequestTimeout = 2000;

    /**
     * 请求超时时间
     */
    @Value("${http.client.socketTimeout}")
    private Integer socketTimeout = 2000;

    @Bean
    public RequestConfig requestConfig(){
        return RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .build();
    }
}
