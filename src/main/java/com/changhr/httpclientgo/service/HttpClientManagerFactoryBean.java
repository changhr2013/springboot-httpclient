package com.changhr.httpclientgo.service;

import com.changhr.httpclientgo.http.IdleConnectionMonitorThread;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HttpClient 客户端封装
 *
 * @author changhr
 * @create 2018-08-01 10:44
 */
@Service("httpClientManagerFactoryBean")
public class HttpClientManagerFactoryBean
        implements FactoryBean<CloseableHttpClient>, InitializingBean, DisposableBean {

    /**
     * FactoryBean 生成的目标对象
     */
    private CloseableHttpClient client;

    @Autowired
    private ConnectionKeepAliveStrategy connectionKeepAliveStrategy;

    @Autowired
    private HttpRequestRetryHandler httpRequestRetryHandler;

    @Autowired
    private DefaultProxyRoutePlanner proxyRoutePlanner;

    @Autowired
    private PoolingHttpClientConnectionManager httpClientConnectionManager;

    @Autowired
    private RequestConfig requestConfig;

    /**
     * 销毁上下文时，销毁 HttpClient 实例
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        /**
         * 调用 httpClient.close() 会先 shutdown connection manager, 然后再释放该 HttClient 所占用的所有资源，
         * 关闭所有在使用或者空闲的 connection 包括底层 socket。由于这里把它所使用的 connection manager 关闭了，
         * 所以在下次还要进行 http 请求的时候，要重新 new 一个 connection manager 来 build 一个 HttpClient，
         * 也就是在需要关闭和新建 Client 的情况下，connection manager 不能是单例的。
         */
        if(this.client != null){
            this.client.close();
        }
    }

    /**
     * 初始化实例
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        /**
         * 建议此处使用 HttpClients.custom() 的方式来创建 HttpClientBuilder，
         * 而不要使用 HttpClientBuilder.create() 方法来创建 HttpClientBuilder。
         * 从官方文档可以得出，HttpClientBuilder 是非线程安全的，但是 HttpClients 却是 Immutable 的，
         * immutable 对象不仅能够保证对象的状态不被改变，而且还可以不使用锁机制就能被其他线程共享。
         */
        this.client = HttpClients.custom()
                .setConnectionManager(httpClientConnectionManager)
                .setRetryHandler(httpRequestRetryHandler)
                .setKeepAliveStrategy(connectionKeepAliveStrategy)
                .setDefaultRequestConfig(requestConfig)
                .build();

        // 初始化监视线程
        IdleConnectionMonitorThread monitorThread = new IdleConnectionMonitorThread(httpClientConnectionManager);
        // 将监视线程设为守护线程并启动
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    /**
     * 返回实例的类型
     * @return
     * @throws Exception
     */
    @Override
    public CloseableHttpClient getObject() throws Exception {
        return this.client;
    }

    @Override
    public Class<?> getObjectType() {
        return (this.client == null ? CloseableHttpClient.class : this.client.getClass());
    }

    /**
     * 构建的实例为单例
     * @return
     */
    @Override
    public boolean isSingleton() {
        return true;
    }
}
