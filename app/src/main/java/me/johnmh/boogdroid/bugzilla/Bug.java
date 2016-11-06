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

import java.util.ArrayList;
import java.util.List;

import me.johnmh.boogdroid.general.*;
import me.johnmh.util.Util;
import me.johnmh.util.Util.TaskListener;

public class Bug extends me.johnmh.boogdroid.general.Bug {

    @Override
    protected void loadComments() {
        final Bug b = this;
        final List<me.johnmh.boogdroid.general.Comment> newList = new ArrayList<me.johnmh.boogdroid.general.Comment>();
        final BugzillaTask task = new BugzillaTask(product.getServer(), "Bug.comments", "'ids':[" + b.id + "]", new TaskListener() {
            @Override
            public void doInBackground(final Object response) {
                try {
                    final JSONObject object = new JSONObject(response.toString());
                    final JSONArray comments = object.getJSONObject("result").getJSONObject("bugs").getJSONObject(Integer.toString(b.id)).getJSONArray("comments");
                    final int size = comments.length();
                    for (int i = 0; i < size; ++i) {
                        if (i == 0) {
                            description = comments.getJSONObject(i).getString("text");
                        } else {
                            newList.add(new Comment(b, comments.getJSONObject(i)));
                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute(final Object response) {
                comments.clear();
                comments.addAll(newList);
                commentsListUpdated();
            }
        });
        task.execute();
    }
}
