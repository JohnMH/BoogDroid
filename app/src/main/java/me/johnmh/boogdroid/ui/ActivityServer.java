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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import me.johnmh.boogdroid.R;
import me.johnmh.boogdroid.general.Server;

public class ActivityServer extends AppCompatActivity implements ProductListFragment.OnProductSelectedListener, BugListFragment.OnBugSelectedListener, LeftMenuFragment.OnServerSelectedListener {
    private int serverPos;
    private int productId;

    private DrawerLayout drawerLayout;
    private ProgressBar progressBar;

    private static int toolbarHeight = 0;

    public static int getFakeToolHeight(){
        return toolbarHeight;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        toolbarHeight = toolbar.getHeight();

        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
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
    public void setSupportProgressBarIndeterminateVisibility(final boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setServer(getIntent().getIntExtra("server_position", -1));
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        setServer(intent.getIntExtra("server_position", -1));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onServerSelected(final int position) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (position == Server.servers.size()) {
            openServerManager();
        } else {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().popBackStack();
            setServer(position);
        }
    }

    @Override
    public void onProductSelected(final int productId) {
        this.productId = productId;
        Fragment bugsFragment = new BugListFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt("server_position", serverPos);
        arguments.putInt("product_id", productId);
        bugsFragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, bugsFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBugSelected(final int bugId) {
//        Fragment bugInfoFragment = new BugInfoFragment();
        Fragment bugTabFragment = new TabFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt("server_position", serverPos);
        arguments.putInt("product_id", productId);
        arguments.putInt("bug_id", bugId);
        bugTabFragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, bugTabFragment).addToBackStack(null).commit();
    }

    private void setServer(final int pos) {
        serverPos = pos;

        if (serverPos == -1) {
            if (Server.servers.size() == 0) {
                openServerManager();
                return;
            } else {
                serverPos = 0;
            }
        }

        Fragment productsFragment = new ProductListFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt("server_position", serverPos);
        productsFragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, productsFragment).commit();
    }

    private void openServerManager() {
        startActivity(new Intent(this, ActivityServerManager.class));
    }
}
