package ws.lamm.bugdroid.orm;

import io.objectbox.converter.PropertyConverter

enum class WebService(val id: Int, val endpoint: String) {
    REST(0, "/rest/") {

//        override fun getBug(vararg  ids: String): JSONObject {
//            TODO("https://bugzilla.readthedocs.io/en/latest/api/core/v1/bug.html#get-bug")
//        }

    },

    JSON(1, "/jsonrpc.cgi") {},

    XMLRPC(2, "/xmlrpc.cgi") {};

//    abstract fun getBug(vararg ids: String):JSONObject

//    abstract fun getBugHistory(id: String, new_since: String)

//    abstract fun searchBugs


    class Converter : PropertyConverter<WebService, Int> {
        override fun convertToDatabaseValue(entityProperty: WebService): Int {
            return entityProperty.id
        }

        override fun convertToEntityProperty(databaseValue: Int): WebService {
            return try {
                values().first { it.id == databaseValue }
            } catch (e: NoSuchElementException) {
                REST
            }
        }
    }
}