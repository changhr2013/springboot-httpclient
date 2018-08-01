package com.changhr.httpclientgo.config;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * HTTP 请求重试配置
 *
 * @author changhr
 * @create 2018-08-01 9:30
 */
@Configuration
public class HttpRequestRetryHandlerConf {

    @Value("${http.client.retryCount}")
    private Integer retryCount = 2;

    @Bean
    public HttpRequestRetryHandler httpRequestRetryHandler(){
        //请求重试
        final int retryCount = this.retryCount;
        return new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                //如果已经重试了 retryCount 次，就放弃
                if (executionCount >= retryCount){
                    return false;
                }
                //如果服务器丢掉了连接，那么就重试
                if(exception instanceof NoHttpResponseException){
                    return true;
                }
                //不要重试SSL握手异常
                if(exception instanceof SSLHandshakeException){
                    return false;
                }
                //超时
                if(exception instanceof InterruptedIOException){
                    return false;
                }
                //目标服务器不可达
                if(exception instanceof UnknownHostException){
                    return false;
                }
                //连接被拒绝
                if(exception instanceof ConnectTimeoutException){
                    return false;
                }
                //SSL握手异常
                if(exception instanceof SSLException){
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                //如果请求是幂等的，就再次尝试
                if(!(request instanceof HttpEntityEnclosingRequest)){
                    return true;
                }
                return false;
            }
        };
    }


}
