package com.tilal6991.channels.redux.reselect.internal;

import com.tilal6991.channels.redux.reselect.computation.Computation2;
import com.tilal6991.channels.redux.reselect.computation.Memoizer2;

public class DefaultMemorizer2<T1, T2, R> implements Memoizer2<T1, T2, R> {

    public Computation2<T1, T2, R> invoke(Computation2<T1, T2, R> func) {
        return new MemoizedComputation<>(func);
    }

    private static class MemoizedComputation<T1, T2, R> implements Computation2<T1, T2, R> {
        private T1 lastT1 = null;
        private T2 lastT2 = null;
        private R lastR = null;

        private Computation2<T1, T2, R> func;

        public MemoizedComputation(Computation2<T1, T2, R> func) {
            this.func = func;
        }

        @Override
        public R invoke(T1 t1, T2 t2) {
            if (lastR == null || lastT1 != t1 || lastT2 != t2) {
                lastT1 = t1;
                lastT2 = t2;
                lastR = func.invoke(t1, t2);
            }
            return lastR;
        }
    }
}