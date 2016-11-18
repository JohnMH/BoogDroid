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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

import me.johnmh.boogdroid.R;
import me.johnmh.boogdroid.bugzilla.BugzillaTask;
import me.johnmh.boogdroid.bugzilla.ChangeStatusInfo;
import me.johnmh.boogdroid.general.Bug;
import me.johnmh.boogdroid.general.BugResolutionChanges;
import me.johnmh.boogdroid.general.StatusInfo;
import me.johnmh.boogdroid.general.Server;
import me.johnmh.util.Util;


public class BugStatusFragment extends Fragment {
    private Bug bug;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.bug_status, null, false);

        final ActionBarActivity activity = (ActionBarActivity) getActivity();

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

        final Spinner spinnerStatus = setupStatusSpinner(view);

        final Spinner spinnerResolution = setupSpinnerResolution(view);

        Button saveButton = (Button)view.findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeStatusInfo changeStatusInfo = (ChangeStatusInfo) spinnerStatus.getSelectedItem();
                StatusInfo statusInfo = bug.getProduct().getServer().findStatusInfo(changeStatusInfo.getName());
                String resolution = (String)spinnerResolution.getSelectedItem();
                if (!statusInfo.isOpen() && "".equals(resolution)) {
                    Toast.makeText(activity, R.string.invalid_change_status, Toast.LENGTH_SHORT).show();
                    return;
                }
                EditText statusChangeComment = (EditText)view.findViewById(R.id.statusChangeComment);
                String comment = statusChangeComment.getText().toString();
                if(changeStatusInfo.isCommentRequired() && (comment.trim().isEmpty())) {
                    Toast.makeText(activity, R.string.invalid_change_status_comment_required, Toast.LENGTH_SHORT).show();
                }
                //TODO: Accept object as params
                new BugzillaTask(bug.getProduct().getServer(), "Bug.update", "ids:["+bug.getId()+"], status:'"
                        + changeStatusInfo +"', resolution:'"+ resolution+"'", new Util.TaskListener() {
                        @Override
                        public void doInBackground(Object response) {
                            //TODO: Check if everything was correct
                        }

                        @Override
                        public void onPostExecute(Object response) {

                        }
                    }).execute();
                }
            }
        );
        return view;
    }

    @NonNull
    private Spinner setupSpinnerResolution(View view) {
        Spinner spinnerResolution = (Spinner) view.findViewById(R.id.resolution);
        BugResolutionChanges resolutionValues = bug.getProduct().getServer().getResolutionValues();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext()
                , android.R.layout.simple_spinner_item, resolutionValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerResolution.setAdapter(adapter);
        spinnerResolution.setSelection(adapter.getPosition(bug.getResolution()));
        return spinnerResolution;
    }

    @NonNull
    private Spinner setupStatusSpinner(View view) {
        Spinner spinnerStatus = (Spinner) view.findViewById(R.id.status);
        String statusName = bug.getStatus();
        Server server = bug.getProduct().getServer();
        StatusInfo statusInfo = server.getStatusChanges().get(statusName);
        ArrayAdapter<ChangeStatusInfo> adapter = new ArrayAdapter<>(getActivity().getBaseContext()
                , android.R.layout.simple_spinner_item, statusInfo.getChangeList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
        spinnerStatus.setSelection(adapter.getPosition(server.findChangeStatusInfo(statusName)));
        return spinnerStatus;
    }
}
