package ws.lamm.bugdroid.bugzilla

import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import org.json.JSONException
import org.json.JSONObject
import ws.lamm.bugdroid.R
import ws.lamm.bugdroid.db.Server
import ws.lamm.bugdroid.orm.BugzillaServer
import ws.lamm.bugdroid.ui.AdapterProduct
import ws.lamm.util.Util.TaskListener
import java.util.*

class Server {

    val products: MutableList<Product> = ArrayList()
    var type: String
    private var jsonImplementation: Boolean = false
    var name: String
    var url: String? = null
    var user: String? = null
    var password: String? = null

    private var adapter: AdapterProduct? = null
    private var activity: AppCompatActivity? = null

    private var databaseServer: Server? = null
    var statusChanges = HashMap<String?, StatusInfo>()
    var bugResolutionChanges: ArrayList<String>? = null

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

    constructor(name: String, url: String, jsonImplementation: Boolean) : this(name, url, BUGZILLA, jsonImplementation)

    constructor(server: Server) {
        databaseServer = server
        type = server.type
        name = server.name
        url = server.url
        user = server.user
        password = server.password
        //jsonImplementation = if (server.json == null) false else server.json
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
            databaseServer = Server()
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
        //return name
        return ""
    }

    fun hasUser(): Boolean {
        return !TextUtils.isEmpty(user)
    }

    protected fun productsListUpdated() {
        adapter!!.notifyDataSetChanged()
        activity!!.setSupportProgressBarIndeterminateVisibility(false)
    }

    fun findChangeStatusInfo(statusName: String): ChangeStatusInfo? {
        val changeList = statusChanges!![statusName]!!.changeList
//        for (changeStatusInfo in changeList) {
//            if (changeStatusInfo.name == statusName) {
//                return changeStatusInfo
//            }
//        }
        return null
    }

    fun findStatusInfo(statusName: String): StatusInfo? {
        return statusChanges!![statusName]
    }
    companion object {
        const val BUGZILLA = "Bugzilla"

        private const val BUGZILLA_ICON = R.drawable.server_icon_bugzilla
        var servers: MutableList<BugzillaServer> = ArrayList()
        var typeName = listOf(BUGZILLA)
        var typeIcon = listOf(BUGZILLA_ICON)



    }

    fun loadProducts() {
        val task = BugzillaTask(this, "Product.get_accessible_products", object : TaskListener {
            override fun doInBackground(response: Any?) {}

            override fun onPostExecute(response: Any?) {
                if (isUseJson!!) {
                    doReadJson(response)
                } else {
                    doReadXml(response)
                }
            }

        })
        task.execute()

        BugzillaTask(this, "Bug.fields", "'names':['bug_status', 'resolution']", object : TaskListener {
            override fun doInBackground(response: Any?) {
                if (isUseJson!!) {
                    doJsonParse(response!!)
                } else {
                    doXmlParse(response)
                }
                doXmlParse(response)
            }

            private fun doJsonParse(response: Any) {
                //TODO: Need a server with json to check this implementation
                val `object`: JSONObject
                try {
                    `object` = JSONObject(response.toString())
                    val fields = `object`.getJSONArray("fields")
                    for (i in 0 until fields.length()) {
                        val field = fields.getJSONObject(i)
                        val displayName = field.get("display_name")
                        if (displayName == "Status") {
                            loadStatusJson(field)
                        } else if (displayName == "Resolution") {
                            loadResolutionJson(field)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            @Throws(JSONException::class)
            private fun loadStatusJson(field: JSONObject) {
                val values = field.getJSONArray("values")
                val bugStatusChanges1 = HashMap<String?, StatusInfo>()
                for (j in 0 until values.length()) {
                    val status = values.getJSONObject(j)
                    val name = status.getString("name")
                    val statusInfo = StatusInfo()
                    statusInfo.name = name
                    val canChangeToList = status.getJSONArray("can_change_to")
                    val changeStatusInfoList = ArrayList<ChangeStatusInfo>()
                    for (k in 0 until canChangeToList.length()) {
                        val statusInfoMap = canChangeToList.getJSONObject(j)
                        val changeStatusInfo = ChangeStatusInfo()
                        changeStatusInfo.name = statusInfoMap.getString("name")
                        changeStatusInfo.isCommentRequired = statusInfoMap.getBoolean("comment_required")
                        changeStatusInfoList.add(changeStatusInfo)
                    }
                    statusInfo.changeList = changeStatusInfoList
                    statusInfo.isOpen = status.getBoolean("is_open")
                    bugStatusChanges1[name] = statusInfo
                }
                statusChanges = bugStatusChanges1
            }

            @Throws(JSONException::class)
            private fun loadResolutionJson(field: JSONObject) {
                val values = field.getJSONArray("values")
                val resolution = ArrayList<String>()
                for (j in 0 until values.length()) {
                    val status = values.getJSONObject(j)
                    val name = status.getString("name")
                    resolution.add(name)
                }
                bugResolutionChanges = resolution
            }

            private fun doXmlParse(response: Any?) {
                val fields = listOf(*((response as HashMap<String, Any>)["fields"] as Array<Any>?)!!)
                for (field in fields) {
                    val fieldMap = field as HashMap<String, Any>
                    val displayName = fieldMap["display_name"]
                    if (displayName == "Status") {
                        loadStatusXml(fieldMap)
                    } else if (displayName == "Resolution") {
                        loadResolutionXml(fieldMap)
                    }
                }
            }

            private fun loadStatusXml(fieldMap: HashMap<String, Any>) {
                val values = listOf(*(fieldMap["values"] as Array<Any>?)!!)
                val changes = HashMap<String?, StatusInfo>()
                for (value in values) {
                    val status = value as HashMap<String, Any>
                    val name = status["name"] as String
                    val changeToList = listOf(*(status["can_change_to"] as Array<Any>?)!!)
                    val changeStatusInfoList = ArrayList<ChangeStatusInfo>()
                    for (canChangeTo in changeToList) {
                        val statusInfoMap = canChangeTo as HashMap<String, Any>
                        val changeStatusInfo = ChangeStatusInfo()
                        changeStatusInfo.name = statusInfoMap["name"] as String
                        changeStatusInfo.isCommentRequired = statusInfoMap["comment_required"] as Boolean?
                        changeStatusInfoList.add(changeStatusInfo)
                    }
                    val statusInfo = StatusInfo()
                    statusInfo.name = name
                    statusInfo.changeList = changeStatusInfoList
                    statusInfo.isOpen = (status["is_open"] as Boolean?)!!
                    changes[name] = statusInfo
                }
                statusChanges = changes
            }

            private fun loadResolutionXml(fieldMap: HashMap<String, Any>) {
                val values = listOf(*(fieldMap["values"] as Array<Any>?)!!)
                val changes = ArrayList<String>()
                for (value in values) {
                    val resolution = value as HashMap<String, Any>
                    val name = resolution["name"] as String
                    changes.add(name)
                }
                bugResolutionChanges = changes
            }

            override fun onPostExecute(response: Any?) {

            }
        }).execute()
    }

    private fun doReadXml(response: Any?) {
        try {
            val listaIds = listOf(*(response as HashMap<String, Array<Any>>)["ids"]!!)
            val iterator = listaIds.iterator()
            var listaIdsStr = ""
            while (iterator.hasNext()) {
                val next = iterator.next()
                listaIdsStr += next
                if (iterator.hasNext()) {
                    listaIdsStr += ","
                }
            }
            loadProductsFromIds("[$listaIdsStr]")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun doReadJson(response: Any?) {
        try {
            val `object` = JSONObject(response!!.toString())
            val listaIds = `object`.getJSONObject("result").getString("ids")
            loadProductsFromIds(listaIds)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun loadProductsFromIds(productIds: String) {
        val task = BugzillaTask(this, "Product.get", "'ids':$productIds,'include_fields':['id', 'name', 'description']", object : TaskListener {
            var newList: MutableList<Product> = ArrayList()

            override fun doInBackground(response: Any?) {
                if (isUseJson!!) {
                    doJsonParse(response)
                } else {
                    doXmlParse(response)
                }
            }

            private fun doXmlParse(response: Any?) {
                try {
                    val productsList = listOf(*(response as HashMap<String, Array<Any>>)["products"]!!)
                    val size = productsList.size
                    for (i in 0 until size) {
                        val product = Product()
                        product.server = this@Server
                        try {
                            val productMap = productsList[i] as HashMap<String, Any>
                            product.id = Integer.parseInt(productMap["id"]!!.toString())
                            product.name = productMap["name"]!!.toString()
                            product.description = productMap["description"]!!.toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        newList.add(product)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            private fun doJsonParse(response: Any?) {
                try {
                    val `object` = JSONObject(response!!.toString())
                    val productsJson = `object`.getJSONObject("result").getJSONArray("products")
                    val size = productsJson.length()
                    for (i in 0 until size) {
                        val product = Product()
                        product.server = this@Server
                        val json = productsJson.getJSONObject(i)
                        try {
                            product.id = json.getInt("id")
                            product.name = json.getString("name")
                            product.description = json.getString("description")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onPostExecute(response: Any?) {
                products.clear()
                products.addAll(newList)
                productsListUpdated()
            }
        })
        task.execute()
    }
}
