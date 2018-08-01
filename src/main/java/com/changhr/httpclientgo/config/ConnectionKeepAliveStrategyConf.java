package com.changhr.httpclientgo.config;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * httpclient 连接保持策略配置
 *
 * @author changhr
 * @create 2018-07-31 17:33
 */
@Configuration
public class ConnectionKeepAliveStrategyConf {

    @Value("${http.client.keepAliveTime}")
    private int keepAliveTime = 30;

    @Bean("connectionKeepAliveStrategy")
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy(){
        return new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                // Honor 'keep-alive' header
                HeaderElementIterator iterator = new BasicHeaderElementIterator(
                        httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while(iterator.hasNext()){
                    HeaderElement headerElement = iterator.nextElement();
                    String param = headerElement.getName();
                    String value = headerElement.getValue();
                    if(value != null && param.equalsIgnoreCase("timeout")){
                        try{
                            return Long.parseLong(value) * 1000;
                        }catch (NumberFormatException ignore){

                        }
                    }
                }
                return keepAliveTime * 1000;
            }
        };
    }

}
