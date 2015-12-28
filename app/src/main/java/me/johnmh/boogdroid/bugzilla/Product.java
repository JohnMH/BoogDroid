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
import java.util.Collections;
import java.util.List;

import me.johnmh.util.Util.TaskListener;

public class Product extends me.johnmh.boogdroid.general.Product {
    public Product(final me.johnmh.boogdroid.general.Server server, final JSONObject json) {
        super(server);
        createFromJSON(json);
    }

    @Override
    protected void loadBugs() {
        final Product p = this;
        final List<me.johnmh.boogdroid.general.Bug> newList = new ArrayList<me.johnmh.boogdroid.general.Bug>();
        final BugzillaTask task = new BugzillaTask(server, "Bug.search", "'product':'" + p.getName() + "', 'resolution':'', 'limit':0, 'include_fields':['id', 'summary', 'priority', 'status', 'creator', 'assigned_to', 'resolution', 'creation_time']", new TaskListener() {
            @Override
            public void doInBackground(final String s) {
                try {
                    final JSONObject object = new JSONObject(s);
                    final JSONArray bugs = object.getJSONObject("result").getJSONArray("bugs");
                    final int size = bugs.length();
                    for (int i = 0; i < size; ++i) {
                        newList.add(new Bug(p, bugs.getJSONObject(i)));
                    }
                    Collections.reverse(newList);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute(final String s) {
                p.clearBugs();
                p.addBugs(newList);
                bugsListUpdated();
            }
        });
        task.execute();
    }

    private void createFromJSON(final JSONObject json) {
        try {
            id = json.getInt("id");
            name = json.getString("name");
            description = json.getString("description");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
