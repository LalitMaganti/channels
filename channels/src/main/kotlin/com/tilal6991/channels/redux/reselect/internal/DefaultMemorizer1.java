package com.tilal6991.channels.redux.reselect.internal;

import com.tilal6991.channels.redux.reselect.computation.Computation1;
import com.tilal6991.channels.redux.reselect.computation.Memoizer1;

public class DefaultMemorizer1<T1, R> implements Memoizer1<T1, R> {

    public Computation1<T1, R> invoke(Computation1<T1, R> func) {
        return new MemoizedComputation<>(func);
    }

    private static class MemoizedComputation<T1, R> implements Computation1<T1, R> {
        private T1 lastT1 = null;
        private R lastR = null;

        private Computation1<T1, R> func;

        public MemoizedComputation(Computation1<T1, R> func) {
            this.func = func;
        }

        @Override
        public R invoke(T1 t1) {
            if (lastR == null || lastT1 != t1) {
                lastT1 = t1;
                lastR = func.invoke(t1);
            }
            return lastR;
        }
    }
}