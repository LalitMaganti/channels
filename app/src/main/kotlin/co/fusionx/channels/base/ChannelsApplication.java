package co.fusionx.channels.base;

import android.app.Application;

import co.fusionx.channels.inject.ChannelsObjectProvider;
import co.fusionx.channels.inject.DaggerChannelsObjectProvider;
import co.fusionx.channels.inject.RelayModule;
import timber.log.Timber;

public class ChannelsApplication extends Application {

    ChannelsObjectProvider provider;

    public void onCreate() {
        super.onCreate();

        provider = DaggerChannelsObjectProvider.builder()
                .relayModule(new RelayModule(this))
                .build();
        Timber.plant(new Timber.DebugTree());
    }
}
