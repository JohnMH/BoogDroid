package ws.lamm.bugdroid.general

import android.support.v7.app.AppCompatActivity
import ws.lamm.bugdroid.ui.ui.AdapterBug

abstract class Product {
    var server: Server

    var id: Int = 0
    var name: String? = null
    var description: String? = null

    val bugs: MutableList<Bug> = ArrayList()

    private var adapter: AdapterBug? = null
    private var activity: AppCompatActivity? = null

    protected abstract fun loadBugs()

    fun setAdapterBug(adapter: AdapterBug, activity: AppCompatActivity) {
        this.adapter = adapter
        this.activity = activity

        activity.setSupportProgressBarIndeterminateVisibility(true)
        loadBugs()
    }

    protected fun bugsListUpdated() {
        adapter!!.notifyDataSetChanged()
        activity!!.setSupportProgressBarIndeterminateVisibility(false)
    }

    fun addBugs(newBugs: List<Bug>) {
        bugs.addAll(newBugs)
    }

    fun clearBugs() {
        bugs.clear()
    }
}
