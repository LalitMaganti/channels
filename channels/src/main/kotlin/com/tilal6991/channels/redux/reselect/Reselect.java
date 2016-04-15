package com.tilal6991.channels.redux.reselect;

import com.tilal6991.channels.redux.reselect.computation.Computation1;
import com.tilal6991.channels.redux.reselect.computation.Computation2;
import com.tilal6991.channels.redux.reselect.computation.Memoizer1;
import com.tilal6991.channels.redux.reselect.computation.Memoizer2;
import com.tilal6991.channels.redux.reselect.computation.SelectorCreator1;
import com.tilal6991.channels.redux.reselect.computation.SelectorCreator2;
import com.tilal6991.channels.redux.reselect.internal.DefaultMemorizer1;
import com.tilal6991.channels.redux.reselect.internal.DefaultMemorizer2;
import com.tilal6991.channels.redux.reselect.internal.DefaultSelectorCreator1;
import com.tilal6991.channels.redux.reselect.internal.DefaultSelectorCreator2;
import com.tilal6991.channels.redux.reselect.selector.MemoizedSelector;
import com.tilal6991.channels.redux.reselect.selector.Selector;

public class Reselect {

    public static <S, P, T1, T2, R> SelectorCreator2<S, P, T1, T2, R> createSelectorCreator(
            Memoizer2<T1, T2, R> memoizer) {
        return new DefaultSelectorCreator2<>(memoizer);
    }

    public static <S, P, T1, T2, R> MemoizedSelector<S, P, R> createSelector(
            Selector<S, P, T1> s1,
            Selector<S, P, T2> s2,
            Computation2<T1, T2, R> result) {
        return Reselect.<S, P, T1, T2, R>createSelectorCreator(new DefaultMemorizer2<T1, T2, R>())
                .create(s1, s2, result);
    }


    public static <S, P, T1, R> SelectorCreator1<S, P, T1, R> createSelectorCreator(
            Memoizer1<T1, R> memoizer) {
        return new DefaultSelectorCreator1<>(memoizer);
    }

    public static <S, P, T1, R> MemoizedSelector<S, P, R> createSelector(
            Selector<S, P, T1> f1,
            Computation1<T1, R> result) {
        return Reselect.<S, P, T1, R>createSelectorCreator(new DefaultMemorizer1<T1, R>())
                .create(f1, result);
    }
}
