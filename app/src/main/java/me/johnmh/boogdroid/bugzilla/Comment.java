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

import org.json.JSONObject;

import me.johnmh.util.Util;

public class Comment extends me.johnmh.boogdroid.general.Comment {
    public Comment(final Bug bug, final JSONObject json) {
        super(bug);
        createFromJSON(json);
    }

    private void createFromJSON(final JSONObject json) {
        try {
            id = json.getInt("id");
            text = json.getString("text");

            if (json.has("creator")) {
                author = new User(json.getString("creator"));
            } else {
                author = new User(json.getString("author"));
            }

            if (json.has("creation_time")) {
                date = Util.formatDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("creation_time"));
            } else {
                date = Util.formatDate("yyyy-MM-dd'T'HH:mm:ss'Z'", json.getString("time"));
            }

            if (json.has("count")) {
                number = json.getInt("count");
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
