package com.tilal6991.channels.redux.reselect.computation;

public interface Computation1<T1, R> {
    R invoke(T1 t1);
}