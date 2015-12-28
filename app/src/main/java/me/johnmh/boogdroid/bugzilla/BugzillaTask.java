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

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import me.johnmh.boogdroid.general.Server;
import me.johnmh.util.Util.TaskListener;

public class BugzillaTask extends AsyncTask<Void, Void, Void> {
    private final String method;
    private String params;
    private String response;

    private TaskListener listener;

    private Server server;

    public BugzillaTask(final Server server, final String method, final TaskListener listener) {
        this.server = server;
        this.method = method;
        this.params = "";
        this.listener = listener;
    }

    public BugzillaTask(final Server server, final String method, final String params, final TaskListener listener) {
        this.server = server;
        this.method = method;
        this.params = params;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(final Void... p) {
        try {
            // Add login info if needed
            if (server.hasUser()) {
                if (params.length() > 0) {
                    params += ",";
                }
                params += "'Bugzilla_login':'" + server.getUser() + "','Bugzilla_password':'" + server.getPassword() + "'";
            }

            // Create the final array
            final JSONArray array;
            if (params.length() > 0) {
                array = new JSONArray("[{" + params + "}]");
            } else {
                array = new JSONArray();
            }

            // Create the request
            final JSONObject request = new JSONObject();
            request.put("id", UUID.randomUUID().hashCode());
            request.put("method", method);
            request.put("params", array);

            // Send the request
            //final HttpClient httpClient = new DefaultHttpClient();
            final HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
            final HttpPost httpPost = new HttpPost(server.getUrl() + "/jsonrpc.cgi");
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(request.toString()));
            HttpEntity entity = httpClient.execute(httpPost).getEntity();
            response = EntityUtils.toString(entity);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        listener.doInBackground(response);
        return null;
    }

    @Override
    protected void onPostExecute(final Void result) {
        listener.onPostExecute(response);
    }
}
