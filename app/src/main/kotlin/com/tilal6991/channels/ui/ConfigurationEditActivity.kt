package com.tilal6991.channels.ui

import android.content.Intent
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import butterknife.bindView
import com.tilal6991.channels.R
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.configuration.ServerConfiguration
import com.tilal6991.channels.configuration.UserConfiguration
import com.tilal6991.channels.databinding.ConfigurationEditAuthBinding
import com.tilal6991.channels.databinding.ConfigurationEditServerBinding
import com.tilal6991.channels.databinding.ConfigurationEditUserBinding
import com.tilal6991.channels.db.connectionDb
import com.tilal6991.channels.ui.ConfigurationAuthPresenter
import com.tilal6991.channels.ui.ConfigurationServerPresenter
import com.tilal6991.channels.ui.ConfigurationUserPresenter
import com.tilal6991.channels.ui.Presenter
import com.tilal6991.channels.ui.helper.CommitingBooleanWatcher
import com.tilal6991.channels.ui.helper.CommitingIntWatcher
import com.tilal6991.channels.ui.helper.CommitingNullableWatcher
import com.tilal6991.channels.ui.helper.CommitingWatcher
import com.tilal6991.channels.util.addAll
import org.parceler.Parcels
import rx.schedulers.Schedulers
import java.util.*

class ConfigurationEditActivity : AppCompatActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val tabs: TabLayout by bindView(R.id.tabs)
    private val pager: ViewPager by bindView(R.id.view_pager)

    private var configuration: ChannelsConfiguration? = null

    private lateinit var server: ConfigurationServerPresenter
    private lateinit var user: ConfigurationUserPresenter
    private lateinit var auth: ConfigurationAuthPresenter
    private val presenters: MutableList<Presenter> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.configuration_edit)
        setSupportActionBar(toolbar)

        supportActionBar!!.title = getString(R.string.configuration_add_title)

        val extras = intent.extras
        configuration = if (extras == null) null else Parcels.unwrap<ChannelsConfiguration>(extras.getParcelable(CONFIGURATION))

        val serverBinding = ConfigurationEditServerBinding.inflate(layoutInflater, pager, false)
        server = ConfigurationServerPresenter(this, serverBinding, configuration)

        val userBinding = ConfigurationEditUserBinding.inflate(layoutInflater, pager, false)
        user = ConfigurationUserPresenter(this, userBinding, configuration)

        val authBinding = ConfigurationEditAuthBinding.inflate(layoutInflater, pager, false)
        auth = ConfigurationAuthPresenter(this, authBinding, configuration)

        presenters.addAll(server, user, auth)
        presenters.forEach { it.setup(savedInstanceState?.getBundle(it.id)) }

        pager.adapter = Adapter(presenters)
        pager.offscreenPageLimit = 3
        tabs.setupWithViewPager(pager)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        presenters.forEach { it.restoreState(savedInstanceState.getBundle(it.id)) }
    }

    override fun onStart() {
        super.onStart()
        presenters.forEach { it.bind() }
    }

    override fun onStop() {
        super.onStop()
        presenters.forEach { it.unbind() }
    }

    override fun onBackPressed() {
        val newConfiguration = convertToConfiguration(
                server.configuration, user.configuration, auth.configuration)
        if (newConfiguration == null) {
            AlertDialog.Builder(this)
                    .setMessage(R.string.configuration_not_saved)
                    .setPositiveButton(R.string.yes) { i, j ->
                        i.dismiss()
                        super.onBackPressed()
                    }
                    .setNegativeButton(R.string.no) { i, j -> i.cancel() }
                    .show()
        } else {
            val completeable = if (configuration == null) {
                connectionDb.insert(newConfiguration)
            } else {
                connectionDb.update(configuration!!.id, newConfiguration)
            }
            completeable.subscribeOn(Schedulers.io()).subscribe()

            val result = Intent()
            result.putExtra(RESULT_OLD_ID, configuration?.id ?: -1)
            result.putExtra(RESULT_CONFIGURATION, Parcels.wrap(configuration))
            setResult(RESULT_OK, result)

            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenters.forEach { outState.putBundle(it.id, it.saveState()) }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenters.forEach { it.teardown() }
    }

    private fun convertToConfiguration(server: ConfigurationServerPresenter.Configuration,
                                       user: ConfigurationUserPresenter.Configuration,
                                       auth: ConfigurationAuthPresenter.Configuration): ChannelsConfiguration? {
        if (!server.isValid() || !user.isValid() || !auth.isValid()) {
            return null
        }

        val serverConfiguration = ServerConfiguration(
                server.hostname.get().toString(), server.port.get(),
                server.ssl.get(),
                auth.serverUsername.get().toString(), auth.serverPassword.get().toString())
        val userConfiguration = UserConfiguration(
                listOf(user.nick.get().toString()), user.autoChangeNick.get(),
                user.realName.get().toString(),
                ConfigurationAuthPresenter.indexToType(auth.authIndex),
                auth.username.get().toString(), auth.password.get().toString())
        return ChannelsConfiguration(-1, server.name.get().toString(), serverConfiguration, userConfiguration)
    }

    inner class Adapter(private val presenters: MutableList<Presenter>) : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Presenter? {
            val presenter = presenters[position]
            container.addView(presenter.binding.root)
            return presenter
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        }

        override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
            return (`object` as Presenter?)?.binding?.root === view
        }

        override fun getCount(): Int {
            return presenters.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return presenters[position].title
        }
    }

    abstract class Presenter : com.tilal6991.channels.ui.Presenter {
        abstract val binding: ViewDataBinding
        abstract val title: String

        protected final fun addIntBackendListeners(vararg pairs: Pair<EditText, ObservableInt>) {
            for ((f, s) in pairs) {
                f.addTextChangedListener(CommitingIntWatcher(s))
            }
        }

        protected final fun addBooleanBackendListeners(vararg pairs: Pair<CheckBox, ObservableBoolean>) {
            for ((f, s) in pairs) {
                f.setOnCheckedChangeListener(CommitingBooleanWatcher(s))
            }
        }

        protected final fun addNullableBackendListeners(vararg pairs: Pair<EditText, ObservableField<CharSequence?>>) {
            for ((f, s) in pairs) {
                f.addTextChangedListener(CommitingNullableWatcher(s))
            }
        }

        protected final fun addNonNullBackendListeners(vararg pairs: Pair<EditText, ObservableField<CharSequence>>) {
            for ((f, s) in pairs) {
                f.addTextChangedListener(CommitingWatcher(s))
            }
        }
    }

    companion object {
        const val CONFIGURATION = "configuration"
        const val RESULT_OLD_ID = "old_id"
        const val RESULT_CONFIGURATION = "configuration"
    }
}
