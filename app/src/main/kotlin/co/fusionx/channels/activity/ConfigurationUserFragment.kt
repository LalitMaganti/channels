package co.fusionx.channels.activity

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.bindView
import co.fusionx.channels.R
import co.fusionx.channels.activity.helper.EmptyWatcher

class ConfigurationUserFragment : Fragment() {
    private val nickContainer: TextInputLayout by bindView(R.id.nick_container)
    private val nick: EditText by bindView(R.id.nick)

    private val realNameContainer: TextInputLayout by bindView(R.id.real_name_container)
    private val realName: EditText by bindView(R.id.real_name)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.configuration_edit_user, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nick.addTextChangedListener(EmptyWatcher(nickContainer))

        realName.addTextChangedListener(EmptyWatcher(realNameContainer))
    }
}