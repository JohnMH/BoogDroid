package ws.lamm.bugdroid.bugzilla

import android.os.AsyncTask
import android.widget.Toast
import de.timroes.axmlrpc.XMLRPCClient
import de.timroes.axmlrpc.XMLRPCException
import me.johnmh.boogdroid.bugzilla.MySSLSocketFactory
import me.johnmh.boogdroid.general.Server
import ws.lamm.util.Util.TaskListener
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ws.lamm.bugdroid.Application
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class BugzillaTask(private val server: Server, private val method: String, private var params: String?, private val listener: TaskListener) : AsyncTask<Void, Void, Void>() {
    private var response: Any? = null

    constructor(server: Server, method: String, listener: TaskListener) : this(server, method, "", listener)

    override fun doInBackground(vararg p: Void): Void? {

        return if (server.isUseJson!!) {
            doJsonImplementation()
        } else {
            doXmlImplementation()
        }
    }

    private fun doXmlImplementation(): Void? {
        var client: XMLRPCClient? = null
        try {
            var url = server.url
            if (!url.startsWith("http")) {
                url = "https://$url"
            }
            if (!url.endsWith("/xmlrpc.cgi")) {
                url = "$url/xmlrpc.cgi"
            }
            client = XMLRPCClient(URL(url))
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        var args: MutableMap<String, Any>? = null
        try {
            if (params == null || params == "") {
                args = HashMap()
            } else {
                args = jsonToMap(JSONObject("{$params}"))
            }
            args["Bugzilla_login"] = server.user
            args["Bugzilla_password"] = server.password
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        try {
            response = client!!.call(method, arrayOf(args))
        } catch (e: XMLRPCException) {
            Toast.makeText(Application.appContext, e.message, Toast.LENGTH_SHORT).show()

            e.printStackTrace()
        }

        listener.doInBackground(response)
        return null
    }

    private fun doJsonImplementation(): Void? {
        try {
            // Add login info if needed
            if (server.hasUser()) {
                if (params!!.length > 0) {
                    params += ","
                }
                params += "'Bugzilla_login':'" + server.user + "','Bugzilla_password':'" + server.password + "'"
            }

            // Create the final array
            val array: JSONArray
            if (params!!.length > 0) {
                array = JSONArray("[{$params}]")
            } else {
                array = JSONArray()
            }

            // Create the request
            val request = JSONObject()
            request.put("id", UUID.randomUUID().hashCode())
            request.put("method", method)
            request.put("params", array)

            // Send the request
            val httpClient = MySSLSocketFactory.getNewHttpClient()
            val httpPost = HttpPost(server.url + "/jsonrpc.cgi")
            httpPost.addHeader("Content-Type", "application/json")
            httpPost.entity = StringEntity(request.toString())
            val entity = httpClient.execute(httpPost).entity
            response = EntityUtils.toString(entity)
        } catch (e: Exception) {
            Toast.makeText(Application.appContext, e.message, Toast.LENGTH_SHORT).show()
        }

        listener.doInBackground(response)
        return null
    }

    override fun onPostExecute(result: Void) {
        listener.onPostExecute(response)
    }

    companion object {

        @Throws(JSONException::class)
        fun jsonToMap(json: JSONObject): MutableMap<String, Any> {
            var retMap: MutableMap<String, Any> = HashMap()

            if (json !== JSONObject.NULL) {
                retMap = toMap(json)
            }
            return retMap
        }

        @Throws(JSONException::class)
        fun toMap(`object`: JSONObject): MutableMap<String, Any> {
            val map = HashMap<String, Any>()

            val keysItr = `object`.keys()
            while (keysItr.hasNext()) {
                val key = keysItr.next()
                var value = `object`.get(key)

                if (value is JSONArray) {
                    value = toList(value)
                } else if (value is JSONObject) {
                    value = toMap(value)
                }
                map[key] = value
            }
            return map
        }

        @Throws(JSONException::class)
        fun toList(array: JSONArray): List<Any> {
            val list = ArrayList<Any>()
            for (i in 0 until array.length()) {
                var value = array.get(i)
                if (value is JSONArray) {
                    value = toList(value)
                } else if (value is JSONObject) {
                    value = toMap(value)
                }
                list.add(value)
            }
            return list
        }
    }
}
