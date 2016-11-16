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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.johnmh.util.Util;
import me.johnmh.util.Util.TaskListener;

public class Bug extends me.johnmh.boogdroid.general.Bug {

    @Override
    public void loadComments() {
        final BugzillaTask task = new BugzillaTask(product.getServer(), "Bug.comments", "'ids':[" + id + "]", new TaskListener() {
            List<me.johnmh.boogdroid.general.Comment> newList = new ArrayList<>();

            @Override
            public void doInBackground(final Object response) {
                if (product.getServer().isUseJson()) {
                    doJsonParse(response);
                } else {
                    doXmlParse(response);
                }
            }

            private void doJsonParse(Object response) {
                try {
                    final JSONObject object = new JSONObject(response.toString());
                    final JSONArray comments = object.getJSONObject("result").getJSONObject("bugs").getJSONObject(Integer.toString(id)).getJSONArray("comments");
                    final int size = comments.length();
                    for (int i = 0; i < size; ++i) {
                        JSONObject json = comments.getJSONObject(i);
                        if (i == 0) {
                            description = json.getString("text");
                        } else {
                            Comment comment = new Comment();
                            try {
                                comment.setId(json.getInt("id"));
                                comment.setText(json.getString("text"));

                                if (json.has("creator")) {
                                    comment.setAuthor(new User(json.getString("creator")));
                                } else {
                                    comment.setAuthor(new User(json.getString("author")));
                                }

                                if (json.has("creation_time")) {
                                    comment.setDate(Util.formatDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("creation_time")));
                                } else {
                                    comment.setDate(Util.formatDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("time")));
                                }

                                if (json.has("count")) {
                                    comment.setNumber(json.getInt("count"));
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            private void doXmlParse(Object response) {
                try {
                    Object bugs = ((HashMap<String, Object>) response).get("bugs");
                    Object objects = ((HashMap<String, Object>) bugs).get(Integer.toString(id));
                    Object[] comments = ((HashMap<String, Object[]>) objects).get("comments");
                    final int size = comments.length;
                    for (int i = 0; i < size; ++i) {
                        HashMap<String, Object> commentMap = (HashMap<String, Object>) comments[i];
                        if (i == 0) {
                            description = (String) commentMap.get("text");
                        } else {
                            Comment comment = new Comment();
                            comment.setBug(Bug.this);
                            try {
                                comment.setId((Integer)commentMap.get("id"));
                                comment.setText((String) commentMap.get("text"));

                                if (commentMap.get("creator") != null) {
                                    comment.setAuthor(new User((String) commentMap.get("creator")));
                                } else {
                                    comment.setAuthor(new User((String) commentMap.get("author")));
                                }

                                if (commentMap.get("creation_time") != null) {
                                    comment.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(commentMap.get("creation_time")));
                                } else {
                                    comment.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(commentMap.get("time")));
                                }

                                if (commentMap.get("count") != null) {
                                    comment.setNumber((Integer) commentMap.get("count"));
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                            newList.add(comment);
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
