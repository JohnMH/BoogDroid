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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
        final View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        RecyclerView view = (RecyclerView) rootView.findViewById(R.id.recycler);
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

        EditText filterProduct = (EditText) rootView.findViewById(R.id.editFilter);
        filterProduct.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {}
        });
        adapter = new AdapterProduct(server.getProducts(), this);
        view.setAdapter(adapter);
        server.setAdapterProduct(adapter, activity);

        return rootView;
    }

    public void onListItemClick(final int position) {
        listener.onProductSelected(adapter.getProductIdFromPosition(position));
    }
}
