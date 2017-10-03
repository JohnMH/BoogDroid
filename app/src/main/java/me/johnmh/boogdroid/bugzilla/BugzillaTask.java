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

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import me.johnmh.boogdroid.general.Server;
import me.johnmh.boogdroid.ui.Application;
import me.johnmh.util.Util.TaskListener;

public class BugzillaTask extends AsyncTask<Void, Void, Void> {
    private final String method;
    private String params;
    private Object response;

    private TaskListener listener;

    private Server server;
    private Context context;

    public BugzillaTask(final Server server, final String method, final TaskListener listener) {
        this(server, method, "", listener);
    }

    public BugzillaTask(final Server server, final String method, final String params, final TaskListener listener) {
        this.server = server;
        this.method = method;
        this.params = params;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(final Void... p) {
        if (server.isUseJson()) {
           return doJsonImplementation();
        } else {
            return doXmlImplementation();
        }
    }

    @Nullable
    private Void doXmlImplementation() {
        XMLRPCClient client = null;
        try {
            String url = server.getUrl();
            if (!url.startsWith("http")) {
                url = "https://"+url;
            }
            if (!url.endsWith("/xmlrpc.cgi")) {
                url = url + "/xmlrpc.cgi";
            }
            client = new XMLRPCClient(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Map<String, Object> args = null;
        try {
            if (params == null || params.equals("")){
                args = new HashMap<>();
            } else {
                args = jsonToMap(new JSONObject("{"+params+"}"));
            }
            args.put("Bugzilla_login", server.getUser());
            args.put("Bugzilla_password", server.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            response = client.call(method, args == null ? null : new Object[]{args});
        } catch (XMLRPCException e) {
            Toast.makeText(Application.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

        listener.doInBackground(response);
        return null;
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    @Nullable
    private Void doJsonImplementation() {
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
            final HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
            final HttpPost httpPost = new HttpPost(server.getUrl() + "/jsonrpc.cgi");
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(request.toString()));
            HttpEntity entity = httpClient.execute(httpPost).getEntity();
            response = EntityUtils.toString(entity);
        } catch (final Exception e) {
            Toast.makeText(Application.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        listener.doInBackground(response);
        return null;
    }

    @Override
    protected void onPostExecute(final Void result) {
        listener.onPostExecute(response);
    }
}
