package ws.lamm.bugdroid.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Html
import android.widget.BaseAdapter
import ws.lamm.bugdroid.R
import ws.lamm.bugdroid.bugzilla.Server

class DialogDeleteServer : DialogFragment() {
    private var serverPos: Int = 0
    private var adapter: BaseAdapter? = null

    fun setServerPos(serverPos: Int): DialogDeleteServer {
        this.serverPos = serverPos
        return this
    }

    fun setAdapter(adapter: BaseAdapter): DialogDeleteServer {
        this.adapter = adapter
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (savedInstanceState != null) {
            val savedServerPos = savedInstanceState.getInt("server_position", -1)
            if (savedServerPos != -1) {
                serverPos = savedServerPos
            }
        }
        val server = Server.servers[serverPos]

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(Html.fromHtml(String.format(getString(R.string.delete_server_title), server.name!!)))
        builder.setMessage(Html.fromHtml(String.format(getString(R.string.delete_server_description), server.name!!)))
        builder.setPositiveButton(R.string.ok) { _, _ ->
            server.delete()
            Server.servers.remove(server)
            if (adapter != null) {
                adapter!!.notifyDataSetChanged()
            }
        }
        builder.setNegativeButton(R.string.cancel) { dialog, id -> }

        return builder.create()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putInt("server_position", serverPos)
        super.onSaveInstanceState(savedInstanceState)
    }
}
