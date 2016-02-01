package co.fusionx.channels.relay;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import co.fusionx.channels.BR;

public abstract class ClientChild extends BaseObservable {

    private ObservableList<CharSequence> buffer = new ObservableArrayList<>()

    public void add(String message) {
        buffer.add(message);
        notifyPropertyChanged(BR.message);
    }

    public final ObservableList<CharSequence> getBuffer() {
        return buffer;
    }

    @Bindable
    public final CharSequence getMessage() {
        return buffer.isEmpty() ? "No message to show" : buffer.get(buffer.size() - 1);
    }

    public abstract CharSequence getName();
}