package com.liuyj.jsoup.proxy;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author liuyuanju1
 * @date 2018/8/20
 * @description: 代理ip
 */
@Data
@ToString
@Accessors(chain = true)
public class IpEntity {
    private String ip;
    private int port;
}
