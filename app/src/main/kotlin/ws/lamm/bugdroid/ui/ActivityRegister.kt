package ws.lamm.bugdroid.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import me.johnmh.boogdroid.R
import ws.lamm.bugdroid.general.Server

class ActivityRegister : AppCompatActivity() {
    private var server: Server? = null

    private var nameView: EditText? = null
    private var urlView: EditText? = null
    private var userView: EditText? = null
    private var passwordView: EditText? = null
    private var useJsonView: CheckBox? = null

    val statusBarHeight: Int
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
        setContentView(R.layout.activity_register)
        val toolbar = findViewById<View>(R.id.my_toolbar) as Toolbar
        //        setSupportActionBar(toolbar);
        //        getSupportActionBar().setDisplayShowHomeEnabled(false);

        val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.support.v7.appcompat.R.attr.actionBarSize))
        val mActionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()

        val listFrag = findViewById<View>(R.id.server_register_view) as FrameLayout
        listFrag.setPadding(0, statusBarHeight + mActionBarSize, 0, 0)

        nameView = findViewById<View>(R.id.name) as EditText
        urlView = findViewById<View>(R.id.url) as EditText
        userView = findViewById<View>(R.id.user) as EditText
        passwordView = findViewById<View>(R.id.password) as EditText
        useJsonView = findViewById<View>(R.id.useJson) as CheckBox

        val serverPos = intent.getIntExtra("server_position", -1)
        if (serverPos == -1) {
            setTitle(R.string.add_server)
        } else {
            setTitle(R.string.edit_server)
            server = Server.servers[serverPos]
            nameView!!.setText(server!!.name)
            urlView!!.setText(server!!.url)
            userView!!.setText(server!!.user)
            passwordView!!.setText(server!!.password)
            useJsonView!!.isActivated = server!!.isUseJson!!
        }
    }

    fun onAccept(view: View) {
        val name = nameView!!.text.toString()
        val url = urlView!!.text.toString()
        val user = userView!!.text.toString()
        val password = passwordView!!.text.toString()
        val useJsonImplementation = useJsonView!!.isActivated

        var error = false

        if (TextUtils.isEmpty(name)) {
            nameView!!.error = getString(R.string.name_cant_be_empty)
            error = true
        }

        for (s in Server.servers) {
            if (s.name == name && server !== s) {
                nameView!!.error = getString(R.string.server_with_that_name_exists)
                error = true
            }
        }

        if (TextUtils.isEmpty(url)) {
            urlView!!.error = getString(R.string.name_cant_be_empty)
            error = true
        }

        if (!error) {
            if (server == null) {
                registerServer(name, url, user, password, useJsonImplementation)
            } else {
                editServer(name, url, user, password, useJsonImplementation)
            }
        }
    }

    private fun editServer(name: String, url: String, user: String, password: String, useJsonImplementation: Boolean?) {
        server!!.name = name
        server!!.url = url
        server!!.setUser(user, password)
        server!!.isUseJson = useJsonImplementation
        server!!.save()
        finish()
    }

    private fun registerServer(name: String, url: String, user: String, password: String, jsonImplementation: Boolean) {
        val newServer = ws.lamm.bugdroid.bugzilla.Server(name, url, jsonImplementation)
        newServer.setUser(user, password)
        newServer.save()
        Server.servers.add(newServer)

        finish()
    }
}
