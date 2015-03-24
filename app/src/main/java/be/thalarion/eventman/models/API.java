package be.thalarion.eventman.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class API {

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
     * @return URL
     * @throws IOException, APIException
     */
    public URL resolve(String resource) throws IOException, APIException {
        JSONObject json = fetch(root);

        try {
            return new URL(json.getString(resource));
        } catch (JSONException e) {
            throw new APIException(e);
        }
    }

    /**
     * fetch - GET a resource
     * @param url
     * @return JSONObject
     * @throws IOException, APIException
     */
    public JSONObject fetch(URL url) throws IOException, APIException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.addRequestProperty("Accept", "application/json");
        conn.addRequestProperty("Accept-Charset", "utf-8");

        conn.connect();
        if(conn.getResponseCode() >= 400)
            throw new APIException(conn.getResponseMessage());

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        // I ain't been learning Java just to write C, did I?
        while((line = br.readLine()) != null){
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
     * @param url
     * @param data
     * @throws IOException, APIException
     */
    public void update(URL url, String data) throws IOException, APIException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.addRequestProperty("Content-Type", "application/json");

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(data);
        bw.close();

        if(conn.getResponseCode() >= 400)
            throw new APIException(conn.getResponseMessage());

        conn.disconnect();
    }

    /**
     * create - POST a resource
     * @param url
     * @param data
     * @throws IOException, APIException
     */
    public void create(URL url, String data) throws IOException, APIException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.addRequestProperty("Content-Type", "application/json");

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(data);
        bw.close();

        if(conn.getResponseCode() >= 400)
            throw new APIException(conn.getResponseMessage());

        conn.disconnect();
    }

    /**
     * delete - DELETE a resource
     * @param url
     * @param data
     * @throws IOException, APIException
     */
    public void delete(URL url, String data) throws IOException, APIException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("DELETE");
        conn.setDoOutput(true);
        conn.addRequestProperty("Content-Type", "application/json");
        conn.connect();

        if(conn.getResponseCode() >= 400)
            throw new APIException(conn.getResponseMessage());

        conn.disconnect();
    }
}
