package be.thalarion.eventman.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class API {

    public static final String API_ROOT_URI = "http://events.restdesc.org/";

    /**
     * Singleton fields and methods
     */
    private static API instance;

    public static API getInstance() {
        if (instance == null) init(API_ROOT_URI);
        return instance;
    }

    public static void init(String root) {
        try {
            instance = new API(new URI(root));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Instance fields and methods
     */

    private URI root;
    // Model URI mapping cache
    private Map<String, URI> mapping;

    private API(URI root) {
        this.root = root;
    }

    /**
     * resolve - Resolve a resource to a URI
     *
     * @param resource
     * @return URI
     * @throws IOException, APIException
     */
    public URI resolve(String resource) throws IOException, APIException {
        if(this.mapping == null) this.mapping = new HashMap<>();

        if(this.mapping.containsKey(resource))
            return this.mapping.get(resource);

        JSONObject json = fetch(root);

        try {
            URI URI = new URI(json.getString(resource));
            this.mapping.put(resource, URI);
            return URI;
        } catch (URISyntaxException | JSONException e) {
            throw new APIException(e);
        }
    }

    /**
     * fetch - GET a resource
     *
     * @param URI
     * @return JSONObject
     * @throws IOException, APIException
     */
    public JSONObject fetch(URI URI) throws IOException, APIException {
        HttpURLConnection conn = (HttpURLConnection) URI.toURL().openConnection();

        conn.setRequestMethod("GET");
        conn.addRequestProperty("Accept", "application/json");
        conn.addRequestProperty("Accept-Charset", "utf-8");

        conn.connect();
        if (conn.getResponseCode() >= 400)
            throw new APIException(conn.getResponseMessage());

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        // I ain't been learning Java just to write C, did I?
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        try {
            return new JSONObject(sb.toString());
        } catch (JSONException e) {
            throw new APIException(e);
        } finally {
            conn.disconnect();
        }
    }

    /**
     * update - PUT/PATCH a resource
     *
     * @param URI
     * @param data
     * @throws IOException, APIException
     */
    public void update(URI URI, String data) throws IOException, APIException {
        HttpURLConnection conn = (HttpURLConnection) URI.toURL().openConnection();

        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.addRequestProperty("Content-Type", "application/json");

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(data);
        bw.close();

        if (conn.getResponseCode() >= 400) {
            try {
                throw new APIException(conn.getResponseMessage());
            } finally {
                conn.disconnect();
            }
        }
    }

    /**
     * create - POST a resource
     *
     * @param URI
     * @param data
     * @return JSONObject
     * @throws IOException, APIException
     */
    public JSONObject create(URI URI, String data) throws IOException, APIException {
        HttpURLConnection conn = (HttpURLConnection) URI.toURL().openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.addRequestProperty("Content-Type", "application/json");

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(data);
        bw.close();

        if (conn.getResponseCode() >= 400)
            throw new APIException(conn.getResponseMessage());

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        try {
            return new JSONObject(sb.toString());
        } catch (JSONException e) {
            throw new APIException(e);
        } finally {
            conn.disconnect();
        }
    }

    /**
     * delete - DELETE a resource
     *
     * @param URI
     * @throws IOException, APIException
     */
    public void delete(URI URI) throws IOException, APIException {
        HttpURLConnection conn = (HttpURLConnection) URI.toURL().openConnection();
        conn.setRequestMethod("DELETE");
        conn.setDoOutput(true);
        conn.addRequestProperty("Content-Type", "application/json");
        conn.connect();

        if (conn.getResponseCode() >= 400) {
            try {
                throw new APIException(conn.getResponseMessage());
            } finally {
                conn.disconnect();
            }
        }
    }
}
