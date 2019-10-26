package ws.lamm.bugdroid.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ws.lamm.bugdroid.R
import ws.lamm.bugdroid.bugzilla.BugzillaTask
import ws.lamm.bugdroid.bugzilla.ChangeStatusInfo
import ws.lamm.bugdroid.bugzilla.Bug
import ws.lamm.bugdroid.bugzilla.Server
import ws.lamm.util.Util


class BugStatusFragment : Fragment() {
    private var bug: Bug? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bug_status, null, false)

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

        val spinnerStatus = setupStatusSpinner(view)

        val spinnerResolution = setupSpinnerResolution(view)

        val saveButton = view.findViewById<View>(R.id.save) as Button
        saveButton.setOnClickListener(View.OnClickListener {
            val changeStatusInfo = spinnerStatus.selectedItem as ChangeStatusInfo
            val statusInfo = bug!!.product.server.findStatusInfo(changeStatusInfo.name)
            val resolution = spinnerResolution.selectedItem as String
            if (!statusInfo!!.isOpen && "" == resolution) {
                Toast.makeText(activity, R.string.invalid_change_status, Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val statusChangeComment = view.findViewById<View>(R.id.statusChangeComment) as EditText
            val comment = statusChangeComment.text.toString()
            if (changeStatusInfo.isCommentRequired!! && comment.trim { it <= ' ' }.isEmpty()) {
                Toast.makeText(activity, R.string.invalid_change_status_comment_required, Toast.LENGTH_SHORT).show()
            }
            //TODO: Accept object as params
            BugzillaTask(bug!!.product.server, "Bug.update", "ids:[" + bug!!.id + "], status:'"
                    + changeStatusInfo + "', resolution:'" + resolution + "'", object : Util.TaskListener {
                override fun doInBackground(response: Any?) {
                    //TODO: Check if everything was correct
                }

                override fun onPostExecute(response: Any?) {

                }
            }).execute()
        }
        )
        return view
    }

    private fun setupSpinnerResolution(view: View): Spinner {
        val spinnerResolution = view.findViewById<View>(R.id.resolution) as Spinner
        val resolutionValues = bug!!.product.server.bugResolutionChanges
        val adapter = ArrayAdapter(activity!!.baseContext, android.R.layout.simple_spinner_item, resolutionValues!!)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerResolution.adapter = adapter
        spinnerResolution.setSelection(adapter.getPosition(bug!!.resolution))
        return spinnerResolution
    }

    private fun setupStatusSpinner(view: View): Spinner {
        val spinnerStatus = view.findViewById<View>(R.id.status) as Spinner
        val statusName = bug!!.status
        val server = bug!!.product.server
        val statusInfo = server.statusChanges!![statusName]
        val adapter = ArrayAdapter(activity!!.baseContext, android.R.layout.simple_spinner_item, statusInfo!!.changeList!!)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter
        spinnerStatus.setSelection(adapter.getPosition(server.findChangeStatusInfo(statusName)))
        return spinnerStatus
    }
}
