/*
 *  BugsControl
 *  Copyright (C) 2014  Jon Ander Peñalba
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.albfan.boogdroid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.albfan.boogdroid.R;

public class LeftMenuFragment extends Fragment {
    private OnServerSelectedListener listener;
    private AdapterServer adapter;

    public interface OnServerSelectedListener {
        public void onServerSelected(final int position);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        listener = (OnServerSelectedListener) activity;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        adapter = new AdapterServer(listener, true);

        final RecyclerView view = new RecyclerView(getActivity());
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.setAdapter(adapter);

        view.setPadding(0, getStatusBarHeight(), 0, 0);
        view.setBackgroundResource(R.color.darkBG);

        return view;
    }

    public int getStatusBarHeight(){
        int result = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resId > 0){
            result = getResources().getDimensionPixelSize(resId);
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
