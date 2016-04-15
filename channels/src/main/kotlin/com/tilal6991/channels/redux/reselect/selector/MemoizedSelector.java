package com.tilal6991.channels.redux.reselect.selector;

public interface MemoizedSelector<S, P, R> extends Selector<S, P, R> {
    int getRecomputations();
    void resetRecomputations();
}