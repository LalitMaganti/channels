package co.fusionx.channels.presenter

import android.app.Activity
import android.os.Bundle
import co.fusionx.channels.activity.ConfigurationEditActivity
import co.fusionx.channels.activity.helper.EmptyWatcher
import co.fusionx.channels.databinding.ConfigurationEditUserBinding

class ConfigurationUserPresenter(override val activity: Activity,
                                 override val binding: ConfigurationEditUserBinding) : ConfigurationEditActivity.Presenter {

    override val id: String
        get() = "configuration_user"

    override fun setup(savedState: Bundle?) {
        binding.nick.addTextChangedListener(EmptyWatcher(binding.nickContainer))

        binding.realName.addTextChangedListener(EmptyWatcher(binding.realNameContainer))
    }
}