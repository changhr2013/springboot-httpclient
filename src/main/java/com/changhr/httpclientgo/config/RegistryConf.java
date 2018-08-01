package com.changhr.httpclientgo.config;

import com.changhr.httpclientgo.http.CustomTrustManager;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * @author changhr
 * @create 2018-08-01 11:54
 */
@Configuration
public class RegistryConf {

    @Bean("customTrustManager")
    public CustomTrustManager customTrustManager(){
        return new CustomTrustManager();
    }

    @Bean("sslContext")
    public SSLContext sslContext(@Qualifier("customTrustManager")CustomTrustManager customTrustManager)
            throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{customTrustManager}, null);
        return sslContext;
    }

    @Bean("sslConnectionSocketFactory")
    public SSLConnectionSocketFactory sslConnectionSocketFactory(
            @Qualifier("sslContext") SSLContext sslContext){
        return new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1", "SSLv3"},
                null,
                //客户端验证服务器身份的策略
                NoopHostnameVerifier.INSTANCE
        );
    }

    @Bean("socketFactoryRegistry")
    public Registry<ConnectionSocketFactory> socketFactoryRegistry(
            @Qualifier("sslConnectionSocketFactory") SSLConnectionSocketFactory sslConnectionSocketFactory){
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslConnectionSocketFactory)
                .build();
    }
}
