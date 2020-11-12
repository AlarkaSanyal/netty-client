package com.example.nettyclient.handler;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;

public class CustomChannelPoolHandler extends AbstractChannelPoolHandler {
    @Override
    public void channelCreated(Channel channel) throws Exception {
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        super.channelAcquired(ch);
    }

    @Override
    public void channelReleased(Channel ch) throws Exception {
        super.channelReleased(ch);
    }
}
