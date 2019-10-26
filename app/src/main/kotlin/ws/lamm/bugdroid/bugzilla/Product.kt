package ws.lamm.bugdroid.bugzilla

import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import org.json.JSONObject
import ws.lamm.bugdroid.bugzilla.Server
import ws.lamm.bugdroid.ui.AdapterBug
import ws.lamm.util.Util
import ws.lamm.util.Util.TaskListener
import java.text.SimpleDateFormat
import java.util.*

class Product {

    lateinit var server: Server

    var id: Int = 0
    var name: String? = null
    var description: String? = null

    val bugs: MutableList<Bug> = ArrayList()

    private var adapter: AdapterBug? = null
    private var activity: AppCompatActivity? = null

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


    fun loadBugs() {
        val task = BugzillaTask(server, "Bug.search", "'product':'$name', 'resolution':'', 'limit':0, 'include_fields':['id', 'summary', 'priority', 'status', 'creator', 'assigned_to', 'resolution', 'creation_time', 'resolution']", object : TaskListener {
            var newList: MutableList<Bug> = ArrayList()

            override fun doInBackground(response: Any?) {
                if (server.isUseJson!!) {
                    doJsonParse(response)
                } else {
                    doXmlParse(response)
                }
            }

            private fun doXmlParse(response: Any?) {
                try {
                    val bugs = listOf(*(response as HashMap<String, Array<Any>>)["bugs"]!!)
                    val size = bugs.size
                    for (i in 0 until size) {
                        val bug = Bug()
                        bug.product = this@Product
                        val bugMap = bugs[i] as HashMap<String, Any>
                        try {
                            bug.id = Integer.parseInt(bugMap["id"]!!.toString())
                            bug.summary = bugMap["summary"]!!.toString()
                            bug.creationDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(bugMap["creation_time"])
                            bug.priority = bugMap["priority"]!!.toString()
                            bug.status = bugMap["status"]!!.toString()
                            bug.resolution = bugMap["resolution"]!!.toString()
                            bug.reporter = User(bugMap["creator"]!!.toString())
                            bug.assignee = User(bugMap["assigned_to"]!!.toString())
                            bug.isOpen = TextUtils.isEmpty(bugMap["resolution"]!!.toString())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        newList.add(bug)
                    }
                    newList.reverse()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            private fun doJsonParse(response: Any?) {
                try {
                    val `object` = JSONObject(response!!.toString())
                    val bugs = `object`.getJSONObject("result").getJSONArray("bugs")
                    val size = bugs.length()
                    for (i in 0 until size) {
                        val bug = Bug()
                        val json = bugs.getJSONObject(i)
                        bug.product = this@Product
                        try {
                            bug.id = json.getInt("id")
                            bug.summary = json.getString("summary")
                            bug.creationDate = Util.formatDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("creation_time"))
                            bug.priority = json.getString("priority")
                            bug.status = json.getString("status")
                            bug.resolution = json.getString("resolution")
                            bug.reporter =User(json.getString("creator"))
                            bug.assignee = User(json.getString("assigned_to"))
                            bug.isOpen = TextUtils.isEmpty(json.getString("resolution"))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        newList.add(bug)
                    }
                    Collections.reverse(newList)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onPostExecute(response: Any?) {
                clearBugs()
                addBugs(newList)
                bugsListUpdated()
            }
        })
        task.execute()
    }
}
