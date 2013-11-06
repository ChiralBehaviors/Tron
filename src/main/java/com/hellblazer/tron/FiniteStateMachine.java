package com.hellblazer.tron;

public interface FiniteStateMachine<Context> {
    void push(State state);

    void pop();

    State current();

    State previous();

    String transition();

    Context getContext();

}
