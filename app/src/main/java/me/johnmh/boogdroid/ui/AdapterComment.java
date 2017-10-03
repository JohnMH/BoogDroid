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

package me.johnmh.boogdroid.ui;

import java.util.List;

import me.johnmh.boogdroid.R;
import me.johnmh.boogdroid.general.Comment;
import me.johnmh.boogdroid.general.User;
import me.johnmh.util.ImageLoader;
import me.johnmh.util.Util;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterComment extends ArrayContainsAdapter<Comment> {
    private LayoutInflater inflater;

    public AdapterComment(final Context context, final List<Comment> list) {
        super(context, R.layout.adapter_comment, list);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_comment, parent, false);
            holder = new ViewHolder();
            holder.creator = (TextView) convertView.findViewById(R.id.creator);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.bugNumber = (TextView) convertView.findViewById(R.id.bug_number);
            holder.authorImg = (ImageView) convertView.findViewById(R.id.author_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Comment item = getItem(position);

        holder.creator.setText(item.getAuthor().name);
        holder.text.setText(item.getText());
        holder.date.setText(item.getDate());
        holder.bugNumber.setText(new StringBuilder().append("#").append((item.getNumber() > 0) ? item.getNumber() : (position + 1)).toString());

        final User author = item.getAuthor();
        if (!TextUtils.isEmpty(author.avatarUrl)) {
            ImageLoader.loadImage(author.avatarUrl, holder.authorImg);
        } else {
            ImageLoader.loadImage("http://www.gravatar.com/avatar/" + Util.md5(author.email), holder.authorImg);
        }

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(final int position) {
        return false;
    }

    private static class ViewHolder {
        TextView creator;
        TextView text;
        TextView date;
        TextView bugNumber;
        ImageView authorImg;
    }
}
