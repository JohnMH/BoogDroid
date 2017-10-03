package me.johnmh.boogdroid.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.johnmh.boogdroid.R;

public class TabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 4 ;
    private int serverPos;
    private int productId;
    private int bugId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x =  inflater.inflate(R.layout.tab_layout,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        Bundle arguments = getArguments();
        if (arguments != null) {
            serverPos = arguments.getInt("server_position", -1);
            productId = arguments.getInt("product_id", -1);
            bugId = arguments.getInt("bug_id", -1);
        } else {
            serverPos = -1;
            productId = -1;
            bugId = -1;
        }
        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return x;

    }

    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            final Bundle arguments = new Bundle();
            arguments.putInt("server_position", serverPos);
            arguments.putInt("product_id", productId);
            arguments.putInt("bug_id", bugId);
            switch (position){
                case 0 : //Comments
                    BugInfoFragment bugInfoFragment = new BugInfoFragment();
                    bugInfoFragment.setArguments(arguments);
                    return bugInfoFragment;
                case 1 : //Status
                    BugStatusFragment bugStatusFragment = new BugStatusFragment();
                    bugStatusFragment.setArguments(arguments);
                    return bugStatusFragment;
                case 2 : //Attributes
                    BugAttributesFragment bugAttributesFragment = new BugAttributesFragment();
                    bugAttributesFragment.setArguments(arguments);
                    return bugAttributesFragment;
                case 3 : //Attachments
                    BugAttachmentsFragment bugAttachmentsFragment = new BugAttachmentsFragment();
                    bugAttachmentsFragment.setArguments(arguments);
                    return bugAttachmentsFragment;
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Comments";
                case 1 :
                    return "Status";
                case 2 :
                    return "Attributes";
                case 3 :
                    return "Attachments";
            }
            return null;
        }
    }
}