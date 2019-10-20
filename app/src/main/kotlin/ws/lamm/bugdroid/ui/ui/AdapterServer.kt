package ws.lamm.bugdroid.ui.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import me.johnmh.boogdroid.R
import ws.lamm.bugdroid.general.Server


class AdapterServer(private val listener: LeftMenuFragment.OnServerSelectedListener, private val showServerManager: Boolean) : RecyclerView.Adapter<AdapterServer.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_server, parent, false)
        return ViewHolder(v, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (showServerManager && position == itemCount - 1) {
            holder.name.setText(R.string.manage_servers)
            holder.iconImage.visibility = View.GONE
        } else {
            val s = Server.servers[position]

            holder.name.text = s.name
            holder.iconImage.visibility = View.VISIBLE
            holder.iconImage.setImageResource(Server.typeIcon[Server.typeName.indexOf(s.type)])
        }
    }

    override fun getItemCount(): Int {
        return if (showServerManager) Server.servers.size + 1 else Server.servers.size
    }

    class ViewHolder(v: View, listener: LeftMenuFragment.OnServerSelectedListener) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById<View>(R.id.name) as TextView
        val iconImage: ImageView = v.findViewById<View>(R.id.icon) as ImageView

        init {
            v.findViewById<View>(R.id.delete_button).visibility = View.GONE
            v.setOnClickListener { listener.onServerSelected(position) }
        }
    }
}
