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

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import me.johnmh.util.Util;
import me.johnmh.util.Util.TaskListener;

public class Product extends me.johnmh.boogdroid.general.Product {

    @Override
    protected void loadBugs() {
        final BugzillaTask task = new BugzillaTask(server, "Bug.search", "'product':'" + getName() + "', 'resolution':'', 'limit':0, 'include_fields':['id', 'summary', 'priority', 'status', 'creator', 'assigned_to', 'resolution', 'creation_time', 'resolution']", new TaskListener() {
            List<me.johnmh.boogdroid.general.Bug> newList = new ArrayList<me.johnmh.boogdroid.general.Bug>();

            @Override
            public void doInBackground(final Object response) {
                if(server.isUseJson()) {
                    doJsonParse(response);
                } else {
                    doXmlParse(response);
                }
            }

            private void doXmlParse(Object response) {
                try {
                    List bugs = Arrays.asList(((HashMap<String, Object[]>) response).get("bugs"));
                    final int size = bugs.size();
                    for (int i = 0; i < size; ++i) {
                        Bug bug = new Bug();
                        bug.setProduct(Product.this);
                        HashMap<String, Object> bugMap = (HashMap<String, Object>) bugs.get(i);
                        try {
                            bug.setId(Integer.parseInt(bugMap.get("id").toString()));
                            bug.setSummary(bugMap.get("summary").toString());
                            String creationTime;
                            creationTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(bugMap.get("creation_time"));
                            bug.setCreationDate(creationTime);
                            bug.setPriority(bugMap.get("priority").toString());
                            bug.setStatus(bugMap.get("status").toString());
                            bug.setResolution(bugMap.get("resolution").toString());
                            bug.setReporter(new User(bugMap.get("creator").toString()));
                            bug.setAssignee(new User(bugMap.get("assigned_to").toString()));
                            bug.setOpen(TextUtils.isEmpty(bugMap.get("resolution").toString()));
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }

                        newList.add(bug);
                    }
                    Collections.reverse(newList);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            private void doJsonParse(Object response) {
                try {
                    final JSONObject object = new JSONObject(response.toString());
                    final JSONArray bugs = object.getJSONObject("result").getJSONArray("bugs");
                    final int size = bugs.length();
                    for (int i = 0; i < size; ++i) {
                        Bug bug = new Bug();
                        JSONObject json = bugs.getJSONObject(i);
                        bug.setProduct(Product.this);
                        try {
                            bug.setId(json.getInt("id"));
                            bug.setSummary(json.getString("summary"));
                            bug.setCreationDate(Util.formatDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("creation_time")));
                            bug.setPriority(json.getString("priority"));
                            bug.setStatus(json.getString("status"));
                            bug.setResolution(json.getString("resolution"));
                            bug.setReporter(new User(json.getString("creator")));
                            bug.setAssignee(new User(json.getString("assigned_to")));
                            bug.setOpen(TextUtils.isEmpty(json.getString("resolution")));
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }

                        newList.add(bug);
                    }
                    Collections.reverse(newList);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute(final Object response) {
                clearBugs();
                addBugs(newList);
                bugsListUpdated();
            }
        });
        task.execute();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }
}
