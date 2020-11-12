package com.example.nettyclient.utilities;

import io.netty.buffer.ByteBuf;

public class Utilities {

    public static byte[] getByteArray(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        int readerIndex = byteBuf.readerIndex();
        byteBuf.getBytes(readerIndex, bytes);
        return bytes;
    }
}
