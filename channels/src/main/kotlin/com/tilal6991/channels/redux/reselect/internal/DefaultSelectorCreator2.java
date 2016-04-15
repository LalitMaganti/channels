package com.tilal6991.channels.redux.reselect.internal;

import com.tilal6991.channels.redux.reselect.computation.Computation2;
import com.tilal6991.channels.redux.reselect.computation.Memoizer2;
import com.tilal6991.channels.redux.reselect.computation.SelectorCreator2;
import com.tilal6991.channels.redux.reselect.selector.MemoizedSelector;
import com.tilal6991.channels.redux.reselect.selector.Selector;

public class DefaultSelectorCreator2<S, P, T1, T2, R> implements SelectorCreator2<S, P, T1, T2, R> {

    private final Memoizer2<T1, T2, R> memoizer;

    public DefaultSelectorCreator2(Memoizer2<T1, T2, R> memoizer) {
        this.memoizer = memoizer;
    }

    @Override
    public MemoizedSelector<S, P, R> create(Selector<S, P, T1> s1,
                                            Selector<S, P, T2> s2,
                                            Computation2<T1, T2, R> result) {
        return new MemoizedSelector2<>(memoizer, s1, s2, result);
    }

    private static class MemoizedSelector2<S, P, T1, T2, R> implements MemoizedSelector<S, P, R> {

        private final Selector<S, P, T1> s1;
        private final Selector<S, P, T2> s2;
        private final Computation2<T1, T2, R> memoisedResultFunction;

        private int recomputations = 0;

        public MemoizedSelector2(Memoizer2<T1, T2, R> memoizer,
                                 Selector<S, P, T1> s1,
                                 Selector<S, P, T2> s2,
                                 final Computation2<T1, T2, R> result) {
            this.s1 = s1;
            this.s2 = s2;
            this.memoisedResultFunction = memoizer.invoke(new Computation2<T1, T2, R>() {
                @Override
                public R invoke(T1 t1, T2 t2) {
                    recomputations++;
                    return result.invoke(t1, t2);
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
            T2 t2 = s2.invoke(state, props);
            return memoisedResultFunction.invoke(t1, t2);
        }
    }
}