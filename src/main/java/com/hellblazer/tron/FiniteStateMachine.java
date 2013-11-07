package com.hellblazer.tron;

public interface FiniteStateMachine<Context, Transitions> {
    void push(State state);

    void pop();

    State getCurrentState();

    void setCurrentState(State state);

    State previous();

    String transition();

    Context getContext();

    void enterStartState();
    
    Transitions getTransitions();
    
}
