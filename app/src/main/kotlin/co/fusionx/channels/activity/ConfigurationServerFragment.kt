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
import co.fusionx.channels.activity.helper.ErrorWatcher

class ConfigurationServerFragment : Fragment() {
    private val nameContainer: TextInputLayout by bindView(R.id.name_container)
    private val name: EditText by bindView(R.id.name)

    private val urlContainer: TextInputLayout by bindView(R.id.url_container)
    private val url: EditText by bindView(R.id.url)

    private val portContainer: TextInputLayout by bindView(R.id.port_container)
    private val port: EditText by bindView(R.id.port)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.configuration_edit_server, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameContainer.error = getString(R.string.empty_error)
        name.addTextChangedListener(EmptyWatcher(nameContainer))

        urlContainer.error = getString(R.string.empty_error)
        url.addTextChangedListener(EmptyWatcher(urlContainer))

        port.addTextChangedListener(EmptyWatcher(portContainer))
        port.addTextChangedListener(ErrorWatcher(portContainer, { isValidPort(it) }, R.string.port_error))
    }

    private fun isValidPort(it: CharSequence): Boolean {
        try {
            val int = Integer.parseInt(it.toString())
            return int > 0 && int < 65536
        } catch (ex: NumberFormatException) {
            return false
        }
    }
}