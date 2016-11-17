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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import me.johnmh.boogdroid.general.BugStatusChanges;
import me.johnmh.boogdroid.general.ChangeStatusCommentRequired;
import me.johnmh.util.Util.TaskListener;

import static android.R.attr.id;

public class Server extends me.johnmh.boogdroid.general.Server {

    public Server(final String name, final String url, boolean jsonImplementation) {
        super(name, url, BUGZILLA, jsonImplementation);
    }

    public Server(final me.johnmh.boogdroid.db.Server server) {
        super(server);
    }

    @Override
    protected void loadProducts() {
        // Get all the products' ids and pass it to loadProductsFromIds()
        final BugzillaTask task = new BugzillaTask(this, "Product.get_accessible_products", new TaskListener() {
            @Override
            public void doInBackground(final Object response) {
            }

            @Override
            public void onPostExecute(final Object response) {
                if (isUseJson()) {
                    doReadJson(response);
                } else {
                    doReadXml(response);
                }
            }

        });
        task.execute();

        new BugzillaTask(this, "Bug.fields", "'names':['bug_status']", new TaskListener() {
            @Override
            public void doInBackground(Object response) {
                if (isUseJson()) {
                    doJsonParse(response);
                } else {
                    doXmlParse(response);
                }
                doXmlParse(response);
            }

            private void doJsonParse(Object response) {
                //TODO: Need a server with json to check this implementation
                final JSONObject object;
                try {
                    object = new JSONObject(response.toString());
                    JSONArray values = object.getJSONObject("fields").getJSONArray("values");
                    BugStatusChanges changes = new BugStatusChanges();
                    for (int i = 0; i < values.length(); i++) {
                        JSONObject status = values.getJSONObject(i);
                        String name = status.getString("name");
                        JSONArray canChangeToList = status.getJSONArray("can_change_to");
                        ChangeStatusCommentRequired changeCommentRequired = new ChangeStatusCommentRequired();
                        for (int j = 0; j < canChangeToList.length(); j++) {
                            JSONObject canChangeToMap = canChangeToList.getJSONObject(j);
                            changeCommentRequired.put(canChangeToMap.getString("name"), canChangeToMap.getBoolean("comment_required"));
                        }
                        changes.put(name, changeCommentRequired);
                    }
                    setChanges(changes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            private void doXmlParse(Object response) {
                Object[] fields = (Object[]) ((HashMap<String, Object>)response).get("fields");
                List values = Arrays.asList((Object[]) ((HashMap<String, Object>) fields[0]).get("values"));
                BugStatusChanges changes = new BugStatusChanges();
                for (Object value : values) {
                    HashMap<String, Object> status = (HashMap<String, Object>) value;
                    String name = (String)status.get("name");
                    List changeToList = Arrays.asList((Object[])status.get("can_change_to"));
                    ChangeStatusCommentRequired changeCommentRequired = new ChangeStatusCommentRequired();
                    for (Object canChangeTo : changeToList) {
                        HashMap<String, Object> canChangeToMap = (HashMap<String, Object>) canChangeTo;
                        changeCommentRequired.put((String)canChangeToMap.get("name"), (Boolean)canChangeToMap.get("comment_required"));
                    }
                    changes.put(name, changeCommentRequired);
                }
                setChanges(changes);
            }

            @Override
            public void onPostExecute(Object response) {

            }
        }).execute();
    }

    private void doReadXml(Object response) {
        try {
            List listaIds = Arrays.asList(((HashMap<String, Object[]>) response).get("ids"));
            Iterator iterator = listaIds.iterator();
            String listaIdsStr = "";
            while (iterator.hasNext()) {
                Object next =  iterator.next();
                listaIdsStr += next;
                if (iterator.hasNext()) {
                    listaIdsStr += ",";
                }
            }
            loadProductsFromIds("["+listaIdsStr+"]");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void doReadJson(Object response) {
        try {
            final JSONObject object = new JSONObject(response.toString());
            String listaIds = object.getJSONObject("result").getString("ids");
            loadProductsFromIds(listaIds);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProductsFromIds(final String productIds) {
        final BugzillaTask task = new BugzillaTask(this, "Product.get", "'ids':" + productIds + ",'include_fields':['id', 'name', 'description']", new TaskListener() {
            List<me.johnmh.boogdroid.general.Product> newList = new ArrayList<>();

            @Override
            public void doInBackground(final Object response) {
                if (isUseJson()) {
                    doJsonParse(response);
                } else {
                    doXmlParse(response);
                }
            }

            private void doXmlParse(Object response) {
                try {
                    List<Object> productsList = Arrays.asList(((HashMap<String, Object[]>) response).get("products"));
                    final int size = productsList.size();
                    for (int i = 0; i < size; ++i) {
                        Product product = new Product();
                        product.setServer(Server.this);
                        try {
                            HashMap<String, Object> productMap = (HashMap<String, Object>) productsList.get(i);
                            product.setId(Integer.parseInt(productMap.get("id").toString()));
                            product.setName(productMap.get("name").toString());
                            product.setDescription(productMap.get("description").toString());
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }

                        newList.add(product);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            private void doJsonParse(Object response) {
                try {
                    final JSONObject object = new JSONObject(response.toString());
                    final JSONArray productsJson = object.getJSONObject("result").getJSONArray("products");
                    final int size = productsJson.length();
                    for (int i = 0; i < size; ++i) {
                        Product product = new Product();
                        product.setServer(Server.this);
                        final JSONObject json = productsJson.getJSONObject(i);
                        try {
                            product.setId(json.getInt("id"));
                            product.setName(json.getString("name"));
                            product.setDescription(json.getString("description"));
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute(final Object response) {
                products.clear();
                products.addAll(newList);
                productsListUpdated();
            }
        });
        task.execute();
    }

}
