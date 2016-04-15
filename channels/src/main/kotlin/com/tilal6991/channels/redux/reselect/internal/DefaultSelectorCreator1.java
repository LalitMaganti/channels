package com.tilal6991.channels.redux.reselect.internal;

import com.tilal6991.channels.redux.reselect.computation.Computation1;
import com.tilal6991.channels.redux.reselect.computation.Memoizer1;
import com.tilal6991.channels.redux.reselect.computation.SelectorCreator1;
import com.tilal6991.channels.redux.reselect.selector.MemoizedSelector;
import com.tilal6991.channels.redux.reselect.selector.Selector;

public class DefaultSelectorCreator1<S, P, T1, R> implements SelectorCreator1<S, P, T1, R> {

    private final Memoizer1<T1, R> memoizer;

    public DefaultSelectorCreator1(Memoizer1<T1, R> memoizer) {
        this.memoizer = memoizer;
    }

    @Override
    public MemoizedSelector<S, P, R> create(Selector<S, P, T1> s1, Computation1<T1, R> result) {
        return new MemoizedSelector1<>(memoizer, s1, result);
    }

    private static class MemoizedSelector1<S, P, T1, R> implements MemoizedSelector<S, P, R> {

        private final Selector<S, P, T1> s1;
        private final Computation1<T1, R> memoisedResultFunction;

        private int recomputations = 0;

        public MemoizedSelector1(Memoizer1<T1, R> memoizer,
                                 Selector<S, P, T1> s1,
                                 final Computation1<T1, R> result) {
            this.s1 = s1;
            this.memoisedResultFunction = memoizer.invoke(new Computation1<T1, R>() {
                @Override
                public R invoke(T1 t1) {
                    recomputations++;
                    return result.invoke(t1);
                }
            });
        }

        @Override
        public int getRecomputations() {
            return recomputations;
        }

        @Override
        public void resetRecomputations() {
            recomputations = 0;
        }

        @Override
        public R invoke(S state, P props) {
            T1 t1 = s1.invoke(state, props);
            return memoisedResultFunction.invoke(t1);
        }
    }
}