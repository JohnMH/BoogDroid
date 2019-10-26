package ws.lamm.bugdroid.ui

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ws.lamm.bugdroid.R

class LeftMenuFragment : Fragment() {
    private var listener: OnServerSelectedListener? = null
    private var adapter: AdapterServer? = null

    private val statusBarHeight: Int
        get() {
            var result = 0
            val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resId > 0) {
                result = resources.getDimensionPixelSize(resId)
            }
            return result
        }

    interface OnServerSelectedListener {
        fun onServerSelected(position: Int)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        listener = activity as OnServerSelectedListener?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        adapter = AdapterServer(listener!!, true)

        val view = RecyclerView(activity)
        view.setHasFixedSize(true)
        view.layoutManager = LinearLayoutManager(activity)
        view.adapter = adapter

        view.setPadding(0, statusBarHeight, 0, 0)
        view.setBackgroundResource(R.color.darkBG)

        return view
    }

    override fun onResume() {
        super.onResume()
        adapter!!.notifyDataSetChanged()
    }
}
