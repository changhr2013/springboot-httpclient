package com.changhr.httpclientgo.service;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.*;

/**
 * httpclient 发送请求工具类
 *
 * @author changhr
 * @create 2018-08-01 15:34
 */
@Service
public class HttpClientService {

    /** 日志 */
    private static final Logger logger = LoggerFactory.getLogger(HttpClientService.class);

    @Resource(name = "httpClientManagerFactoryBean")
    private CloseableHttpClient client;

    @Autowired
    private RequestConfig requestConfig;

    private final String CHARSET_UTF8_NAME = "UTF-8";
    private final Charset CHARSET_UTF8 = Charset.forName(CHARSET_UTF8_NAME);
    private final ContentType TEXT_PLAIN_UTF8 = ContentType.create("text/plain", CHARSET_UTF8);

    /**
     * HTTP POST 请求 <br/>
     * 有参 POST 带【Bytes Map】带【自定义 Headers】
     *
     * @param url           请求地址
     * @param strParams     字符串参数Map
     * @param byteParams    字节型参数Map
     * @param headers       请求头Map
     * @return String
     * @throws Exception
     */
    public String sendHttpRequestPost(String url, Map<String, String> strParams,
                                  Map<String, byte[]> byteParams, Map<String, String> headers) throws Exception{
        int code = -1;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        MultipartEntityBuilder requestEntityBuilder = MultipartEntityBuilder.create();

        //字符串参数
        if(strParams != null && strParams.size() > 0){
            Iterator<String> strParamsIterator = strParams.keySet().iterator();
            while(strParamsIterator.hasNext()){
                String key = strParamsIterator.next();
                String value = strParams.get(key);
                requestEntityBuilder.addPart(key, new StringBody(value, TEXT_PLAIN_UTF8));
            }
        }

        //字节型参数
        if (byteParams != null && byteParams.size() >0){
            Iterator<String> byteParamsIterator = byteParams.keySet().iterator();
            while(byteParamsIterator.hasNext()){
                String key = byteParamsIterator.next();
                byte[] value = byteParams.get(key);
                ByteArrayBody postBody = new ByteArrayBody(value, key);
                requestEntityBuilder.addPart(key, postBody);
            }
        }

        // Headers
        if (headers != null && headers.size() > 0) {
            Iterator<String> headerIterator = headers.keySet().iterator();
            while (headerIterator.hasNext()){
                String name = headerIterator.next();
                String value = headers.get(name);
                httpPost.setHeader(name, value);
            }
        }

        HttpEntity httpEntity = requestEntityBuilder.build();
        httpPost.setEntity(httpEntity);
        try(CloseableHttpResponse httpResponse = client.execute(httpPost)){
            code = httpResponse.getStatusLine().getStatusCode();
            HttpEntity entity = httpResponse.getEntity();
            if(code == HttpStatus.SC_OK){
                return EntityUtils.toString(entity, CHARSET_UTF8);
            }else{
                //消费掉内容
                EntityUtils.consume(entity);
                throw new Exception("http return code = " + code);
            }
        }catch (Exception e) {
            throw new Exception("发生异常，请检查连接、参数是否正确", e);
        } finally {
            if (httpEntity != null){
                try{
                    EntityUtils.consume(httpEntity);
                }catch (Exception e){
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * HTTP GET 请求 <br/>
     * 有参 GET 带【自定义 Headers】
     * @param url           请求url
     * @param strParams     请求参数Map
     * @param headers       请求头Map
     * @return String
     * @throws Exception
     */
    public String sendHttpRequestGet(String url, Map<String, String> strParams, Map<String, String> headers)
            throws Exception{

        try{
            List<NameValuePair> params = new ArrayList<>();
            //字符串参数
            if(strParams != null && strParams.size() > 0){
                Iterator<String> strParamIterator = strParams.keySet().iterator();
                while(strParamIterator.hasNext()){
                    String key = strParamIterator.next();
                    String value = strParams.get(key);
                    params.add(new BasicNameValuePair(key, value));
                }
            }
            String paramURLStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
            HttpGet httpGet = new HttpGet(url + "?" + paramURLStr);
            httpGet.setConfig(requestConfig);

            if (headers != null && headers.size() > 0){
                Iterator<String> headerIterator = headers.keySet().iterator();
                while (headerIterator.hasNext()) {
                    String name = headerIterator.next();
                    String value = headers.get(name);
                    httpGet.setHeader(name, value);
                }
            }

            try(CloseableHttpResponse response = client.execute(httpGet)){
                //得到响应体
                HttpEntity entity = response.getEntity();
                int code = response.getStatusLine().getStatusCode();
                if(code >= HttpStatus.SC_OK && code < HttpStatus.SC_BAD_REQUEST ){
                    return EntityUtils.toString(entity, CHARSET_UTF8);
                }else{
                    //消费掉内容
                    EntityUtils.consume(entity);
                    throw new Exception("http return code = " + code);
                }
            }
        } catch (Exception e){
            throw new Exception("发生异常，请检查连接、参数是否正确。", e);
        }

    }

    /**
     * 无参 POST
     * @param url
     * @return
     * @throws Exception
     */
    public String sendHttpRequestPost(String url) throws Exception {
        return sendHttpRequestPost(url, null, null, null);
    }

    /**
     * 无参 POST 带【自定义 Headers】
     * @param url
     * @param headers
     * @return
     * @throws Exception
     */
    public String sendHttpHeadersPost(String url, Map<String, String> headers) throws Exception {
        return sendHttpRequestPost(url, null, null, headers);
    }

    /**
     * 有参 POST
     * @param url
     * @param strParams
     * @return
     * @throws Exception
     */
    public String sendHttpRequestPost(String url, Map<String, String> strParams) throws Exception {
        return sendHttpRequestPost(url, strParams, null, null);
    }

    /**
     * 有参 POST 带【自定义 Headers】
     * @param url
     * @param strParams
     * @param headers
     * @return
     * @throws Exception
     */
    public String sendHttpHeadersPost(String url, Map<String, String> strParams, Map<String, String> headers) throws Exception {
        return sendHttpRequestPost(url, strParams, null, headers);
    }

    /**
     * 有参 POST 带【Bytes Map】
     * @param url
     * @param strParams
     * @param byteParams
     * @return
     * @throws Exception
     */
    public String sendHttpRequestPost(String url, Map<String, String> strParams, Map<String, byte[]> byteParams) throws Exception {
        return sendHttpRequestPost(url, strParams, byteParams, null);
    }

    /**
     * 无参 POST 带【Bytes Map】
     * @param url
     * @param byteParams
     * @return
     * @throws Exception
     */
    public String sendHttpBytesPost(String url, Map<String, byte[]> byteParams) throws Exception {
        return sendHttpRequestPost(url, null, byteParams, null);
    }

    /**
     * 无参 POST 带【Bytes Map】带【自定义 Headers】
     * @param url
     * @param byteParams
     * @param headers
     * @return
     * @throws Exception
     */
    public String sendHttpBytesPost(String url, Map<String, byte[]> byteParams, Map<String, String> headers) throws Exception {
        return sendHttpRequestPost(url, null, byteParams, headers);
    }

    /**
     * 有参 HTTP GET
     * @param url
     * @param strParams
     * @return
     * @throws Exception
     */
    public String sendHttpRequestGet(String url, Map<String, String> strParams) throws Exception {
        return sendHttpRequestGet(url, strParams, null);
    }

    /**
     * 无参 HTTP GET
     * @param url
     * @return
     * @throws Exception
     */
    public String sendHttpRequestGet(String url) throws Exception {
        return sendHttpRequestGet(url, null, null);
    }

    /**
     * 无参 HTTP GET 带【自定义Headers】
     * @param url
     * @param headers
     * @return
     * @throws Exception
     */
    public String sendHttpHeadersGet(String url, Map<String, String> headers) throws Exception {
        return sendHttpRequestGet(url, null, headers);
    }
}
