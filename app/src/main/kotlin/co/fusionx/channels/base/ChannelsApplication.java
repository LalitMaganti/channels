package co.fusionx.channels.base;

import android.app.Application;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.view.View;

import co.fusionx.channels.inject.ChannelsObjectProvider;
import co.fusionx.channels.inject.DaggerChannelsObjectProvider;
import co.fusionx.channels.inject.RelayModule;
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

        provider = DaggerChannelsObjectProvider.builder()
                .relayModule(new RelayModule(this))
                .build();
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected void log(int priority, String tag, String message, Throwable t) {
                if (t != null) {
                    throw new RuntimeException(t);
                }
                super.log(priority, tag, message, t);
            }
        });
    }
}
