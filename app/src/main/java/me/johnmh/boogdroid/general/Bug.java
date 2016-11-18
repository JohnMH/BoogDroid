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

package me.johnmh.boogdroid.general;

import android.support.v7.app.AppCompatActivity;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import me.johnmh.boogdroid.ui.BugInfoFragment;

public abstract class Bug {
    protected int id;
    protected boolean open;

    protected String summary;
    protected String priority = null;
    protected String status;
    protected String description;
    protected String creationDate;

    protected User reporter;
    protected User assignee = null;

    protected final List<Comment> comments = new ArrayList<Comment>();

    protected Product product;

    private BaseAdapter adapter;
    private AppCompatActivity activity;
    private BugInfoFragment fragment;
    private String resolution;

    public abstract void loadComments();

    public void setAdapterComment(final BaseAdapter adapter, final AppCompatActivity activity, final BugInfoFragment fragment) {
        this.adapter = adapter;
        this.activity = activity;
        this.fragment = fragment;

        activity.setSupportProgressBarIndeterminateVisibility(true);
        loadComments();
    }

    protected void commentsListUpdated() {
        adapter.notifyDataSetChanged();
        activity.setSupportProgressBarIndeterminateVisibility(false);
        fragment.updateView();
    }

    public int getId() {
        return id;
    }

    public boolean isOpen() {
        return open;
    }

    public String getSummary() {
        return summary;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public User getReporter() {
        return reporter;
    }

    public User getAssignee() {
        return assignee;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReporter(me.johnmh.boogdroid.bugzilla.User reporter) {
        this.reporter = reporter;
    }

    public void setAssignee(me.johnmh.boogdroid.bugzilla.User assignee) {
        this.assignee = assignee;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setProduct(me.johnmh.boogdroid.bugzilla.Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return summary;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
