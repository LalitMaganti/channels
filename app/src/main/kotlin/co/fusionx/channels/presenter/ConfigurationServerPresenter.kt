package co.fusionx.channels.presenter

import android.app.Activity
import android.os.Bundle
import co.fusionx.channels.R
import co.fusionx.channels.activity.ConfigurationEditActivity
import co.fusionx.channels.activity.helper.EmptyWatcher
import co.fusionx.channels.activity.helper.ErrorWatcher
import co.fusionx.channels.configuration.ChannelsConfiguration
import co.fusionx.channels.databinding.ConfigurationEditServerBinding
import org.parceler.Parcel
import org.parceler.Parcels

class ConfigurationServerPresenter(override val activity: Activity,
                                   override val binding: ConfigurationEditServerBinding,
                                   private val inputConfig: ChannelsConfiguration? = null) : ConfigurationEditActivity.Presenter {
    override val id: String
        get() = "configuration_server"

    private lateinit var configuration: Configuration

    override fun setup(savedState: Bundle?) {
        if (savedState == null) {
            configuration = if (inputConfig == null) Configuration() else Configuration()
        } else {
            configuration = Parcels.unwrap(savedState.getParcelable(CONFIGURATION))
        }
        binding.configuration = configuration

        if (configuration.name == null) {
            binding.nameContainer.error = getString(R.string.empty_error)
        }
        binding.name.addTextChangedListener(EmptyWatcher(binding.nameContainer))

        if (configuration.hostname == null) {
            binding.urlContainer.error = getString(R.string.empty_error)
        }
        binding.url.addTextChangedListener(EmptyWatcher(binding.urlContainer))

        binding.port.addTextChangedListener(EmptyWatcher(binding.portContainer))
        binding.port.addTextChangedListener(ErrorWatcher(binding.portContainer, R.string.port_error) {
            isValidPort(it)
        })
    }

    override fun saveState(): Bundle {
        val bundle = Bundle()
        bundle.putParcelable(CONFIGURATION, Parcels.wrap(configuration))
        return bundle
    }

    private fun isValidPort(it: CharSequence): Boolean {
        try {
            val int = Integer.parseInt(it.toString())
            return int <= 0 && int > 65536
        } catch (ex: NumberFormatException) {
            return false
        }
    }

    @Parcel(Parcel.Serialization.BEAN)
    class Configuration @JvmOverloads constructor(
            var name: String? = null,
            var hostname: String? = null,
            var port: Int = 6667,
            var ssl: Boolean = false,
            var sslAllCerts: Boolean = false) {

        constructor(c: ChannelsConfiguration) : this(
                c.name,
                c.connection.hostname,
                c.connection.port,
                c.connection.ssl,
                c.connection.sslAllCerts)
    }

    companion object {
        private const val CONFIGURATION = "configuration"
    }
}