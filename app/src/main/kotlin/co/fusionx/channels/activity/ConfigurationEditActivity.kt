package co.fusionx.channels.activity

import android.app.Activity
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import butterknife.bindView
import co.fusionx.channels.R
import co.fusionx.channels.configuration.Configuration
import co.fusionx.channels.databinding.ConfigurationEditAuthBinding
import co.fusionx.channels.databinding.ConfigurationEditServerBinding
import co.fusionx.channels.databinding.ConfigurationEditUserBinding
import co.fusionx.channels.presenter.ConfigurationAuthPresenter
import co.fusionx.channels.presenter.ConfigurationServerPresenter
import co.fusionx.channels.presenter.ConfigurationUserPresenter
import org.parceler.Parcels

class ConfigurationEditActivity : AppCompatActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val tabs: TabLayout by bindView(R.id.tabs)
    private val pager: ViewPager by bindView(R.id.view_pager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.configuration_edit)
        setSupportActionBar(toolbar)

        supportActionBar!!.title = getString(R.string.configuration_add_title)

        val extras = intent.extras
        val configuration = if (extras == null) null else Parcels.unwrap<Configuration>(extras.getParcelable(CONFIGURATION))

        val serverBinding = ConfigurationEditServerBinding.inflate(layoutInflater, pager, false)
        val server = ConfigurationServerPresenter(this, serverBinding, configuration?.connection)
        server.setup(savedInstanceState)

        val userBinding = ConfigurationEditUserBinding.inflate(layoutInflater, pager, false)
        val user = ConfigurationUserPresenter(this, userBinding)
        user.setup(savedInstanceState)

        val authBinding = ConfigurationEditAuthBinding.inflate(layoutInflater, pager, false)
        val auth = ConfigurationAuthPresenter(this, authBinding)
        auth.setup(savedInstanceState)

        pager.adapter = Adapter(this, server, user, auth)
        pager.offscreenPageLimit = 3
        tabs.setupWithViewPager(pager)
    }

    inner class Adapter(private val activity: Activity,
                        private val server: ConfigurationServerPresenter,
                        private val user: ConfigurationUserPresenter,
                        private val auth: ConfigurationAuthPresenter) : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Presenter? {
            val presenter = when (position) {
                0 -> server
                1 -> user
                2 -> auth
                else -> null
            }
            if (presenter != null) {
                container.addView(presenter.binding.root)
            }
            return presenter
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        }

        override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
            return (`object` as Presenter?)?.binding?.root === view
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> activity.getString(R.string.connection_settings)
                1 -> activity.getString(R.string.user_settings)
                2 -> activity.getString(R.string.auth_settings)
                else -> null
            }
        }
    }

    interface Presenter : co.fusionx.channels.presenter.Presenter {
        val binding: ViewDataBinding
    }

    companion object {
        const val CONFIGURATION = "configuration"
    }
}
