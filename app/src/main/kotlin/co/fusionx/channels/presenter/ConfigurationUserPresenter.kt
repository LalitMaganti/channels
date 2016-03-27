package co.fusionx.channels.presenter

import android.app.Activity
import android.databinding.BaseObservable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import co.fusionx.channels.R
import co.fusionx.channels.activity.ConfigurationEditActivity
import co.fusionx.channels.configuration.ChannelsConfiguration
import co.fusionx.channels.databinding.ConfigurationEditUserBinding
import co.fusionx.channels.presenter.helper.EmptyWatcher
import co.fusionx.channels.util.isNotEmpty
import co.fusionx.channels.util.isValidPort
import org.parceler.Parcels

class ConfigurationUserPresenter(override val activity: Activity,
                                 override val binding: ConfigurationEditUserBinding,
                                 private val inputConfig: ChannelsConfiguration? = null) : ConfigurationEditActivity.Presenter() {
    lateinit var configuration: Configuration

    override val id: String
        get() = "configuration_user"
    override val title: String
        get() = activity.getString(R.string.user_settings)

    override fun setup(savedState: Bundle?) {
        if (savedState == null) {
            configuration = if (inputConfig == null) Configuration() else Configuration(inputConfig)
        } else {
            configuration = savedState.getParcelable(CONFIGURATION) ?: Configuration()
        }
        binding.configuration = configuration

        binding.nick.addTextChangedListener(EmptyWatcher(binding.nickContainer))

        binding.realName.addTextChangedListener(EmptyWatcher(binding.realNameContainer))

        addNonNullBackendListeners(
                binding.nick to configuration.nick,
                binding.realName to configuration.realName)

        addBooleanBackendListeners(
                binding.autoChangeNick to configuration.autoChangeNick)
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

    class Configuration(val nick: ObservableField<CharSequence> = ObservableField("ChannelsUser"),
                        val autoChangeNick: ObservableBoolean = ObservableBoolean(true),
                        val realName: ObservableField<CharSequence> = ObservableField("ChannelsUser")) : BaseObservable(), Parcelable {

        constructor(c: ChannelsConfiguration) : this(
                ObservableField(c.user.nicks[0]),
                ObservableBoolean(c.user.autoChangeNick),
                ObservableField(c.user.realName))

        fun isValid(): Boolean {
            return nick.isNotEmpty() && realName.isNotEmpty()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(nick.get().toString())
            dest.writeInt(if (autoChangeNick.get()) 1 else 0)
            dest.writeString(realName.get().toString())
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object {
            @JvmField final val CREATOR: Parcelable.Creator<Configuration> = object : Parcelable.Creator<Configuration> {
                override fun createFromParcel(source: Parcel): Configuration {
                    return Configuration(
                            ObservableField(source.readString()),
                            ObservableBoolean(source.readInt() != 0),
                            ObservableField(source.readString()))
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