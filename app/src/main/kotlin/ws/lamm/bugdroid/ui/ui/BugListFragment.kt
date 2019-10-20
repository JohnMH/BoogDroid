package ws.lamm.bugdroid.ui.ui

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import me.johnmh.boogdroid.R
import ws.lamm.bugdroid.general.Server


class BugListFragment : ListFragment() {
    private var listener: OnBugSelectedListener? = null
    private var adapter: AdapterBug? = null

    interface OnBugSelectedListener {
        fun onBugSelected(bugId: Int)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        listener = activity as OnBugSelectedListener?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.product_list_fragment, container, false)
        val activity = activity as AppCompatActivity?

        activity!!.setSupportProgressBarIndeterminateVisibility(true)

        val arguments = arguments
        val serverPos: Int
        val productId: Int
        if (arguments != null) {
            serverPos = arguments.getInt("server_position", -1)
            productId = arguments.getInt("product_id", -1)
        } else {
            serverPos = -1
            productId = -1
        }

        if (serverPos == -1 || productId == -1) {
            Toast.makeText(activity, R.string.invalid_product, Toast.LENGTH_SHORT).show()
            activity.onBackPressed()
            return view
        }

        val product = Server.servers[serverPos].getProductFromId(productId)
        val filterProduct = view.findViewById<View>(R.id.editFilterProduct) as EditText
        filterProduct.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                adapter!!.filter.filter(cs)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun afterTextChanged(arg0: Editable) {}
        })

        adapter = AdapterBug(activity, product!!.bugs)
        listAdapter = adapter
        product.setAdapterBug(adapter!!, activity)

        return view
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        listener!!.onBugSelected(adapter!!.getBugIdFromPosition(position))
    }
}
