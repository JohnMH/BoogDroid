package ws.lamm.bugdroid.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import me.johnmh.boogdroid.R
import ws.lamm.bugdroid.general.Bug
import ws.lamm.bugdroid.general.Server
import ws.lamm.util.ImageLoader
import ws.lamm.util.md5


class BugAttributesFragment : Fragment() {
    private var bug: Bug? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bug_attributes, null, false)

        val activity = activity as AppCompatActivity?

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
            activity!!.onBackPressed()
            return view
        }

        bug = Server.servers[serverPos].getBugFromId(bugId)

        //updateView(view);

        return view
    }

    fun updateView(view: View) {
        val reporter = bug!!.reporter
        if (!TextUtils.isEmpty(reporter.avatarUrl)) {
            ImageLoader.loadImage(reporter.avatarUrl, view.findViewById<View>(R.id.reporter_img) as ImageView)
        } else {
            ImageLoader.loadImage("http://www.gravatar.com/avatar/" + reporter.email.md5(), view.findViewById<View>(R.id.reporter_img) as ImageView)
        }
        val assignee = bug!!.assignee
        if (assignee != null) {
            if (!TextUtils.isEmpty(assignee.avatarUrl)) {
                ImageLoader.loadImage(assignee.avatarUrl, view.findViewById<View>(R.id.assignee_img) as ImageView)
            } else {
                ImageLoader.loadImage("http://www.gravatar.com/avatar/" + assignee.email.md5(), view.findViewById<View>(R.id.assignee_img) as ImageView)
            }
        }

        val textCreationDate = view.findViewById<View>(R.id.creation_date) as TextView
        textCreationDate.text = bug!!.creationDate

        val textSummary = view.findViewById<View>(R.id.summary) as TextView
        textSummary.text = bug!!.summary

        val textReporter = view.findViewById<View>(R.id.reporter) as TextView
        textReporter.text = bug!!.reporter.name
        if (assignee != null) {
            val textAssignee = view.findViewById<View>(R.id.assignee) as TextView
            textAssignee.text = assignee.name
        }

        val textPriority = view.findViewById<View>(R.id.priority) as TextView
        textPriority.text = bug!!.priority
        val textStatus = view.findViewById<View>(R.id.status) as TextView
        textStatus.text = bug!!.status

        val textDescription = view.findViewById<View>(R.id.description) as TextView
        textDescription.text = bug!!.description
    }
}
