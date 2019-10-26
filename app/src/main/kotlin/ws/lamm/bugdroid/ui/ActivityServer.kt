package ws.lamm.bugdroid.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ProgressBar
import ws.lamm.bugdroid.R
import ws.lamm.bugdroid.bugzilla.Server
import ws.lamm.bugdroid.ui.fragments.TabFragment

class ActivityServer : AppCompatActivity(), ProductListFragment.OnProductSelectedListener, BugListFragment.OnBugSelectedListener, LeftMenuFragment.OnServerSelectedListener {
    private var serverPos: Int = 0
    private var productId: Int = 0

    private var drawerLayout: DrawerLayout? = null
    private var progressBar: ProgressBar? = null

    private val statusBarHeight: Int
        get() {
            var result = 0
            val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resId > 0) {
                result = resources.getDimensionPixelSize(resId)
            }
            return result
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)

        drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        progressBar = findViewById<View>(R.id.progressbar) as ProgressBar

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_navigation_drawer)
        toolbar.setNavigationOnClickListener { drawerLayout!!.openDrawer(GravityCompat.START) }

        fakeToolHeight = toolbar.height

        toolbar.setPadding(0, statusBarHeight, 0, 0)
    }

    override fun setSupportProgressBarIndeterminateVisibility(visible: Boolean) {
        progressBar!!.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        setServer(intent.getIntExtra("server_position", -1))
    }

    override fun onNewIntent(intent: Intent) {
        setServer(intent.getIntExtra("server_position", -1))
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerVisible(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onServerSelected(position: Int) {
        drawerLayout!!.closeDrawer(GravityCompat.START)
        if (position == Server.servers.size) {
            openServerManager()
        } else {
            supportFragmentManager.popBackStack()
            supportFragmentManager.popBackStack()
            setServer(position)
        }
    }

    override fun onProductSelected(productId: Int) {
        this.productId = productId
        val bugsFragment = BugListFragment()
        val arguments = Bundle()
        arguments.putInt("server_position", serverPos)
        arguments.putInt("product_id", productId)
        bugsFragment.arguments = arguments
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, bugsFragment).addToBackStack(null).commit()
    }

    override fun onBugSelected(bugId: Int) {
        //        Fragment bugInfoFragment = new BugInfoFragment();
        val bugTabFragment = TabFragment()
        val arguments = Bundle()
        arguments.putInt("server_position", serverPos)
        arguments.putInt("product_id", productId)
        arguments.putInt("bug_id", bugId)
        bugTabFragment.arguments = arguments
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, bugTabFragment).addToBackStack(null).commit()
    }

    private fun setServer(pos: Int) {
        serverPos = pos

        if (serverPos == -1) {
            if (Server.servers.isEmpty()) {
                openServerManager()
                return
            } else {
                serverPos = 0
            }
        }

        val productsFragment = ProductListFragment()
        val arguments = Bundle()
        arguments.putInt("server_position", serverPos)
        productsFragment.arguments = arguments
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, productsFragment).commit()
    }

    private fun openServerManager() {
        startActivity(Intent(this, ActivityServerManager::class.java))
    }

    companion object {

        var fakeToolHeight = 0
            private set
    }
}
