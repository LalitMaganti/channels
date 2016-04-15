package com.tilal6991.channels.redux.reselect.computation;

public interface Computation2<T1, T2, R> {
    R invoke(T1 t1, T2 t2);
}