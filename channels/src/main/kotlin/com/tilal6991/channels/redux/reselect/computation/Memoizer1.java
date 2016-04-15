package com.tilal6991.channels.redux.reselect.computation;

public interface Memoizer1<T1, R> {
    Computation1<T1, R> invoke(Computation1<T1, R> func);
}