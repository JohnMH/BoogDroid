package ws.lamm.util

import java.math.BigInteger
import java.security.MessageDigest
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

object Util {

    fun formatDate(format: String, string: String): String {
        try {
            val df = SimpleDateFormat(format, Locale.getDefault())
            return DateFormat.getDateTimeInstance().format(df.parse(string))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return string
    }

    interface TaskListener {
        fun doInBackground(response: Any?)
        fun onPostExecute(response: Any?)
    }
}
