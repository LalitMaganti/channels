package co.fusionx.channels.controller

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import butterknife.bindView
import co.fusionx.channels.R

class ConfigurationEditActivity : AppCompatActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val tabs: TabLayout by bindView(R.id.tabs)
    private val pager: ViewPager by bindView(R.id.view_pager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.configuration_edit)
        setSupportActionBar(toolbar)

        supportActionBar!!.title = getString(R.string.configuration_add_title)

        pager.adapter = Adapter(this, supportFragmentManager)
        pager.offscreenPageLimit = 2
        tabs.setupWithViewPager(pager)
    }

    class Adapter(private val context: Context,
                  private val fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> ServerFragment()
                1 -> UserFragment()
                2 -> AuthFragment()
                else -> null
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> context.getString(R.string.connection_settings)
                1 -> context.getString(R.string.user_settings)
                2 -> context.getString(R.string.auth_settings)
                else -> null
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }

    class AuthFragment : Fragment() {
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

    class ServerFragment : Fragment() {
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

    class UserFragment : Fragment() {
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

    open class ErrorWatcher(private val layout: TextInputLayout,
                            private val errorFn: (CharSequence) -> Boolean,
                            private val errorMsg: Int) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (errorFn(s)) {
                layout.error = layout.context.getString(errorMsg)
            } else {
                layout.isErrorEnabled = false
                layout.error = null
            }
        }
    }

    class EmptyWatcher(private val layout: TextInputLayout) : ErrorWatcher(layout, { it.isEmpty() }, R.string.empty_error)
}
