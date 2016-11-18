/*
 *  BugsControl
 *  Copyright (C) 2013  Jon Ander Peñalba
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

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;

import me.johnmh.boogdroid.R;
import me.johnmh.boogdroid.general.Server;

public class ActivityRegister extends ActionBarActivity {
    private Server server = null;

    private EditText nameView;
    private EditText urlView;
    private EditText userView;
    private EditText passwordView;
    private CheckBox useJsonView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        getSupportActionBar().setDisplayShowHomeEnabled(false);

        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.actionBarSize});
        int mActionBarSize = (int)styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        FrameLayout listFrag = (FrameLayout)findViewById(R.id.server_register_view);
        listFrag.setPadding(0, getStatusBarHeight() + mActionBarSize, 0, 0);

        nameView = ((EditText) findViewById(R.id.name));
        urlView = ((EditText) findViewById(R.id.url));
        userView = ((EditText) findViewById(R.id.user));
        passwordView = ((EditText) findViewById(R.id.password));
        useJsonView = ((CheckBox) findViewById(R.id.useJson));

        final int serverPos = getIntent().getIntExtra("server_position", -1);
        if (serverPos == -1) {
            setTitle(R.string.add_server);
        } else {
            setTitle(R.string.edit_server);
            server = Server.servers.get(serverPos);
            nameView.setText(server.getName());
            urlView.setText(server.getUrl());
            userView.setText(server.getUser());
            passwordView.setText(server.getPassword());
            useJsonView.setActivated(server.isUseJson());
        }
    }

    public int getStatusBarHeight(){
        int result = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resId > 0){
            result = getResources().getDimensionPixelSize(resId);
        }
        return result;
    }

    public void onAccept(final View view) {
        final String name = nameView.getText().toString();
        final String url = urlView.getText().toString();
        final String user = userView.getText().toString();
        final String password = passwordView.getText().toString();
        final Boolean useJsonImplementation = useJsonView.isActivated();

        boolean error = false;

        if (TextUtils.isEmpty(name)) {
            nameView.setError(getString(R.string.name_cant_be_empty));
            error = true;
        }

        for (final Server s : Server.servers) {
            if (s.getName().equals(name) && server != s) {
                nameView.setError(getString(R.string.server_with_that_name_exists));
                error = true;
            }
        }

        if (TextUtils.isEmpty(url)) {
            urlView.setError(getString(R.string.name_cant_be_empty));
            error = true;
        }

        if (!error) {
            if (server == null) {
                registerServer(name, url, user, password, useJsonImplementation);
            } else {
                editServer(name, url, user, password, useJsonImplementation);
            }
        }
    }

    private void editServer(final String name, final String url, final String user, final String password, final Boolean useJsonImplementation) {
        server.setName(name);
        server.setUrl(url);
        server.setUser(user, password);
        server.setUseJson(useJsonImplementation);
        server.save();
        finish();
    }

    private void registerServer(final String name, final String url, final String user, final String password, boolean jsonImplementation) {
        final Server newServer = new me.johnmh.boogdroid.bugzilla.Server(name, url, jsonImplementation);
        newServer.setUser(user, password);
        newServer.save();
        Server.servers.add(newServer);

        finish();
    }
}
