package es.jdl.holydayapi.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class ServicesUtils {
    /**
     * Lee linea a linea ¿util o mejor getContent?
     * @param url
     * @return
     * @throws IOException
     */
    public static String getURLContent(String url) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader((new URL(url)).openStream()));
        StringBuffer text = new StringBuffer();
        String line;

        while ((line = reader.readLine()) != null) {
            text.append(line);
        }
        reader.close();
        return text.toString();
    }

    /**
     * Super simple POST get content. It passes params in x-www-form-urlencoded, UTF-8 forced charset
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static InputStream getPostContentStream(String url, Map<String, Object> params) throws IOException {
        URL postUrl = new URL(url);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection)postUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        return conn.getInputStream();
    }

    public static String getPostContent(String url, Map<String, Object> params) throws IOException {
        Reader in = new BufferedReader(new InputStreamReader(getPostContentStream(url, params), "UTF-8"));

        StringBuffer sb = new StringBuffer();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        in.close();
        return sb.toString();
    }

}
