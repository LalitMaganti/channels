package com.tilal6991.channels.base;

import android.app.Application;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.util.Log;
import android.view.View;

import com.squareup.leakcanary.LeakCanary;
import com.tilal6991.channels.inject.ChannelsObjectProvider;
import com.tilal6991.channels.inject.DaggerChannelsObjectProvider;
import com.tilal6991.channels.inject.RelayModule;
import timber.log.Timber;

@BindingMethods({
        @BindingMethod(type = View.class,
                attribute = "android:onClick",
                method = "setOnClickListener")
})
public class ChannelsApplication extends Application {

    ChannelsObjectProvider provider;

    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        provider = DaggerChannelsObjectProvider.builder()
                .relayModule(new RelayModule(this))
                .build();
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected void log(int priority, String tag, String message, Throwable t) {
                if (priority == Log.ERROR) {
                    if (t != null) {
                        throw new RuntimeException(t);
                    } else {
                        throw new IllegalStateException(message);
                    }
                }
                super.log(priority, tag, message, t);
            }
        });
    }
}
