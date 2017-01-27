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

package me.albfan.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader extends AsyncTask<Void, Void, Bitmap> {
    public static Map<String, Bitmap> cache = new HashMap<String, Bitmap>();

    private final String src;
    private final ImageView view;

    public static void loadImage(final String src, final ImageView view) {
        if (cache.containsKey(src)) {
            view.setImageBitmap(cache.get(src));
        } else {
            (new ImageLoader(src, view)).execute();
        }
    }

    private ImageLoader(final String src, final ImageView view) {
        this.src = src;
        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            final URL url = new URL(src);
            return BitmapFactory.decodeStream(url.openStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final Bitmap result) {
        if (result != null) {
            cache.put(src, result);
            view.setImageBitmap(result);
        }
    }
}
