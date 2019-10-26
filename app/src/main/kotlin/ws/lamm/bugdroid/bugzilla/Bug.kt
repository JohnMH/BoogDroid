package ws.lamm.bugdroid.bugzilla

import android.support.v7.app.AppCompatActivity
import android.widget.BaseAdapter
import org.json.JSONObject
import ws.lamm.bugdroid.ui.BugInfoFragment
import ws.lamm.util.Util
import ws.lamm.util.Util.TaskListener
import java.text.SimpleDateFormat
import java.util.*

class Bug {

    var id: Int = 0
    var isOpen: Boolean = false

    lateinit var summary: String
    var priority: String? = null
    lateinit var status: String
    var description: String? = null
        protected set
    lateinit var creationDate: String

    lateinit var reporter: User
    var assignee: User? = null

    val comments: MutableList<Comment> = ArrayList()

    lateinit var product: Product

    private var adapter: BaseAdapter? = null
    private var activity: AppCompatActivity? = null
    private var fragment: BugInfoFragment? = null
    var resolution: String? = null

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

    override fun toString(): String {
        return summary
    }

    fun loadComments() {
        val task = BugzillaTask(product.server, "Bug.comments", "'ids':[$id]", object : TaskListener {
            var newList: MutableList<Comment> = ArrayList()

            override fun doInBackground(response: Any?) {
                if (product.server.isUseJson!!) {
                    doJsonParse(response)
                } else {
                    doXmlParse(response)
                }
            }

            private fun doJsonParse(response: Any?) {
                try {
                    val `object` = JSONObject(response!!.toString())
                    val comments = `object`.getJSONObject("result").getJSONObject("bugs").getJSONObject(Integer.toString(id)).getJSONArray("comments")
                    val size = comments.length()
                    for (i in 0 until size) {
                        val json = comments.getJSONObject(i)
                        if (i == 0) {
                            description = json.getString("text")
                        } else {
                            val comment = Comment()
                            try {
                                comment.id = json.getInt("id")
                                comment.text = json.getString("text")

                                if (json.has("creator")) {
                                    comment.author = User(json.getString("creator"))
                                } else {
                                    comment.author = User(json.getString("author"))
                                }

                                if (json.has("creation_time")) {
                                    comment.date = Util.formatDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("creation_time"))
                                } else {
                                    comment.date = Util.formatDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("time"))
                                }

                                if (json.has("count")) {
                                    comment.number = json.getInt("count")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            private fun doXmlParse(response: Any?) {
                try {
                    val bugs = (response as HashMap<String, Any>)["bugs"]
                    val objects = (bugs as HashMap<String, Any>)[Integer.toString(id)]
                    val comments = (objects as HashMap<String, Array<Any>>)["comments"]
                    val size = comments!!.size
                    for (i in 0 until size) {
                        val commentMap = comments[i] as HashMap<String, Any>
                        if (i == 0) {
                            description = commentMap["text"] as String?
                        } else {
                            val comment = Comment()
                            comment.bug = this@Bug
                            try {
                                comment.id = (commentMap["id"] as Int?)!!
                                comment.text = commentMap["text"] as String

                                if (commentMap["creator"] != null) {
                                    comment.author = User(commentMap["creator"] as String)
                                } else {
                                    comment.author = User(commentMap["author"] as String)
                                }

                                if (commentMap["creation_time"] != null) {
                                    comment.date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(commentMap["creation_time"])
                                } else {
                                    comment.date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(commentMap["time"])
                                }

                                if (commentMap["count"] != null) {
                                    comment.number = (commentMap["count"] as Int?)!!
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            newList.add(comment)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onPostExecute(response: Any?) {
                comments.clear()
                comments.addAll(newList)
                commentsListUpdated()
            }
        })
        task.execute()
    }
}
