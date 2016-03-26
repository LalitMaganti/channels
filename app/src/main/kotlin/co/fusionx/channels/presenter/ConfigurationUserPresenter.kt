package co.fusionx.channels.presenter

import android.app.Activity
import android.os.Bundle
import co.fusionx.channels.activity.ConfigurationEditActivity
import co.fusionx.channels.activity.helper.EmptyWatcher
import co.fusionx.channels.configuration.ChannelsConfiguration
import co.fusionx.channels.databinding.ConfigurationEditUserBinding
import org.parceler.Parcel
import org.parceler.Parcels

class ConfigurationUserPresenter(override val activity: Activity,
                                 override val binding: ConfigurationEditUserBinding,
                                 private val inputConfig: ChannelsConfiguration? = null) : ConfigurationEditActivity.Presenter {
    private lateinit var configuration: Configuration

    override val id: String
        get() = "configuration_user"

    override fun setup(savedState: Bundle?) {
        if (savedState == null) {
            configuration = if (inputConfig == null) Configuration() else Configuration(inputConfig)
        } else {
            configuration = Parcels.unwrap(savedState.getParcelable(CONFIGURATION))
        }
        binding.configuration = configuration

        binding.nick.addTextChangedListener(EmptyWatcher(binding.nickContainer))

        binding.realName.addTextChangedListener(EmptyWatcher(binding.realNameContainer))
    }

    @Parcel(Parcel.Serialization.BEAN)
    class Configuration(val nicks: List<String> = listOf("ChannelsUser"),
                        val autoChangeNick: Boolean = true,
                        val realName: String = "ChannelsUser") {
        constructor(c: ChannelsConfiguration) : this(
                c.user.nicks,
                c.user.autoChangeNick,
                c.user.realName)
    }

    companion object {
        private const val CONFIGURATION = "configuration"
    }
}