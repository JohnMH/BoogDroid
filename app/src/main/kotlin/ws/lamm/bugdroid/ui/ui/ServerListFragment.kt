package ws.lamm.bugdroid.ui.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import me.johnmh.boogdroid.R
import ws.lamm.bugdroid.general.Server

class ServerListFragment : ListFragment() {
    private var listener: OnServerSelectedListener? = null
    private var adapter: ServerTypeAdapter? = null

    interface OnServerSelectedListener {
        fun onServerSelected(position: Int)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        listener = activity as OnServerSelectedListener?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.product_list_fragment, container, false)

        val filterProduct = view.findViewById<View>(R.id.editFilterProduct) as EditText
        filterProduct.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                adapter!!.filter.filter(cs)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun afterTextChanged(arg0: Editable) {}
        })

        adapter = activity?.let { ServerTypeAdapter(it) }
        listAdapter = adapter
        return view
    }

    override fun onResume() {
        super.onResume()
        adapter!!.notifyDataSetChanged()
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        listener!!.onServerSelected(position)
    }

    private inner class ServerTypeAdapter(context: Context) : ArrayAdapter<Server>(context, R.layout.adapter_server, R.id.name, Server.servers) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = activity!!.layoutInflater.inflate(R.layout.adapter_server, parent, false)
            }

            val s = Server.servers[position]

            (convertView!!.findViewById<View>(R.id.name) as TextView).text = s.name

            val iconImage = convertView.findViewById<View>(R.id.icon) as ImageView
            iconImage.setImageResource(Server.typeIcon[Server.typeName.indexOf(s.type)])

            convertView.findViewById<View>(R.id.delete_button).setOnClickListener { adapter?.let { it1 -> DialogDeleteServer().setAdapter(it1).setServerPos(position).show(fragmentManager!!, "DeleteServerDialog") } }

            return convertView
        }
    }
}
