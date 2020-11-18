package com.example.nettyclient.service;

import com.example.nettyclient.config.NettyClient;
import com.example.nettyclient.handler.ClientChannelHandler;
import com.example.nettyclient.handler.ClientChannelHandlerObserver;
import com.example.nettyclient.model.RentalRequest;
import com.example.nettyclient.model.RentalResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@Scope(value = "prototype")
public class ClientGateway extends ClientChannelHandlerObserver implements ClientGatewayInterface {

    private static final Logger logger = LoggerFactory.getLogger(ClientGateway.class);

    private CountDownLatch countDownLatch;
    private RentalResponse rentalResponse;
    public static final byte CARRIAGE_RETURN_BYTE = 0X0D;
    StopWatch watch = new StopWatch();

    @Autowired
    private NettyClient nettyClient;

    @Value("${netty.timeout}")
    private int timeout;

    public ClientGateway() {
        logger.info("Creating countdown");
        this.countDownLatch = createCountDownLatch();
    }

    private CountDownLatch createCountDownLatch() {
        return new CountDownLatch(1);
    }

    @Override
    public RentalResponse status(RentalRequest rentalRequest) {

        logger.info("Entering status() method");

        rentalResponse = new RentalResponse();
        rentalResponse.setId(rentalRequest.getId());
        try {
            handleRequest(ArrayUtils.add(rentalRequest.getColor().getBytes(), CARRIAGE_RETURN_BYTE), rentalRequest.getId());
            rentalResponse.setStatus("booked");
        } catch (Exception e) {
            logger.error("Error in sending data", e);
            rentalResponse.setStatus("Not booked");
        }
        rentalResponse.setCarType(rentalRequest.getCarType());
        logger.info("Total gateway time: " + (int) watch.getTime());
        return rentalResponse;
    }

    private void handleRequest(byte[] bytes, String id) throws Exception {
        logger.info("Sending request to server");
        Channel channel = null;
        try {
            clientChannelHandler = new ClientChannelHandler(this);

            channel = nettyClient.getChannel();
            ByteBuf byteBuf = nettyClient.getByteBufAllocator().ioBuffer();
            byteBuf.writeBytes(bytes);
            if (channel != null) {
                logger.info("Adding handler to pipeline");
                channel.pipeline().addLast(id, clientChannelHandler);

                logger.info("Sending data");
                watch.start();
                channel.writeAndFlush(byteBuf);
                watch.stop();
                logger.info("Sent data");

                countDownLatch.await(timeout, TimeUnit.SECONDS);
            }

            if (rentalResponse.getModel() == null) {
                throw new Exception("Timed out, did not get response");
            }

        } catch (Exception e) {
            throw e;
        } finally {

            // Free up channel
            clientChannelHandler = null;
            if (channel != null && channel.pipeline().toMap().containsKey(id)) {
                channel.pipeline().remove(id);
            }
        }
    }

    @Override
    public void processResponse(byte[] response) {
        logger.info("Received message");

        try {
            int length = response.length;
            byte[] responseWithoutCarriageReturn = Arrays.copyOfRange(response, 0, length - 1);
            rentalResponse.setModel(new String(responseWithoutCarriageReturn));
        } finally {
            countDownLatch.countDown();
        }
    }
}
