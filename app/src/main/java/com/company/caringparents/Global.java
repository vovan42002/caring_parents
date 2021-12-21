package com.company.caringparents;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


public class Global {
    public static String password;
    public static String email;
    public static Long id;

    public static String ip = "http://192.168.0.4:8080";

    public static String child_name;
    public static Long child_id;

    public static JSONArray methodGet(String url) {
        System.out.println(url);
        JSONArray jsonArray = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.connect();

            BufferedReader br;
            System.out.println("Response code = " + connection.getResponseCode() + " message = " + connection.getResponseMessage());
            if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            br.close();
            if (sb.toString() != null || sb.toString() != "") {
                try {
                    jsonArray = new JSONArray(sb.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonArray;
            }

        } catch (ProtocolException protocolException) {
            protocolException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }
}





