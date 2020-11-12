package com.example.nettyclient.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientConfig.class);

    @Value("${netty.ip}")
    private String nettyIp;

    @Value("${netty.port}")
    private int nettyPort;

    @Value("${netty.acquireTimeOut}")
    private int nettyAcquireTimeOut;

    @Value("${netty.initialPoolSize}")
    private int nettyInitialPoolSize;

    @Value("${netty.maxPoolSize}")
    private int nettyMaxPoolSize;

    @Bean
    public NettyClient nettyClient() {
        NettyClient nettyClient = new NettyClient(nettyIp, nettyPort, nettyAcquireTimeOut, nettyInitialPoolSize, nettyMaxPoolSize);
        nettyClient.initializeChannels();
        return nettyClient;
    }
}
