package be.thalarion.eventman.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// TODO: error handling EVERYWHERE
public class API {

    // TODO: retrieve this from a config file?
    public static final String API_ROOT_URL = "http://events.restdesc.org/";

    /**
     * Singleton fields and methods
     */
    private static API instance;

    public static API getInstance() {
        if(instance == null) init(API_ROOT_URL);
        return instance;
    }

    public static void init(String root) {
        try {
            instance = new API(new URL(root));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Instance fields and methods
     */

    private URL root;

    private API(URL root) {
        this.root = root;
    }

    /**
     * resolve - Resolve a resource to a URL
     * @param resource
     * @return URL of resolved resource
     * @throws Exception
     */
    public URL resolve(String resource) throws Exception {
        JSONObject json = fetch(root);

        return new URL(json.getString(resource));
    }

    /**
     * fetch - Retrieve a resource
     * @param url
     * @return JSONObject
     */
    public JSONObject fetch(URL url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.addRequestProperty("Accept", "application/json");
            conn.addRequestProperty("Accept-Charset", "utf-8");

            try {
                conn.connect();
                if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){
                    // TODO: here
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                // Let's go C-style
                while((line = br.readLine()) != null){
                    sb.append(line);
                }
                br.close();

                try {
                    return new JSONObject(sb.toString());
                } catch (JSONException e) {
                    // TODO: here
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // TODO: here
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }

        } catch (IOException e) {
            // TODO: here
            e.printStackTrace();
        }

        return null;
    }

}
