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

}
