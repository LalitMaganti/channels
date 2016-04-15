package com.tilal6991.channels.redux.reselect.computation;

import com.tilal6991.channels.redux.reselect.selector.MemoizedSelector;
import com.tilal6991.channels.redux.reselect.selector.Selector;

public interface SelectorCreator2<S, P, T1, T2, R> {

    MemoizedSelector<S, P, R> create(Selector<S, P, T1> s1,
                                     Selector<S, P, T2> s2,
                                     Computation2<T1, T2, R> result);
}