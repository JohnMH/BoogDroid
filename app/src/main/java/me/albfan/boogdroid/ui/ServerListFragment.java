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
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import me.albfan.boogdroid.R;
import me.albfan.boogdroid.general.Server;

public class ServerListFragment extends ListFragment {
    private OnServerSelectedListener listener;
    private ServerTypeAdapter adapter;

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
        final View view = inflater.inflate(R.layout.product_list_fragment, container, false);

        EditText filterProduct = ((EditText) view.findViewById(R.id.editFilterProduct));
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

        adapter = new ServerTypeAdapter(getActivity());
        setListAdapter(adapter);
        return view;
    }

    public int getStatusBarHeight(){
        int result = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resId > 0){
            result = getResources().getDimensionPixelSize(resId);
        }
        result += ActivityServer.getFakeToolHeight();
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        listener.onServerSelected(position);
    }

    private class ServerTypeAdapter extends ArrayAdapter<Server> {
        public ServerTypeAdapter(final Context context) {
            super(context, R.layout.adapter_server, R.id.name, Server.servers);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.adapter_server, parent, false);
            }

            final Server s = Server.servers.get(position);

            ((TextView) convertView.findViewById(R.id.name)).setText(s.getName());

            final ImageView iconImage = (ImageView) convertView.findViewById(R.id.icon);
            iconImage.setImageResource(Server.typeIcon.get(Server.typeName.indexOf(s.getType())));

            convertView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new DialogDeleteServer().setAdapter(adapter).setServerPos(position).show(getFragmentManager(), "DeleteServerDialog");
                }
            });

            return convertView;
        }
    }
}
