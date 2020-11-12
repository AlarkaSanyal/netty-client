package com.example.nettyclient.config;

import com.example.nettyclient.handler.CustomChannelPoolHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;

public class NettyClient {

    public static final byte CARRIAGE_RETURN_BYTE = 0X0D;
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private String ip;
    private int port;
    private FixedChannelPool fixedChannelPool;
    private int acquireTimeOut = 5000;
    private int initialPoolSize = 10;
    private int maxPoolSize = 10;
    private ArrayList<Channel> channels;
    private Iterator<Channel> channelIterator;
    private ByteBufAllocator byteBufAllocator = new PooledByteBufAllocator();

    public NettyClient(String ip, int port, int acquireTimeOut, int initialPoolSize, int maxPoolSize) {
        this.ip = ip;
        this.port = port;
        this.acquireTimeOut = acquireTimeOut;
        this.initialPoolSize = initialPoolSize;
        this.maxPoolSize = maxPoolSize;
    }

    public void initializeChannels() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.remoteAddress(ip, port)
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);

        ChannelPoolHandler channelPoolHandler = new CustomChannelPoolHandler();
        fixedChannelPool = new FixedChannelPool(bootstrap,
                                                channelPoolHandler,
                                                ChannelHealthChecker.ACTIVE,
                                                FixedChannelPool.AcquireTimeoutAction.NEW,
                                                acquireTimeOut,
                                                maxPoolSize,
                                                maxPoolSize,
                                                true);

        channels = new ArrayList<Channel>();

        for (int i = 0; i < initialPoolSize; i++) {
            setUpNewChannel();
        }
        channelIterator = channels.iterator();
    }

    /**
     * Get a channel from the pool
     *
     * @return
     */
    public Channel getChannel() {
        logger.info("Trying to get channel");

        Channel channel = null;
        while (channelIterator.hasNext()) {
            logger.info("Iterator has Next");
            Channel newChannel = channelIterator.next();
            if (newChannel.isActive()) {
                if (newChannel.isWritable()) {
                    logger.info("Got new active and writable channel: " + newChannel.toString());
                    channel = newChannel;
                    break;
                } else {
                    logger.info("Got new active but not writable channel: " + newChannel.toString());
                    continue;
                }
            } else {
                removeChannel(newChannel);
            }
        }
        if (channel == null) {
            logger.info("Used up all available channels, setting up new set of channels");
            channel = setUpNewChannel();
            channelIterator = channels.iterator(); // reset counter
        }
        // If still null, then issue
        if (channel == null) {
            logger.error("Error in getting a channel");
        }
        return channel;
    }

    /**
     * Initial set up of a pool of channels to be used later
     */
    private Channel setUpNewChannel() {
        logger.info("Trying to set up new channel");

        Channel channel = fixedChannelPool.acquire().syncUninterruptibly().getNow();
        if (channel.isActive()) {
            logger.info("Got new channel: " + channel.toString());
            byte[] carriage_return_byte_array = {CARRIAGE_RETURN_BYTE};
            ByteBuf carriage_return = Unpooled.copiedBuffer(carriage_return_byte_array);
            // Setting this server up as Delimiter based.
            DelimiterBasedFrameDecoder decoder = new DelimiterBasedFrameDecoder(350, Boolean.FALSE, carriage_return);
            channel.pipeline().addFirst(decoder);
            channels.add(channel);
        } else {
            removeChannel(channel);
        }
        return channel;
    }

    /**
     * Inactive channel NEEDS to be released because of channel lifecycle
     * Lifecycle: register -> active -> inactive -> unregister
     * A channel cannot go "inactive -> active". It will have to follow
     * full lifecycle.
     *
     * @param channel
     */
    private void removeChannel(Channel channel) {
        logger.info("Closing inactive channel: " + channel.toString());

        fixedChannelPool.release(channel);
        channel.close();
    }

    public ByteBufAllocator getByteBufAllocator() {
        return byteBufAllocator;
    }
}
