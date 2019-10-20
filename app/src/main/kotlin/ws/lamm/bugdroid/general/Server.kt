package ws.lamm.bugdroid.general

import android.support.v7.app.AppCompatActivity
import android.text.TextUtils

import java.util.ArrayList
import java.util.Arrays

import me.johnmh.boogdroid.R
import ws.lamm.bugdroid.bugzilla.ChangeStatusInfo
import ws.lamm.bugdroid.ui.ui.AdapterProduct

abstract class Server {

    val products: MutableList<Product> = ArrayList()
    val type: String?
    private var jsonImplementation: Boolean = false
    var name: String?
    var url: String? = null
    var user: String? = null
        protected set
    var password: String? = null
        protected set

    private var adapter: AdapterProduct? = null
    private var activity: AppCompatActivity? = null

    private var databaseServer: ws.lamm.bugdroid.db.Server? = null
    var statusChanges: BugStatusChanges? = null
    var resolutionValues: BugResolutionChanges? = null

    var isUseJson: Boolean?
        get() = jsonImplementation
        set(useJson) {
            this.jsonImplementation = useJson!!
        }

    constructor(name: String, url: String, type: String, jsonImplementation: Boolean) {
        this.type = type
        this.name = name
        this.url = url
        user = ""
        password = ""
        this.jsonImplementation = jsonImplementation
    }

    constructor(server: ws.lamm.bugdroid.db.Server) {
        databaseServer = server
        type = server.type
        name = server.name
        url = server.url
        user = server.user
        password = server.password
        jsonImplementation = if (server.json == null) false else server.json
    }

    fun setUser(user: String, password: String) {
        this.user = user
        this.password = password
    }

    fun setAdapterProduct(adapter: AdapterProduct, activity: AppCompatActivity) {
        this.adapter = adapter
        this.activity = activity

        activity.setSupportProgressBarIndeterminateVisibility(true)
        loadProducts()
    }

    fun getProductFromId(productId: Int): Product? {
        for (p in products) {
            if (p.id == productId) {
                return p
            }
        }

        return null
    }

    fun getBugFromId(bugId: Int): Bug? {
        for (p in products) {
            for (b in p.bugs) {
                if (b.id == bugId) {
                    return b
                }
            }
        }

        return null
    }

    fun save() {
        if (databaseServer == null) {
            databaseServer = ws.lamm.bugdroid.db.Server()
        }
        databaseServer!!.type = type
        databaseServer!!.name = name
        databaseServer!!.url = url
        databaseServer!!.user = user
        databaseServer!!.password = password
        databaseServer!!.save()
    }

    fun delete() {
        if (databaseServer != null) {
            databaseServer!!.delete()
        }
    }

    override fun toString(): String {
        return name
    }

    fun hasUser(): Boolean {
        return !TextUtils.isEmpty(user)
    }

    protected abstract fun loadProducts()

    protected fun productsListUpdated() {
        adapter!!.notifyDataSetChanged()
        activity!!.setSupportProgressBarIndeterminateVisibility(false)
    }

    fun findChangeStatusInfo(statusName: String): ChangeStatusInfo? {
        val changeList = statusChanges!![statusName]!!.changeList
        for (changeStatusInfo in changeList) {
            if (changeStatusInfo.name == statusName) {
                return changeStatusInfo
            }
        }
        return null
    }

    fun findStatusInfo(statusName: String): StatusInfo? {
        return statusChanges!![statusName]
    }

    companion object {
        const val BUGZILLA = "Bugzilla"
        private const val BUGZILLA_ICON = R.drawable.server_icon_bugzilla

        var servers: MutableList<Server> = ArrayList()
        var typeName = listOf(BUGZILLA)
        var typeIcon = listOf(BUGZILLA_ICON)
    }
}
