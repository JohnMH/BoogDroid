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

package me.johnmh.boogdroid.general;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.johnmh.boogdroid.R;
import me.johnmh.boogdroid.bugzilla.ChangeStatusInfo;
import me.johnmh.boogdroid.ui.AdapterProduct;

public abstract class Server {
    public static List<Server> servers = new ArrayList<Server>();
    public static List<String> typeName = Arrays.asList(Server.BUGZILLA);
    public static List<Integer> typeIcon = Arrays.asList(Server.BUGZILLA_ICON);

    public static final String BUGZILLA = "Bugzilla";

    private static final int BUGZILLA_ICON = R.drawable.server_icon_bugzilla;

    protected final List<Product> products = new ArrayList<Product>();
    protected final String type;
    protected boolean jsonImplementation;
    protected String name;
    protected String url;
    protected String user;
    protected String password;

    private AdapterProduct adapter;
    private AppCompatActivity activity;

    private me.johnmh.boogdroid.db.Server databaseServer = null;
    private BugStatusChanges statusChanges;
    private BugResolutionChanges resolutionValues;

    public Server(final String name, final String url, final String type, boolean jsonImplementation) {
        this.type = type;
        this.name = name;
        this.url = url;
        user = "";
        password = "";
        this.jsonImplementation = jsonImplementation;
    }

    public Server(final me.johnmh.boogdroid.db.Server server) {
        databaseServer = server;
        type = server.type;
        name = server.name;
        url = server.url;
        user = server.user;
        password = server.password;
        jsonImplementation = server.json == null ? false : server.json;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setUser(final String user, final String password) {
        this.user = user;
        this.password = password;
    }

    public void setAdapterProduct(final AdapterProduct adapter, final AppCompatActivity activity) {
        this.adapter = adapter;
        this.activity = activity;

        activity.setSupportProgressBarIndeterminateVisibility(true);
        loadProducts();
    }

    public Product getProductFromId(final int productId) {
        for (Product p : products) {
            if (p.getId() == productId) {
                return p;
            }
        }

        return null;
    }

    public Bug getBugFromId(final int bugId) {
        for (Product p : products) {
            for (Bug b : p.bugs) {
                if (b.getId() == bugId) {
                    return b;
                }
            }
        }

        return null;
    }

    public void save() {
        if (databaseServer == null) {
            databaseServer = new me.johnmh.boogdroid.db.Server();
        }
        databaseServer.type = type;
        databaseServer.name = name;
        databaseServer.url = url;
        databaseServer.user = user;
        databaseServer.password = password;
        databaseServer.save();
    }

    public void delete() {
        if (databaseServer != null) {
            databaseServer.delete();
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Boolean isUseJson() {
        return jsonImplementation;
    }

    public boolean hasUser() {
        return !TextUtils.isEmpty(user);
    }

    protected abstract void loadProducts();

    protected void productsListUpdated() {
        adapter.notifyDataSetChanged();
        activity.setSupportProgressBarIndeterminateVisibility(false);
    }

    public void setUseJson(Boolean useJson) {
        this.jsonImplementation = useJson;
    }


    public void setStatusChanges(BugStatusChanges changes) {
        this.statusChanges = changes;
    }

    public BugStatusChanges getStatusChanges() {
        return statusChanges;
    }

    public void setResolutionValues(BugResolutionChanges resolutionValues) {
        this.resolutionValues = resolutionValues;
    }

    public BugResolutionChanges getResolutionValues() {
        return resolutionValues;
    }

    public ChangeStatusInfo findChangeStatusInfo(String statusName) {
        List<ChangeStatusInfo> changeList = getStatusChanges().get(statusName).getChangeList();
        for (ChangeStatusInfo changeStatusInfo : changeList) {
            if (changeStatusInfo.getName().equals(statusName)) {
                return changeStatusInfo;
            }
        }
        return null;
    }

    public StatusInfo findStatusInfo(String statusName) {
        return getStatusChanges().get(statusName);
    }

}
