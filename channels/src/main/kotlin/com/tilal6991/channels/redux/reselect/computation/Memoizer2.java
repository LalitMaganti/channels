package com.tilal6991.channels.redux.reselect.computation;

public interface Memoizer2<T1, T2, R> {
    Computation2<T1, T2, R> invoke(Computation2<T1, T2, R> func);
}