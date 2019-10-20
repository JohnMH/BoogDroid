package ws.lamm.bugdroid.ui.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.johnmh.boogdroid.R
import ws.lamm.bugdroid.general.Bug

class AdapterBug(context: Context, list: List<Bug>) : ArrayContainsAdapter<Bug>(context, R.layout.adapter_bug, list) {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_bug, parent, false)
            holder = ViewHolder()
            holder.summary = convertView!!.findViewById<View>(R.id.summary) as TextView
            holder.creationDate = convertView.findViewById<View>(R.id.creation_date) as TextView
            holder.assignee = convertView.findViewById<View>(R.id.assignee) as TextView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        val item = getItem(position)
        val color: Int
        if (item.isOpen) {
            color = context.resources.getColor(R.color.adapter_red)
        } else {
            color = context.resources.getColor(R.color.adapter_green)
        }
        var summary = ""
        if (item.priority != null) {
            summary += "[" + item.priority + "] "
        }
        summary += item.summary

        holder.summary!!.setTextColor(color)
        holder.summary!!.text = summary

        holder.creationDate!!.text = item.creationDate
        val assignee = item.assignee
        if (assignee != null) {
            holder.assignee!!.text = assignee.name
        } else {
            holder.assignee!!.text = ""
        }

        return convertView
    }

    fun getBugIdFromPosition(position: Int): Int {
        return getItem(position).id
    }

    private class ViewHolder {
        internal var summary: TextView? = null
        internal var creationDate: TextView? = null
        internal var assignee: TextView? = null
    }
}
