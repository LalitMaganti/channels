package co.fusionx.channels.presenter

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.fusionx.channels.R
import co.fusionx.channels.activity.ConfigurationEditActivity
import co.fusionx.channels.activity.helper.EmptyWatcher
import co.fusionx.channels.activity.helper.ErrorWatcher
import co.fusionx.channels.databinding.ConfigurationEditServerBinding
import co.fusionx.channels.presenter.Presenter
import co.fusionx.channels.configuration.Configuration
import co.fusionx.channels.configuration.ServerConfiguration
import org.parceler.Parcels

class ConfigurationServerPresenter(override val activity: Activity,
                                   override val binding: ConfigurationEditServerBinding,
                                   private val inputConfig: ServerConfiguration? = null) : ConfigurationEditActivity.Presenter {
    override val id: String
        get() = "configuration_server"

    private lateinit var configuration: ServerConfiguration

    override fun setup(savedState: Bundle?) {
        if (savedState == null) {
            configuration = inputConfig ?: ServerConfiguration()
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

    companion object {
        private const val CONFIGURATION = "configuration"
    }
}