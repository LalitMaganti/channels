package co.fusionx.channels.activity

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.fusionx.channels.R
import co.fusionx.channels.activity.helper.EmptyWatcher
import co.fusionx.channels.activity.helper.ErrorWatcher
import co.fusionx.channels.databinding.ConfigurationEditServerBinding
import co.fusionx.channels.relay.Configuration
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.Parcels

class ConfigurationServerFragment : Fragment() {

    private lateinit var configuration: ServerConfiguration

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = ConfigurationEditServerBinding.inflate(inflater, container, false)

        val parcelable: Parcelable
        if (savedInstanceState == null) {
            parcelable = arguments.getParcelable<Parcelable>(CONFIGURATION_ARGUMENT)
        } else {
            parcelable = savedInstanceState.getParcelable<Parcelable>(CONFIGURATION_ARGUMENT)
        }

        configuration = Parcels.unwrap<ServerConfiguration>(parcelable)
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

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(CONFIGURATION_ARGUMENT, Parcels.wrap(configuration))
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
    data class ServerConfiguration @JvmOverloads constructor(
            var name: String? = null, var hostname: String? = null, var port: Int = 6667,
            var ssl: Boolean = false, var sslAllCerts: Boolean = false) {

        constructor(configuration: Configuration) : this(configuration.name,
                configuration.connection.hostname, configuration.connection.port,
                false, false)
    }

    companion object {
        const val CONFIGURATION_ARGUMENT = "CONFIGURATION"

        fun create(input: Configuration? = null): ConfigurationServerFragment {
            val fragment = ConfigurationServerFragment()
            fragment.arguments = Bundle()

            val configuration = if (input == null) ServerConfiguration() else ServerConfiguration(input)
            fragment.arguments.putParcelable(CONFIGURATION_ARGUMENT, Parcels.wrap(configuration))
            return fragment
        }
    }
}