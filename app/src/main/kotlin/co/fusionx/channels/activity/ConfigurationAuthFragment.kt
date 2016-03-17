package co.fusionx.channels.activity

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import butterknife.bindView
import co.fusionx.channels.R
import co.fusionx.channels.activity.helper.EmptyWatcher

class ConfigurationAuthFragment : Fragment() {
    private val spinner: Spinner by bindView(R.id.authentication_chooser)

    private val nickservPasswordContainer: TextInputLayout by bindView(R.id.nickserv_continer)
    private val nickservPassword: EditText by bindView(R.id.nicksev_password)

    private val saslContainer: LinearLayout by bindView(R.id.sasl_container)
    private val saslUsernameContainer: TextInputLayout by bindView(R.id.sasl_username_container)
    private val saslUsername: EditText by bindView(R.id.sasl_username)
    private val saslPasswordContainer: TextInputLayout by bindView(R.id.sasl_password_container)
    private val saslPassword: EditText by bindView(R.id.sasl_password)

    private val serverUsernameContainer: TextInputLayout by bindView(R.id.server_username_container)
    private val serverUsername: EditText by bindView(R.id.server_username)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.configuration_edit_auth, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner.adapter = ArrayAdapter<String>(
                activity,
                R.layout.support_simple_spinner_dropdown_item,
                arrayOf("None", "SASL", "NickServ")
        )
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(view: AdapterView<*>?) {
            }

            override fun onItemSelected(av: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    hide(nickservPasswordContainer)
                    hide(saslContainer)
                } else if (position == 1) {
                    nickservPasswordContainer.visibility = View.GONE
                    show(saslContainer)
                } else if (position == 2) {
                    show(nickservPasswordContainer)
                    saslContainer.visibility = View.GONE
                }
            }
        }

        nickservPasswordContainer.error = getString(R.string.empty_error)
        nickservPassword.addTextChangedListener(EmptyWatcher(nickservPasswordContainer))

        saslUsernameContainer.error = getString(R.string.empty_error)
        saslUsername.addTextChangedListener(EmptyWatcher(saslUsernameContainer))

        saslPasswordContainer.error = getString(R.string.empty_error)
        saslPassword.addTextChangedListener(EmptyWatcher(saslPasswordContainer))

        serverUsername.addTextChangedListener(EmptyWatcher(serverUsernameContainer))
    }

    @Suppress("UsePropertyAccessSyntax")
    fun show(view: View) {
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
    fun hide(view: View) {
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
}