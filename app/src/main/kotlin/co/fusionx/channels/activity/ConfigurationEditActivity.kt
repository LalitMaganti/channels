package co.fusionx.channels.activity

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
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
                0 -> ConfigurationServerFragment()
                1 -> ConfigurationUserFragment()
                2 -> ConfigurationAuthFragment()
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
}
