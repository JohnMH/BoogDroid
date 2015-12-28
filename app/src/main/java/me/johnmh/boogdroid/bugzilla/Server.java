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

package me.johnmh.boogdroid.bugzilla;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.johnmh.util.Util.TaskListener;

public class Server extends me.johnmh.boogdroid.general.Server {
    public Server(final String name, final String url) {
        super(name, url, BUGZILLA);
    }

    public Server(final me.johnmh.boogdroid.db.Server server) {
        super(server);
    }

    @Override
    protected void loadProducts() {
        // Get all the products' ids and pass it to loadProductsFromIds()
        final BugzillaTask task = new BugzillaTask(this, "Product.get_accessible_products", new TaskListener() {
            @Override
            public void doInBackground(final String s) {
            }

            @Override
            public void onPostExecute(final String s) {
                try {
                    final JSONObject object = new JSONObject(s);
                    loadProductsFromIds(object.getJSONObject("result").getString("ids"));
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
        task.execute();
    }

    private void loadProductsFromIds(final String productIds) {
        final Server server = this;
        final List<me.johnmh.boogdroid.general.Product> newList = new ArrayList<me.johnmh.boogdroid.general.Product>();
        final BugzillaTask task = new BugzillaTask(this, "Product.get", "'ids':" + productIds + ",'include_fields':['id', 'name', 'description']", new TaskListener() {
            @Override
            public void doInBackground(final String s) {
                try {
                    final JSONObject object = new JSONObject(s);
                    final JSONArray productsJson = object.getJSONObject("result").getJSONArray("products");
                    final int size = productsJson.length();
                    for (int i = 0; i < size; ++i) {
                        final JSONObject p = productsJson.getJSONObject(i);
                        newList.add(new Product(server, p));
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute(final String s) {
                products.clear();
                products.addAll(newList);
                productsListUpdated();
            }
        });
        task.execute();
    }
}
