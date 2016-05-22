package com.tilal6991.channels.redux

import com.brianegan.bansa.Action
import com.brianegan.bansa.Store
import rx.Observable
import rx.Scheduler
import rx.subscriptions.Subscriptions

class RxStore<S>(private val store: Store<S>, private val scheduler: Scheduler) {

    private val worker = scheduler.createWorker()

    fun dispatch(action: Action) {
        worker.schedule { store.dispatch(action) }
    }

    fun observable(): Observable<S> {
        return Observable.create { subs ->
            val handle = store.subscribe {
                if (!subs.isUnsubscribed) {
                    subs.onNext(it)
                }
            }

            subs.add(Subscriptions.create { handle.unsubscribe() })

            // Emit initial value.
            worker.schedule {
                if (!subs.isUnsubscribed) {
                    subs.onNext(store.state)
                }
            }
        }
    }
}