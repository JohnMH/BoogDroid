package ws.lamm.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView

import java.net.URL
import java.util.HashMap

class ImageLoader private constructor(private val src: String, private val view: ImageView) : AsyncTask<Void, Void, Bitmap>() {

    override fun doInBackground(vararg params: Void): Bitmap? {
        try {
            val url = URL(src)
            return BitmapFactory.decodeStream(url.openStream())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(result: Bitmap?) {
        if (result != null) {
            cache[src] = result
            view.setImageBitmap(result)
        }
    }

    companion object {
        var cache: MutableMap<String, Bitmap> = HashMap()

        fun loadImage(src: String, view: ImageView) {
            if (cache.containsKey(src)) {
                view.setImageBitmap(cache[src])
            } else {
                ImageLoader(src, view).execute()
            }
        }
    }
}
