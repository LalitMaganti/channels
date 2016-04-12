package com.tilal6991.channels.redux.bansa

fun <A, S> combineReducers(vararg reducers: (S, A) -> S): (S, A) -> S =
        { state: S, action: A ->
            reducers.fold(state, { state, reducer -> reducer(state, action) })
        }
