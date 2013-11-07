package com.hellblazer.tron;

public interface FiniteStateMachine<Context, Transitions> {
    void enterStartState();

    Context getContext();

    Enum<?> getCurrentState();

    Transitions getTransitions();

    void pop();

    Enum<?> previous();

    void push(Enum<?> state);

    void setCurrentState(Enum<?> state);

    String transition();

}
