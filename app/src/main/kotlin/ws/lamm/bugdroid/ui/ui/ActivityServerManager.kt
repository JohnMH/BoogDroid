package ws.lamm.bugdroid.ui.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import me.johnmh.boogdroid.R
import ws.lamm.bugdroid.ui.ActivityRegister

class ActivityServerManager : AppCompatActivity(), ServerListFragment.OnServerSelectedListener {

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
        setContentView(R.layout.activity_server_manager)
        val toolbar = findViewById<View>(R.id.my_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.support.v7.appcompat.R.attr.actionBarSize))
        val mActionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()

        val listFrag = findViewById<View>(R.id.server_manager_frag) as RelativeLayout
        listFrag.setPadding(0, statusBarHeight + mActionBarSize, 0, 0)
        title = "Servers"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_server_manager, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_new -> {
                startActivity(Intent(this, ActivityRegister::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onServerSelected(position: Int) {
        val intent = Intent(this, ActivityRegister::class.java)
        intent.putExtra("server_position", position)
        startActivity(intent)
    }
}
