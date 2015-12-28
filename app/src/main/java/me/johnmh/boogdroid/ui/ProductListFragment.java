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

package me.johnmh.boogdroid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.johnmh.boogdroid.R;
import me.johnmh.boogdroid.general.Server;

public class ProductListFragment extends Fragment {
    private OnProductSelectedListener listener;
    private AdapterProduct adapter;

    public interface OnProductSelectedListener {
        public void onProductSelected(final int productId);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        listener = (OnProductSelectedListener) activity;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final RecyclerView view = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        final ActionBarActivity activity = (ActionBarActivity) getActivity();

        view.setHasFixedSize(true);

        view.setLayoutManager(new LinearLayoutManager(activity));

        activity.setSupportProgressBarIndeterminateVisibility(true);

        final Bundle arguments = getArguments();
        final int serverPos;
        if (arguments != null) {
            serverPos = arguments.getInt("server_position", 0);
        } else {
            serverPos = 0;
        }

        final Server server = Server.servers.get(serverPos);

        adapter = new AdapterProduct(server.getProducts(), this);
        view.setAdapter(adapter);
        server.setAdapterProduct(adapter, activity);

        return view;
    }

    public void onListItemClick(final int position) {
        listener.onProductSelected(adapter.getProductIdFromPosition(position));
    }
}
