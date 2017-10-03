/*
 *  BugsControl
 *  Copyright (C) 2014  Jon Ander Peñalba
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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import me.johnmh.boogdroid.R;
import me.johnmh.boogdroid.bugzilla.BugzillaTask;
import me.johnmh.boogdroid.general.Bug;
import me.johnmh.boogdroid.general.Server;
import me.johnmh.boogdroid.general.User;

import me.johnmh.util.ImageLoader;
import me.johnmh.util.Util;


public class BugInfoFragment extends ListFragment {
    private Bug bug;

    private View mainView;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_bug, container, false);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        activity.setSupportProgressBarIndeterminateVisibility(true);

        final Bundle arguments = getArguments();
        final int serverPos;
        final int productId;
        final int bugId;
        if (arguments != null) {
            serverPos = arguments.getInt("server_position", -1);
            productId = arguments.getInt("product_id", -1);
            bugId = arguments.getInt("bug_id", -1);
        } else {
            serverPos = -1;
            productId = -1;
            bugId = -1;
        }

        if (serverPos == -1 || productId == -1 || bugId == -1) {
            Toast.makeText(activity, R.string.invalid_bug, Toast.LENGTH_SHORT).show();
            activity.onBackPressed();
            return view;
        }

        bug = Server.servers.get(serverPos).getBugFromId(bugId);

        mainView = inflater.inflate(R.layout.bug_info, null, false);
        updateView();

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        ListView listView = getListView();
        listView.addHeaderView(mainView);

        final AdapterComment adapter = new AdapterComment(activity, bug.getComments());


        EditText editCommentFilter = (EditText) mainView.findViewById(R.id.editCommentFilter);
        editCommentFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        View footer = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_comment, null, false);

        final EditText editComment = (EditText)footer.findViewById(R.id.editComment);
        editComment.setText("");
        Button addComment = (Button)footer.findViewById(R.id.addComment);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = JSONObject.quote(editComment.getText().toString());
                final Server server = bug.getProduct().getServer();
                final BugzillaTask task = new BugzillaTask(server, "Bug.add_comment", "'id':" + bug.getId() +", 'comment': "+comment, new Util.TaskListener() {

                    @Override
                    public void doInBackground(final Object response) {
                        if (server.isUseJson()) {
                            doJsonParse(response);
                        } else {
                            doXmlParse(response);
                        }
                    }

                    private void doJsonParse(Object response) {
                        //TODO: It returns the new comment id. So you could only reload that one
                        try {
                            System.out.println(response);
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }

                    private void doXmlParse(Object response) {
                        //TODO: It returns the new comment id. So you could only reload that one
                        try {
                            System.out.println(response);
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPostExecute(final Object response) {
                        updateView();
                        bug.loadComments();
                    }
                });
                task.execute();
            }
        });
        listView.addFooterView(footer);

        setListAdapter(adapter);
        bug.setAdapterComment(adapter, activity, this);
    }

    public void updateView() {
        final User reporter = bug.getReporter();
        if (!TextUtils.isEmpty(reporter.avatarUrl)) {
            ImageLoader.loadImage(reporter.avatarUrl, (ImageView) mainView.findViewById(R.id.reporter_img));
        } else {
            ImageLoader.loadImage("http://www.gravatar.com/avatar/" + Util.md5(reporter.email), (ImageView) mainView.findViewById(R.id.reporter_img));
        }
        final User assignee = bug.getAssignee();
        if (assignee != null) {
            if (!TextUtils.isEmpty(assignee.avatarUrl)) {
                ImageLoader.loadImage(assignee.avatarUrl, (ImageView) mainView.findViewById(R.id.assignee_img));
            } else {
                ImageLoader.loadImage("http://www.gravatar.com/avatar/" + Util.md5(assignee.email), (ImageView) mainView.findViewById(R.id.assignee_img));
            }
        }

        TextView textCreationDate = (TextView) mainView.findViewById(R.id.creation_date);
        textCreationDate.setText(bug.getCreationDate());

        TextView textSummary = (TextView) mainView.findViewById(R.id.summary);
        textSummary.setText(bug.getSummary());

        TextView textReporter = (TextView) mainView.findViewById(R.id.reporter);
        textReporter.setText(bug.getReporter().name);
        if (assignee != null) {
            TextView textAssignee = (TextView) mainView.findViewById(R.id.assignee);
            textAssignee.setText(assignee.name);
        }

        TextView textPriority = (TextView) mainView.findViewById(R.id.priority);
        textPriority.setText(bug.getPriority());
        TextView textStatus = (TextView) mainView.findViewById(R.id.status);
        textStatus.setText(bug.getStatus());
        TextView textResolution = (TextView) mainView.findViewById(R.id.resolution);
        textResolution.setText(bug.getResolution());

        TextView textDescription = (TextView) mainView.findViewById(R.id.description);
        textDescription.setText(bug.getDescription());
    }
}
