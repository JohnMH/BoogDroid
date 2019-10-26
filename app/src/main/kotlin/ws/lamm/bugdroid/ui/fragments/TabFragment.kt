package ws.lamm.bugdroid.ui.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.kodein.di.KodeinAware
import org.kodein.di.android.support.closestKodein
import ws.lamm.bugdroid.R
import ws.lamm.bugdroid.ui.BugAttachmentsFragment
import ws.lamm.bugdroid.ui.BugAttributesFragment
import ws.lamm.bugdroid.ui.BugInfoFragment
import ws.lamm.bugdroid.ui.BugStatusFragment

class TabFragment : Fragment(), KodeinAware {

    override val kodein by closestKodein()

    private var serverPos: Int = 0
    private var productId: Int = 0
    private var bugId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.tab_layout, null)
        tabLayout = view.findViewById<View>(R.id.tabs) as TabLayout
        viewPager = view.findViewById<View>(R.id.viewpager) as ViewPager

        val arguments = arguments

        if (arguments != null) {
            serverPos = arguments.getInt("server_position", -1)
            productId = arguments.getInt("product_id", -1)
            bugId = arguments.getInt("bug_id", -1)
        } else {
            serverPos = -1
            productId = -1
            bugId = -1
        }


        viewPager.adapter = MyAdapter(childFragmentManager)

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */
        tabLayout.post { tabLayout.setupWithViewPager(viewPager) }

        return view

    }

    internal inner class MyAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        /**
         * Return fragment with respect to Position .
         */

        override fun getItem(position: Int): Fragment? {
            val arguments = Bundle()
            arguments.putInt("server_position", serverPos)
            arguments.putInt("product_id", productId)
            arguments.putInt("bug_id", bugId)
            when (position) {
                0 //Comments
                -> {
                    val bugInfoFragment = BugInfoFragment()
                    bugInfoFragment.arguments = arguments
                    return bugInfoFragment
                }
                1 //Status
                -> {
                    val bugStatusFragment = BugStatusFragment()
                    bugStatusFragment.arguments = arguments
                    return bugStatusFragment
                }
                2 //Attributes
                -> {
                    val bugAttributesFragment = BugAttributesFragment()
                    bugAttributesFragment.arguments = arguments
                    return bugAttributesFragment
                }
                3 //Attachments
                -> {
                    val bugAttachmentsFragment = BugAttachmentsFragment()
                    bugAttachmentsFragment.arguments = arguments
                    return bugAttachmentsFragment
                }
            }
            return null
        }

        override fun getCount(): Int {

            return int_items

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        override fun getPageTitle(position: Int): CharSequence? {

            when (position) {
                0 -> return "Comments"
                1 -> return "Status"
                2 -> return "Attributes"
                3 -> return "Attachments"
            }
            return null
        }
    }

    companion object {

        lateinit var tabLayout: TabLayout
        lateinit var viewPager: ViewPager
        var int_items = 4
    }
}