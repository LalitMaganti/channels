package com.tilal6991.channels.redux.reselect.selector;

public interface Selector<S, P, R> {
    R invoke(S state, P props);
}