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

package me.albfan.boogdroid.ui;

import java.util.List;

import me.albfan.boogdroid.R;
import me.albfan.boogdroid.general.Bug;
import me.albfan.boogdroid.general.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

public class AdapterBug extends ArrayContainsAdapter<Bug> {
    private final LayoutInflater inflater;
    private Filter filter;

    public AdapterBug(final Context context, final List<Bug> list) {
        super(context, R.layout.adapter_bug, list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_bug, parent, false);
            holder = new ViewHolder();
            holder.summary = (TextView) convertView.findViewById(R.id.summary);
            holder.creationDate = (TextView) convertView.findViewById(R.id.creation_date);
            holder.assignee = (TextView) convertView.findViewById(R.id.assignee);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Bug item = getItem(position);
        final int color;
        if (item.isOpen()) {
            color = getContext().getResources().getColor(R.color.adapter_red);
        } else {
            color = getContext().getResources().getColor(R.color.adapter_green);
        }
        String summary = "";
        if (item.getPriority() != null) {
            summary += "[" + item.getPriority() + "] ";
        }
        summary += item.getSummary();

        holder.summary.setTextColor(color);
        holder.summary.setText(summary);

        holder.creationDate.setText(item.getCreationDate());
        final User assignee = item.getAssignee();
        if (assignee != null) {
            holder.assignee.setText(assignee.name);
        } else {
            holder.assignee.setText("");
        }

        return convertView;
    }

    public int getBugIdFromPosition(final int position) {
        return getItem(position).getId();
    }

    private static class ViewHolder {
        TextView summary;
        TextView creationDate;
        TextView assignee;
    }
}
