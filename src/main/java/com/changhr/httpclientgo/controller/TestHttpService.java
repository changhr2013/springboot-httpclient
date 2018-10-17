package com.changhr.httpclientgo.controller;

import com.changhr.httpclientgo.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author changhr
 * @create 2018-10-17 18:04
 */
@RestController
public class TestHttpService {

    @Autowired
    private HttpClientService httpClientService;

    @GetMapping("/baidu")
    public String getBaidu() throws Exception {
        String result = httpClientService.sendHttpRequestGet("http://www.baidu.com");
        return result;
    }

}
