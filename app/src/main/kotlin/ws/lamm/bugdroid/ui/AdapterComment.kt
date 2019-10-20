package ws.lamm.bugdroid.ui

import me.johnmh.boogdroid.R
import ws.lamm.bugdroid.general.Comment
import ws.lamm.bugdroid.ui.ui.ArrayContainsAdapter
import ws.lamm.util.ImageLoader

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ws.lamm.util.md5

class AdapterComment(context: Context, list: List<Comment>) : ArrayContainsAdapter<Comment>(context, R.layout.adapter_comment, list) {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_comment, parent, false)
            holder = ViewHolder()
            holder.creator = convertView!!.findViewById<View>(R.id.creator) as TextView
            holder.text = convertView.findViewById<View>(R.id.text) as TextView
            holder.date = convertView.findViewById<View>(R.id.date) as TextView
            holder.bugNumber = convertView.findViewById<View>(R.id.bug_number) as TextView
            holder.authorImg = convertView.findViewById<View>(R.id.author_img) as ImageView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        val item = getItem(position)

        holder.creator!!.text = item.author.name
        holder.text!!.text = item.text
        holder.date!!.text = item.date
        holder.bugNumber!!.text = StringBuilder().append("#").append(if (item.number > 0) item.number else position + 1).toString()

        val author = item.author
        if (!TextUtils.isEmpty(author.avatarUrl)) {
            ImageLoader.loadImage(author.avatarUrl, holder.authorImg!!)
        } else {
            ImageLoader.loadImage("http://www.gravatar.com/avatar/" + author.email.md5(), holder.authorImg!!)
        }

        return convertView
    }

    override fun areAllItemsEnabled(): Boolean {
        return false
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }

    private class ViewHolder {
        internal var creator: TextView? = null
        internal var text: TextView? = null
        internal var date: TextView? = null
        internal var bugNumber: TextView? = null
        internal var authorImg: ImageView? = null
    }
}
