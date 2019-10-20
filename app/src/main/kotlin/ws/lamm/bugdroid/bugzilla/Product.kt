package ws.lamm.bugdroid.bugzilla

import android.text.TextUtils
import org.json.JSONObject
import ws.lamm.bugdroid.general.Product
import ws.lamm.util.Util
import ws.lamm.util.Util.TaskListener
import java.text.SimpleDateFormat
import java.util.*

class Product : Product() {

    override fun loadBugs() {
        val task = BugzillaTask(server, "Bug.search", "'product':'$name', 'resolution':'', 'limit':0, 'include_fields':['id', 'summary', 'priority', 'status', 'creator', 'assigned_to', 'resolution', 'creation_time', 'resolution']", object : TaskListener {
            var newList: MutableList<ws.lamm.bugdroid.general.Bug> = ArrayList()

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
                        bug.setProduct(this@Product)
                        val bugMap = bugs[i] as HashMap<String, Any>
                        try {
                            bug.id = Integer.parseInt(bugMap["id"]!!.toString())
                            bug.summary = bugMap["summary"]!!.toString()
                            val creationTime: String
                            creationTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(bugMap["creation_time"])
                            bug.creationDate = creationTime
                            bug.priority = bugMap["priority"]!!.toString()
                            bug.status = bugMap["status"]!!.toString()
                            bug.resolution = bugMap["resolution"]!!.toString()
                            bug.setReporter(User(bugMap["creator"]!!.toString()))
                            bug.setAssignee(User(bugMap["assigned_to"]!!.toString()))
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
                        bug.setProduct(this@Product)
                        try {
                            bug.id = json.getInt("id")
                            bug.summary = json.getString("summary")
                            bug.creationDate = Util.formatDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("creation_time"))
                            bug.priority = json.getString("priority")
                            bug.status = json.getString("status")
                            bug.resolution = json.getString("resolution")
                            bug.setReporter(User(json.getString("creator")))
                            bug.setAssignee(User(json.getString("assigned_to")))
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
