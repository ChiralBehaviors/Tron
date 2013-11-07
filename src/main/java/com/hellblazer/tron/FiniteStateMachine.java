package com.hellblazer.tron;

public interface FiniteStateMachine<Context, Transitions> {
    void enterStartState();

    Context getContext();

    Transitions getCurrentState();

    Transitions getTransitions();

    void pop();

    Transitions previous();

    void push(Transitions state);

    void setCurrentState(Transitions state);

    String transition();

}
