package co.fusionx.channels.presenter

import android.app.Activity
import android.databinding.BaseObservable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import co.fusionx.channels.R
import co.fusionx.channels.activity.ConfigurationEditActivity
import co.fusionx.channels.configuration.ChannelsConfiguration
import co.fusionx.channels.databinding.ConfigurationEditServerBinding
import co.fusionx.channels.presenter.helper.EmptyWatcher
import co.fusionx.channels.presenter.helper.ErrorWatcher
import co.fusionx.channels.util.isNotEmpty
import co.fusionx.channels.util.isValidPort

class ConfigurationServerPresenter(override val activity: Activity,
                                   override val binding: ConfigurationEditServerBinding,
                                   private val inputConfig: ChannelsConfiguration?) : ConfigurationEditActivity.Presenter() {

    lateinit var configuration: Configuration

    override val id: String
        get() = "configuration_server"
    override val title: String
        get() = activity.getString(R.string.connection_settings)

    override fun setup(savedState: Bundle?) {
        if (savedState == null) {
            configuration = if (inputConfig == null) Configuration() else Configuration(inputConfig)
        } else {
            configuration = savedState.getParcelable(CONFIGURATION) ?: Configuration()
        }
        binding.configuration = configuration

        if (configuration.name.get() == null) {
            binding.nameContainer.error = getString(R.string.empty_error)
        }
        binding.name.addTextChangedListener(EmptyWatcher(binding.nameContainer))

        if (configuration.hostname.get() == null) {
            binding.urlContainer.error = getString(R.string.empty_error)
        }
        binding.url.addTextChangedListener(EmptyWatcher(binding.urlContainer))

        binding.port.addTextChangedListener(ErrorWatcher(binding.portContainer, R.string.port_error) {
            !it.isValidPort()
        })

        addNullableBackendListeners(
                binding.name to configuration.name,
                binding.url to configuration.hostname)

        addIntBackendListeners(
                binding.port to configuration.port)

        addBooleanBackendListeners(
                binding.ssl to configuration.ssl)
    }

    override fun restoreState(bundle: Bundle) {
        val array = bundle.getSparseParcelableArray<Parcelable>(VIEW)
        binding.root.restoreHierarchyState(array)
    }

    override fun saveState(): Bundle {
        val bundle = Bundle()
        bundle.putParcelable(CONFIGURATION, configuration)

        val array = SparseArray<Parcelable>()
        binding.root.saveHierarchyState(array)
        bundle.putSparseParcelableArray(VIEW, array)

        return bundle
    }

    class Configuration @JvmOverloads constructor(
            var name: ObservableField<CharSequence?> = ObservableField(null),
            var hostname: ObservableField<CharSequence?> = ObservableField(null),
            var port: ObservableInt = ObservableInt(6667),
            var ssl: ObservableBoolean = ObservableBoolean(false)) : BaseObservable(), Parcelable {

        constructor(c: ChannelsConfiguration) : this(
                ObservableField(c.name),
                ObservableField(c.server.hostname),
                ObservableInt(c.server.port),
                ObservableBoolean(c.server.ssl))

        fun isValid(): Boolean {
            return name.isNotEmpty() && hostname.isNotEmpty() && port.isValidPort()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(name.get()?.toString() ?: "")
            dest.writeString(hostname.get()?.toString() ?: "")
            dest.writeInt(port.get())
            dest.writeInt(if (ssl.get()) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object {
            @JvmField final val CREATOR: Parcelable.Creator<Configuration> = object : Parcelable.Creator<Configuration> {
                override fun createFromParcel(source: Parcel): Configuration {
                    return Configuration(
                            ObservableField(source.readString()),
                            ObservableField(source.readString()),
                            ObservableInt(source.readInt()),
                            ObservableBoolean(source.readInt() != 0))
                }

                override fun newArray(size: Int): Array<Configuration?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        private const val CONFIGURATION = "configuration"
        private const val VIEW = "view"
    }
}