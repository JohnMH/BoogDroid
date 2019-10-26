package ws.lamm.bugdroid.ui

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import ws.lamm.bugdroid.R
import ws.lamm.bugdroid.bugzilla.Server

class ProductListFragment : Fragment() {
    private var listener: OnProductSelectedListener? = null
    private var adapter: AdapterProduct? = null

    interface OnProductSelectedListener {
        fun onProductSelected(productId: Int)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        listener = activity as OnProductSelectedListener?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.recycler_view, container, false)
        val activity = activity as AppCompatActivity?
        val view = rootView.findViewById<View>(R.id.recycler) as RecyclerView
        view.setHasFixedSize(true)

        view.layoutManager = LinearLayoutManager(activity)

        activity!!.setSupportProgressBarIndeterminateVisibility(true)

        val arguments = arguments
        val serverPos: Int
        if (arguments != null) {
            serverPos = arguments.getInt("server_position", 0)
        } else {
            serverPos = 0
        }

        val server = Server.servers[serverPos]

        val filterProduct = rootView.findViewById<View>(R.id.editFilter) as EditText
        filterProduct.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                adapter!!.filter.filter(cs)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun afterTextChanged(arg0: Editable) {}
        })
        adapter = AdapterProduct(server.products, this)
        view.adapter = adapter
        server.setAdapterProduct(adapter!!, activity)

        return rootView
    }

    fun onListItemClick(position: Int) {
        listener!!.onProductSelected(adapter!!.getProductIdFromPosition(position))
    }
}
