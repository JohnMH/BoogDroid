package ws.lamm.bugdroid.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import me.johnmh.boogdroid.R
import ws.lamm.bugdroid.general.Bug
import ws.lamm.bugdroid.general.Server
import org.json.JSONObject
import ws.lamm.bugdroid.bugzilla.BugzillaTask
import ws.lamm.util.ImageLoader
import ws.lamm.util.Util
import ws.lamm.util.md5


class BugInfoFragment : ListFragment() {
    private var bug: Bug? = null

    private var mainView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_bug, container, false)
        val activity = activity as AppCompatActivity?

        activity!!.setSupportProgressBarIndeterminateVisibility(true)

        val arguments = arguments
        val serverPos: Int
        val productId: Int
        val bugId: Int
        if (arguments != null) {
            serverPos = arguments.getInt("server_position", -1)
            productId = arguments.getInt("product_id", -1)
            bugId = arguments.getInt("bug_id", -1)
        } else {
            serverPos = -1
            productId = -1
            bugId = -1
        }

        if (serverPos == -1 || productId == -1 || bugId == -1) {
            Toast.makeText(activity, R.string.invalid_bug, Toast.LENGTH_SHORT).show()
            activity.onBackPressed()
            return view
        }

        bug = Server.servers[serverPos].getBugFromId(bugId)

        mainView = inflater.inflate(R.layout.bug_info, null, false)
        updateView()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity?

        val listView = listView
        listView.addHeaderView(mainView)

        val adapter = AdapterComment(activity!!, bug!!.comments)


        val editCommentFilter = mainView!!.findViewById<View>(R.id.editCommentFilter) as EditText
        editCommentFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        val footer = (getActivity()!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.footer_comment, null, false)

        val editComment = footer.findViewById<View>(R.id.editComment) as EditText
        editComment.setText("")
        val addComment = footer.findViewById<View>(R.id.addComment) as Button
        addComment.setOnClickListener {
            val comment = JSONObject.quote(editComment.text.toString())
            val server = bug!!.product.server
            val task = BugzillaTask(server, "Bug.add_comment", "'id':" + bug!!.id + ", 'comment': " + comment, object : Util.TaskListener {

                override fun doInBackground(response: Any?) {
                    if (server.isUseJson!!) {
                        doJsonParse(response)
                    } else {
                        doXmlParse(response)
                    }
                }

                private fun doJsonParse(response: Any?) {
                    //TODO: It returns the new comment id. So you could only reload that one
                    try {
                        println(response)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                private fun doXmlParse(response: Any?) {
                    //TODO: It returns the new comment id. So you could only reload that one
                    try {
                        println(response)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onPostExecute(response: Any?) {
                    updateView()
                    bug!!.loadComments()
                }
            })
            task.execute()
        }
        listView.addFooterView(footer)

        listAdapter = adapter
        bug!!.setAdapterComment(adapter, activity, this)
    }

    fun updateView() {
        val reporter = bug!!.reporter
        if (!TextUtils.isEmpty(reporter.avatarUrl)) {
            ImageLoader.loadImage(reporter.avatarUrl, mainView!!.findViewById<View>(R.id.reporter_img) as ImageView)
        } else {
            ImageLoader.loadImage("http://www.gravatar.com/avatar/" + reporter.email.md5(), mainView!!.findViewById<View>(R.id.reporter_img) as ImageView)
        }
        val assignee = bug!!.assignee
        if (assignee != null) {
            if (!TextUtils.isEmpty(assignee.avatarUrl)) {
                ImageLoader.loadImage(assignee.avatarUrl, mainView!!.findViewById<View>(R.id.assignee_img) as ImageView)
            } else {
                ImageLoader.loadImage("http://www.gravatar.com/avatar/" + assignee.email.md5(), mainView!!.findViewById<View>(R.id.assignee_img) as ImageView)
            }
        }

        val textCreationDate = mainView!!.findViewById<View>(R.id.creation_date) as TextView
        textCreationDate.text = bug!!.creationDate

        val textSummary = mainView!!.findViewById<View>(R.id.summary) as TextView
        textSummary.text = bug!!.summary

        val textReporter = mainView!!.findViewById<View>(R.id.reporter) as TextView
        textReporter.text = bug!!.reporter.name
        if (assignee != null) {
            val textAssignee = mainView!!.findViewById<View>(R.id.assignee) as TextView
            textAssignee.text = assignee.name
        }

        val textPriority = mainView!!.findViewById<View>(R.id.priority) as TextView
        textPriority.text = bug!!.priority
        val textStatus = mainView!!.findViewById<View>(R.id.status) as TextView
        textStatus.text = bug!!.status
        val textResolution = mainView!!.findViewById<View>(R.id.resolution) as TextView
        textResolution.text = bug!!.resolution

        val textDescription = mainView!!.findViewById<View>(R.id.description) as TextView
        textDescription.text = bug!!.description
    }
}
