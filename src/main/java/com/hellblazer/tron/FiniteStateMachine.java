package com.hellblazer.tron;

public interface FiniteStateMachine<Context, Transitions> {
    void push(Enum<?> state);

    void pop();

    Enum<?> getCurrentState();

    void setCurrentState(Enum<?> state);

    Enum<?> previous();

    String transition();

    Context getContext();

    void enterStartState();
    
    Transitions getTransitions();
    
}
