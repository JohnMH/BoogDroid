package ws.lamm.bugdroid.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import java.util.*

/**
 * A concrete BaseAdapter that is backed by an array of arbitrary
 * objects.  By default this class expects that the provided resource id references
 * a single TextView.  If you want to use a more complex layout, use the constructors that
 * also takes a field id.  That field id should reference a TextView in the larger layout
 * resource.
 *
 *
 * However the TextView is referenced, it will be filled with the toString() of each object in
 * the array. You can add lists or arrays of custom objects. Override the toString() method
 * of your objects to determine what text will be displayed for the item in the list.
 *
 *
 * To use something other than TextViews for the array display, for instance, ImageViews,
 * or to have some of data besides toString() results fill the views,
 * override [.getView] to return the type of view you want.
 *
 * @param context The current context.
 * @param resource The resource ID for a layout file containing a TextView to use when
 * instantiating views.
 * @param objects The objects to represent in the ListView.
 *
 */
abstract class ArrayContainsAdapter<T>(context: Context, resource: Int, objects: MutableList<T>) : BaseAdapter(), Filterable {
    /**
     * Contains the list of objects that represent the data of this ArrayAdapter.
     * The content of this list is referred to as "the array" in the documentation.
     */
    private var mObjects: MutableList<T>? = null

    /**
     * Lock used to modify the content of [.mObjects]. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see [.getFilter] to make a synchronized copy of
     * the original array of data.
     */
    private val mLock = Any()

    /**
     * The resource indicating what views to inflate to display the content of this
     * array adapter.
     */
    private var mResource: Int = 0

    /**
     * The resource indicating what views to inflate to display the content of this
     * array adapter in a drop down widget.
     */
    private var mDropDownResource: Int = 0

    /**
     * If the inflated resource is not a TextView, [.mFieldId] is used to find
     * a TextView inside the inflated views hierarchy. This field must contain the
     * identifier that matches the one defined in the resource file.
     */
    private var mFieldId = 0

    /**
     * Indicates whether or not [.notifyDataSetChanged] must be called whenever
     * [.mObjects] is modified.
     */
    private var mNotifyOnChange = true

    /**
     * Returns the context associated with this array adapter. The context is used
     * to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    lateinit var context: Context
        private set

    // A copy of the original mObjects array, initialized from and then used instead as soon as
    // the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
    private var mOriginalValues: ArrayList<T>? = null
    private var mFilter: ArrayFilter? = null

    private var mInflater: LayoutInflater? = null

    init {
        init(context, resource, objects)
    }

    /**
     * Remove all elements from the list.
     */
    fun clear() {
        synchronized(mLock) {
            if (mOriginalValues != null) {
                mOriginalValues!!.clear()
            } else {
                mObjects!!.clear()
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged()
    }

    /**
     * {@inheritDoc}
     */
    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        mNotifyOnChange = true
    }

    /**
     * Control whether methods that change the list ([.add],
     * [.insert], [.remove], [.clear]) automatically call
     * [.notifyDataSetChanged].  If set to false, caller must
     * manually call notifyDataSetChanged() to have the changes
     * reflected in the attached view.
     *
     * The default is true, and calling notifyDataSetChanged()
     * resets the flag to true.
     *
     * @param notifyOnChange if true, modifications to the list will
     * automatically call [                       ][.notifyDataSetChanged]
     */
    fun setNotifyOnChange(notifyOnChange: Boolean) {
        mNotifyOnChange = notifyOnChange
    }

    private fun init(context: Context, resource: Int, objects: MutableList<T>) {
        this.context = context
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mDropDownResource = resource
        mResource = mDropDownResource
        mObjects = objects
        mFieldId = 0
    }

    /**
     * {@inheritDoc}
     */
    override fun getCount(): Int {
        return mObjects!!.size
    }

    /**
     * {@inheritDoc}
     */
    override fun getItem(position: Int): T {
        return mObjects!![position]
    }

    /**
     * {@inheritDoc}
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup,
                                       resource: Int): View {
        val view: View
        val text: TextView

        if (convertView == null) {
            view = mInflater!!.inflate(resource, parent, false)
        } else {
            view = convertView
        }

        try {
            if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = view as TextView
            } else {
                //  Otherwise, find the TextView field within the layout
                text = view.findViewById<View>(mFieldId) as TextView
            }
        } catch (e: ClassCastException) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView")
            throw IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e)
        }

        val item = getItem(position)
        if (item is CharSequence) {
            text.text = item
        } else {
            text.text = item.toString()
        }

        return view
    }

    /**
     *
     * Sets the layout resource to create the drop down views.
     *
     * @param resource the layout resource defining the drop down views
     * @see .getDropDownView
     */
    fun setDropDownViewResource(resource: Int) {
        this.mDropDownResource = resource
    }

    /**
     * {@inheritDoc}
     */
    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent, mDropDownResource)
    }

    /**
     * {@inheritDoc}
     */
    override fun getFilter(): Filter {
        if (mFilter == null) {
            mFilter = ArrayFilter()
        }
        return mFilter as ArrayFilter
    }

    /**
     *
     * An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.
     */
    private inner class ArrayFilter : Filter() {
        override fun performFiltering(prefix: CharSequence?): FilterResults {
            val results = FilterResults()

            if (mOriginalValues == null) {
                synchronized(mLock) {
                    mOriginalValues = ArrayList(mObjects!!)
                }
            }

            if (prefix == null || prefix.length == 0) {
                val list: ArrayList<T>
                synchronized(mLock) {
                    list = ArrayList(mOriginalValues!!)
                }
                results.values = list
                results.count = list.size
            } else {
                val prefixString = prefix.toString().toLowerCase(Locale.getDefault())

                val values: ArrayList<T>
                synchronized(mLock) {
                    values = ArrayList(mOriginalValues!!)
                }

                val count = values.size
                val newValues = ArrayList<T>()

                for (i in 0 until count) {
                    val value = values[i]
                    val valueText = value.toString().toLowerCase(Locale.getDefault())

                    // First match against the whole, non-splitted value
                    if (valueText.contains(prefixString)) {
                        newValues.add(value)
                    }
                }

                results.values = newValues
                results.count = newValues.size
            }

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {

            mObjects = results.values as MutableList<T>
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }
}
