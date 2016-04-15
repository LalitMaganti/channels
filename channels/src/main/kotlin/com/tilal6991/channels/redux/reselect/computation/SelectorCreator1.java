package com.tilal6991.channels.redux.reselect.computation;

import com.tilal6991.channels.redux.reselect.selector.MemoizedSelector;
import com.tilal6991.channels.redux.reselect.selector.Selector;

public interface SelectorCreator1<S, P, T1, R> {
    MemoizedSelector<S, P, R> create(Selector<S, P, T1> s1, Computation1<T1, R> result);
}