package com.example.nettyclient.handler;

public abstract class ClientChannelHandlerObserver {
    public ClientChannelHandler clientChannelHandler;
    public abstract void processResponse(byte[] response);
}
