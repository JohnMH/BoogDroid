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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.johnmh.boogdroid.general.BugResolutionChanges;
import me.johnmh.boogdroid.general.BugStatusChanges;
import me.johnmh.boogdroid.general.StatusInfo;
import me.johnmh.util.Util.TaskListener;

public class Server extends me.johnmh.boogdroid.general.Server {

    public Server(final String name, final String url, boolean jsonImplementation) {
        super(name, url, BUGZILLA, jsonImplementation);
    }

    public Server(final me.johnmh.boogdroid.db.Server server) {
        super(server);
    }

    @Override
    protected void loadProducts() {
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

        new BugzillaTask(this, "Bug.fields", "'names':['bug_status', 'resolution']", new TaskListener() {
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
                    JSONArray fields = object.getJSONArray("fields");
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject field = fields.getJSONObject(i);
                        Object displayName = field.get("display_name");
                        if (displayName.equals("Status")) {
                            loadStatusJson(field);
                        } else if (displayName.equals("Resolution")) {
                            loadResolutionJson(field);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            private void loadStatusJson(JSONObject field) throws JSONException {
                JSONArray values = field.getJSONArray("values");
                BugStatusChanges changes = new BugStatusChanges();
                for (int j = 0; j < values.length(); j++) {
                    JSONObject status = values.getJSONObject(j);
                    String name = status.getString("name");
                    StatusInfo statusInfo = new StatusInfo();
                    statusInfo.setName(name);
                    JSONArray canChangeToList = status.getJSONArray("can_change_to");
                    ArrayList<ChangeStatusInfo> changeStatusInfoList = new ArrayList<>();
                    for (int k = 0; k < canChangeToList.length(); k++) {
                        JSONObject statusInfoMap = canChangeToList.getJSONObject(j);
                        ChangeStatusInfo changeStatusInfo = new ChangeStatusInfo();
                        changeStatusInfo.setName(statusInfoMap.getString("name"));
                        changeStatusInfo.setCommentRequired(statusInfoMap.getBoolean("comment_required"));
                        changeStatusInfoList.add(changeStatusInfo);
                    }
                    statusInfo.setChangeList(changeStatusInfoList);
                    statusInfo.setOpen(status.getBoolean("is_open"));
                    changes.put(name, statusInfo);
                }
                setStatusChanges(changes);
            }

            private void loadResolutionJson(JSONObject field) throws JSONException {
                JSONArray values = field.getJSONArray("values");
                BugResolutionChanges resolution = new BugResolutionChanges();
                for (int j = 0; j < values.length(); j++) {
                    JSONObject status = values.getJSONObject(j);
                    String name = status.getString("name");
                    resolution.add(name);
                }
                setResolutionValues(resolution);
            }

            private void doXmlParse(Object response) {
                List fields = Arrays.asList((Object[]) ((HashMap<String, Object>)response).get("fields"));
                for (Object field : fields) {
                    HashMap<String, Object> fieldMap = (HashMap<String, Object>) field;
                    Object displayName = fieldMap.get("display_name");
                    if (displayName.equals("Status")) {
                        loadStatusXml(fieldMap);
                    } else if (displayName.equals("Resolution")) {
                        loadResolutionXml(fieldMap);
                    }
                }
            }

            private void loadStatusXml(HashMap<String, Object> fieldMap) {
                List values = Arrays.asList((Object[]) fieldMap.get("values"));
                BugStatusChanges changes = new BugStatusChanges();
                for (Object value : values) {
                    HashMap<String, Object> status = (HashMap<String, Object>) value;
                    String name = (String)status.get("name");
                    List changeToList = Arrays.asList((Object[])status.get("can_change_to"));
                    ArrayList<ChangeStatusInfo> changeStatusInfoList = new ArrayList<>();
                    for (Object canChangeTo : changeToList) {
                        HashMap<String, Object> statusInfoMap = (HashMap<String, Object>) canChangeTo;
                        ChangeStatusInfo changeStatusInfo = new ChangeStatusInfo();
                        changeStatusInfo.setName((String)statusInfoMap.get("name"));
                        changeStatusInfo.setCommentRequired((Boolean)statusInfoMap.get("comment_required"));
                        changeStatusInfoList.add(changeStatusInfo);
                    }
                    StatusInfo statusInfo = new StatusInfo();
                    statusInfo.setName(name);
                    statusInfo.setChangeList(changeStatusInfoList);
                    statusInfo.setOpen((Boolean)status.get("is_open"));
                    changes.put(name, statusInfo);
                }
                setStatusChanges(changes);
            }

            private void loadResolutionXml(HashMap<String, Object> fieldMap) {
                List values = Arrays.asList((Object[]) fieldMap.get("values"));
                BugResolutionChanges changes = new BugResolutionChanges();
                for (Object value : values) {
                    HashMap<String, Object> resolution = (HashMap<String, Object>) value;
                    String name = (String) resolution.get("name");
                    changes.add(name);
                }
                setResolutionValues(changes);
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
