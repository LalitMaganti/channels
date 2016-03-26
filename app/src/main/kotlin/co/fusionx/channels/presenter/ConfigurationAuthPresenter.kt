package co.fusionx.channels.presenter

import android.app.Activity
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import co.fusionx.channels.R
import co.fusionx.channels.activity.ConfigurationEditActivity
import co.fusionx.channels.activity.helper.EmptyWatcher
import co.fusionx.channels.configuration.ChannelsConfiguration
import co.fusionx.channels.configuration.UserConfiguration
import co.fusionx.channels.databinding.ConfigurationEditAuthBinding
import org.parceler.Parcel
import org.parceler.Parcels

class ConfigurationAuthPresenter(override val activity: Activity,
                                 override val binding: ConfigurationEditAuthBinding,
                                 private val inputConfig: ChannelsConfiguration? = null) : ConfigurationEditActivity.Presenter {
    private lateinit var configuration: Configuration

    override val id: String
        get() = "configuration_auth"

    override fun setup(savedState: Bundle?) {
        if (savedState == null) {
            configuration = if (inputConfig == null) Configuration() else Configuration()
        } else {
            configuration = Parcels.unwrap(savedState.getParcelable(CONFIGURATION))
        }
        binding.configuration = configuration

        val spinner = binding.authenticationChooser
        spinner.adapter = ArrayAdapter<String>(
                activity,
                R.layout.support_simple_spinner_dropdown_item,
                arrayOf("None", "SASL", "NickServ")
        )
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(view: AdapterView<*>?) {
            }

            override fun onItemSelected(av: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onSpinnerPositionChanged(position)
            }
        }
        spinner.setSelection(0)
        onSpinnerPositionChanged(0)

        binding.nickservContainer.error = getString(R.string.empty_error)
        binding.nickservPassword.addTextChangedListener(EmptyWatcher(binding.nickservContainer))

        binding.saslUsernameContainer.error = getString(R.string.empty_error)
        binding.saslUsername.addTextChangedListener(EmptyWatcher(binding.saslUsernameContainer))

        binding.saslPasswordContainer.error = getString(R.string.empty_error)
        binding.saslPassword.addTextChangedListener(EmptyWatcher(binding.saslPasswordContainer))

        binding.serverUsername.addTextChangedListener(EmptyWatcher(binding.serverUsernameContainer))
    }

    private fun onSpinnerPositionChanged(position: Int) {
        if (position == 0) {
            hide(binding.nickservContainer)
            hide(binding.saslContainer)
        } else if (position == 1) {
            binding.nickservContainer.visibility = View.GONE
            show(binding.saslContainer)
        } else if (position == 2) {
            show(binding.nickservContainer)
            binding.saslContainer.visibility = View.GONE
        }
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun show(view: View) {
        if (view.visibility == View.VISIBLE) {
            return
        }

        view.alpha = 0f
        view.visibility = View.VISIBLE

        ViewCompat.animate(view)
                .setDuration(400L)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .alpha(1f)
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun hide(view: View) {
        if (view.visibility != View.VISIBLE) {
            return
        }

        view.alpha = 1f
        ViewCompat.animate(view)
                .setDuration(400L)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .alpha(0f)
                .withEndAction { view.visibility = View.GONE }
    }

    @Parcel(Parcel.Serialization.BEAN)
    class Configuration @JvmOverloads constructor(
            var authType: Int = UserConfiguration.NONE_AUTH_TYPE,
            var username: String? = null,
            var password: String? = null,
            var serverUsername: String = "ChannelsUser",
            var serverPassword: String? = null) {

        constructor(c: ChannelsConfiguration) : this(
                c.user.authType,
                c.user.authUsername,
                c.user.authPassword,
                c.server.username,
                c.server.password)
    }

    companion object {
        private const val CONFIGURATION = "configuration"
    }
}