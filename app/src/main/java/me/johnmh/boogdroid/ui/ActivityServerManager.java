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

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import me.johnmh.boogdroid.R;

public class ActivityServerManager extends AppCompatActivity implements ServerListFragment.OnServerSelectedListener {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.actionBarSize});
        int mActionBarSize = (int)styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        RelativeLayout listFrag = (RelativeLayout)findViewById(R.id.server_manager_frag);
        listFrag.setPadding(0, getStatusBarHeight() + mActionBarSize, 0, 0);
        setTitle("Servers");
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_server_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                startActivity(new Intent(this, ActivityRegister.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onServerSelected(final int position) {
        final Intent intent = new Intent(this, ActivityRegister.class);
        intent.putExtra("server_position", position);
        startActivity(intent);
    }
}
