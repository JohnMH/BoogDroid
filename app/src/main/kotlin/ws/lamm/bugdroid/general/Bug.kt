package ws.lamm.bugdroid.general

import android.support.v7.app.AppCompatActivity
import android.widget.BaseAdapter

import ws.lamm.bugdroid.ui.BugInfoFragment

abstract class Bug {
    var id: Int = 0
    var isOpen: Boolean = false

    lateinit var summary: String
    var priority: String? = null
    lateinit var status: String
    var description: String? = null
        protected set
    lateinit var creationDate: String

    lateinit var reporter: User
        protected set
    var assignee: User? = null
        protected set

    val comments: MutableList<Comment> = ArrayList()

    lateinit var product: Product
        protected set

    private var adapter: BaseAdapter? = null
    private var activity: AppCompatActivity? = null
    private var fragment: BugInfoFragment? = null
    var resolution: String? = null

    abstract fun loadComments()

    fun setAdapterComment(adapter: BaseAdapter, activity: AppCompatActivity, fragment: BugInfoFragment) {
        this.adapter = adapter
        this.activity = activity
        this.fragment = fragment

        activity.setSupportProgressBarIndeterminateVisibility(true)
        loadComments()
    }

    protected fun commentsListUpdated() {
        adapter!!.notifyDataSetChanged()
        activity!!.setSupportProgressBarIndeterminateVisibility(false)
        fragment!!.updateView()
    }

    fun setReporter(reporter: ws.lamm.bugdroid.bugzilla.User) {
        this.reporter = reporter
    }

    fun setAssignee(assignee: ws.lamm.bugdroid.bugzilla.User) {
        this.assignee = assignee
    }

    fun setProduct(product: ws.lamm.bugdroid.bugzilla.Product) {
        this.product = product
    }

    override fun toString(): String {
        return summary
    }
}
