package com.example.nettyclient.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.example.nettyclient.utilities.Utilities.getByteArray;

public class ClientChannelHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ClientChannelHandler.class);

    private ClientChannelHandlerObserver clientChannelHandlerObserver;

    public ClientChannelHandler(ClientChannelHandlerObserver clientChannelHandlerObserver) {
        this.clientChannelHandlerObserver = clientChannelHandlerObserver;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write(Unpooled.copiedBuffer("Initial message to server".getBytes()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        logger.info("Message received");

        ByteBuf response = (ByteBuf) o;
        receivedMessage(channelHandlerContext, response);
        response.clear();
    }

    // Check if Heartbeat or Color
    private void receivedMessage(ChannelHandlerContext channelHandlerContext, ByteBuf response) {
        String message = new String(getByteArray(response));
        if (message.contains("Heartbeat")) {
            logger.info("Received Heartbeat message");
            channelHandlerContext.fireUserEventTriggered(response);
        } else {
            clientChannelHandlerObserver.processResponse(getByteArray(response));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Error in ClientChannelHandler" + cause.getMessage());
        Exception exception = new Exception("Error in ClientChannelHandler", cause.getCause());
        exception.setStackTrace(cause.getStackTrace());
        throw exception;
    }
}
