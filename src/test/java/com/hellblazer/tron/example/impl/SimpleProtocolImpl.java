package com.hellblazer.tron.example.impl;

import com.hellblazer.tron.example.BufferHandler;
import com.hellblazer.tron.example.SimpleProtocol;

public class SimpleProtocolImpl implements SimpleProtocol {
    private BufferHandler handler;

    @Override
    public void setHandler(BufferHandler handler) {
        this.handler = handler;
    }

    public BufferHandler getHandler() {
        return handler;
    }

    @Override
    public void establishClientSession() {
        // TODO Auto-generated method stub

    }

    @Override
    public void awaitAck() {
        // TODO Auto-generated method stub

    }

    @Override
    public void transmitMessage(String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendGoodbye() {
        // TODO Auto-generated method stub

    }

    @Override
    public void ackReceived() {
        // TODO Auto-generated method stub

    }

}
