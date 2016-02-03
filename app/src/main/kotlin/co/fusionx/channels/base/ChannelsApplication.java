package co.fusionx.channels.base;

import android.app.Application;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.view.View;
import android.widget.TextView;

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

    @BindingAdapter({"dynamicText"})
    public static void setFormattedText(TextView textView, int dynamicText) {
        if (dynamicText == 0) return;
        textView.setText(textView.getResources().getString(dynamicText));
    }

    public void onCreate() {
        super.onCreate();

        provider = DaggerChannelsObjectProvider.builder()
                .relayModule(new RelayModule(this))
                .build();
        Timber.plant(new Timber.DebugTree());
    }
}
