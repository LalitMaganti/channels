package com.tilal6991.channels.presenter

import android.app.Activity
import android.databinding.BaseObservable
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.view.ViewCompat
import android.util.SparseArray
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.tilal6991.channels.R
import com.tilal6991.channels.activity.ConfigurationEditActivity
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.configuration.UserConfiguration
import com.tilal6991.channels.databinding.ConfigurationEditAuthBinding
import com.tilal6991.channels.presenter.helper.EmptyWatcher
import com.tilal6991.channels.util.isNotEmpty

class ConfigurationAuthPresenter(override val activity: Activity,
                                 override val binding: ConfigurationEditAuthBinding,
                                 private val inputConfig: ChannelsConfiguration?) : ConfigurationEditActivity.Presenter() {
    lateinit var configuration: Configuration

    override val id: String
        get() = "configuration_auth"
    override val title: String
        get() = activity.getString(R.string.auth_settings)

    override fun setup(savedState: Bundle?) {
        if (savedState == null) {
            configuration = if (inputConfig == null) Configuration() else Configuration(inputConfig)
        } else {
            configuration = savedState.getParcelable(CONFIGURATION) ?: Configuration()
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

        val authIndex = configuration.authIndex.get()
        spinner.setSelection(authIndex)
        onSpinnerPositionChanged(authIndex)

        if (authIndex == NICKSERV_INDEX) {
            if (configuration.password.get() == null) {
                binding.nickservContainer.error = getString(R.string.empty_error)
            }
        } else if (authIndex == SASL_INDEX) {
            if (configuration.username.get() == null) {
                binding.saslUsernameContainer.error = getString(R.string.empty_error)
            }
            if (configuration.password.get() == null) {
                binding.saslPasswordContainer.error = getString(R.string.empty_error)
            }
        }

        binding.saslUsername.addTextChangedListener(EmptyWatcher(binding.saslUsernameContainer))
        binding.saslPassword.addTextChangedListener(EmptyWatcher(binding.saslPasswordContainer))

        binding.nickservPassword.addTextChangedListener(EmptyWatcher(binding.nickservContainer))

        binding.serverUsername.addTextChangedListener(EmptyWatcher(binding.serverUsernameContainer))

        addNullableBackendListeners(
                binding.saslUsername to configuration.username,
                binding.saslPassword to configuration.password,
                binding.nickservPassword to configuration.password,
                binding.serverPassword to configuration.serverPassword)

        addNonNullBackendListeners(
                binding.serverUsername to configuration.serverUsername)
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

    private fun onSpinnerPositionChanged(position: Int) {
        configuration.authIndex.set(position)

        if (position == NONE_INDEX) {
            hide(binding.nickservContainer)
            hide(binding.saslContainer)
        } else if (position == SASL_INDEX) {
            binding.nickservContainer.visibility = View.GONE
            show(binding.saslContainer)
        } else if (position == NICKSERV_INDEX) {
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

    class Configuration @JvmOverloads constructor(
            var authIndex: ObservableInt = ObservableInt(0),
            var username: ObservableField<CharSequence?> = ObservableField(null),
            var password: ObservableField<CharSequence?> = ObservableField(null),
            var serverUsername: ObservableField<CharSequence> = ObservableField("ChannelsUser"),
            var serverPassword: ObservableField<CharSequence?> = ObservableField(null)) : BaseObservable(), Parcelable {

        constructor(c: ChannelsConfiguration) : this(
                ObservableInt(authTypeToIndex(c.user.authType)),
                ObservableField(c.user.authUsername),
                ObservableField(c.user.authPassword),
                ObservableField(c.server.username),
                ObservableField(c.server.password))

        fun isValid(): Boolean {
            val userValidity: Boolean
            if (authIndex.get() == SASL_INDEX) {
                userValidity = username.isNotEmpty() && password.isNotEmpty()
            } else if (authIndex.get() == NICKSERV_INDEX) {
                userValidity = password.isNotEmpty()
            } else {
                userValidity = true
            }
            return userValidity && serverUsername.isNotEmpty()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(authIndex.get())
            dest.writeString(username.get()?.toString() ?: "")
            dest.writeString(password.get()?.toString() ?: "")
            dest.writeString(serverUsername.get()?.toString() ?: "")
            dest.writeString(serverPassword.get()?.toString() ?: "")
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object {
            @JvmField final val CREATOR: Parcelable.Creator<Configuration> = object : Parcelable.Creator<Configuration> {
                override fun createFromParcel(source: Parcel): Configuration {
                    return Configuration(
                            ObservableInt(source.readInt()),
                            ObservableField(source.readString()),
                            ObservableField(source.readString()),
                            ObservableField(source.readString()),
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

        const val NONE_INDEX = 0
        const val SASL_INDEX = 1
        const val NICKSERV_INDEX = 2

        private fun authTypeToIndex(authType: Int): Int = when (authType) {
            UserConfiguration.NONE_AUTH_TYPE -> NONE_INDEX
            UserConfiguration.SASL_AUTH_TYPE -> SASL_INDEX
            UserConfiguration.NICKSERV_AUTH_TYPE -> NICKSERV_INDEX
            else -> -1
        }

        fun indexToType(authIndex: ObservableInt): Int = when (authIndex.get()) {
            NONE_INDEX -> UserConfiguration.NONE_AUTH_TYPE
            SASL_INDEX -> UserConfiguration.SASL_AUTH_TYPE
            NICKSERV_INDEX -> UserConfiguration.NICKSERV_AUTH_TYPE
            else -> -1
        }
    }
}