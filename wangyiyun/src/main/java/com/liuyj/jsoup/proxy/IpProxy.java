package com.liuyj.jsoup.proxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author liuyuanju1
 * @date 2018/8/20
 * @description: 获取代理ip 工具类
 */
public class IpProxy {

    private static final String PROXY_URL = "http://api.goubanjia.com/api/get.shtml?order=2a0075de67dd405841b36e740089648c&" +
            "num=100&carrier=0&protocol=0&an1=1&sp1=1&sp2=2&sp3=3&sort=1&system=1&rettype=0&seprator=%0D%0A";

    public static List<IpEntity> getProxyIp(){
        HttpGet httpGet = new HttpGet(PROXY_URL);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String data = null;
        List<IpEntity> ipList = null;
        try {
            response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            data = JSON.parseObject(result).get("data").toString();
            ipList = JSONArray.parseArray(data,IpEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ipList;
    }

    public static void main(String[] args) {
        getProxyIp();
    }
}
