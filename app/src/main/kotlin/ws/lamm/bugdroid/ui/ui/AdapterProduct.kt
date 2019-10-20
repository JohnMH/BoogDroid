package ws.lamm.bugdroid.ui.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

import java.util.ArrayList

import me.johnmh.boogdroid.R
import ws.lamm.bugdroid.general.Product

class AdapterProduct(private val fullValues: List<Product>, private val fragment: ProductListFragment) : RecyclerView.Adapter<AdapterProduct.ViewHolder>(), Filterable {
    private var values: List<Product>? = null
    private var filter: Filter? = null

    init {
        this.values = fullValues
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_product, parent, false)
        return ViewHolder(v, fragment)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val p = values!![i]
        holder.name.text = p.name
        holder.description.text = p.description
    }

    override fun getItemCount(): Int {
        return values!!.size
    }

    fun getProductIdFromPosition(position: Int): Int {
        return values!![position].id
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = object : Filter() {
                override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                    val filterResults = Filter.FilterResults()
                    if (charSequence == null || charSequence.length == 0) {
                        filterResults.count = fullValues.size
                        filterResults.values = fullValues
                    } else {
                        val nProducts = ArrayList<Product>()
                        for (product in fullValues) {
                            if (product.name!!.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                nProducts.add(product)
                            }
                        }
                        filterResults.count = nProducts.size
                        filterResults.values = nProducts
                    }
                    return filterResults
                }

                override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                    values = filterResults.values as List<Product>
                    notifyDataSetChanged()
                }
            }
        }
        return filter as Filter
    }

    class ViewHolder(v: View, fragment: ProductListFragment) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById<View>(R.id.name) as TextView
        val description: TextView = v.findViewById<View>(R.id.description) as TextView

        init {
            v.setOnClickListener { fragment.onListItemClick(position) }
        }
    }
}
