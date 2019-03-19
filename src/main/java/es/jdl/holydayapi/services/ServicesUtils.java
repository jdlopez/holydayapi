package es.jdl.holydayapi.services;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.objectify.Ref;
import es.jdl.holydayapi.config.GsonRefSerializer;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class ServicesUtils {

    private static Gson gson = new GsonBuilder()
            .setDateFormat("dd/MM/yyyy HH:mm:ss").registerTypeAdapter(Ref.class, new GsonRefSerializer()).create();

    /** escribe el objeto pasado en formato JSON como respuesta HTTP */
    public static void writeJSONResponse(HttpServletResponse resp, Object respObj) throws IOException {
        if (respObj == null)
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Result is NULL");
        else {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.print(gson.toJson(respObj));
            writer.flush();
        }
    }

    /**
     * Gestion basica de errores para servicios REST
     * @param resp
     * @param cause
     * @throws IOException
     */
    public static void returnException(HttpServletResponse resp, Throwable cause) throws IOException {
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "{ \"error\": \"" + (
                cause.getMessage() + cause.getCause()==null?"":
                " caused by: " + cause.getCause().getMessage()) +
                "\"}");
    }

    /**
     * Lee linea a linea ¿util o mejor getContent?
     * @param url
     * @return
     * @throws IOException
     */
    public static String getURLContent(String url) throws IOException {
        URLFetchService urlFetch = URLFetchServiceFactory.getURLFetchService();
        HTTPResponse response = urlFetch.fetch(new URL(url));
        return new String(response.getContent());
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
        URLFetchService urlFetch = URLFetchServiceFactory.getURLFetchService();
        HTTPRequest request = new HTTPRequest(postUrl, HTTPMethod.POST);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        request.setHeader(new HTTPHeader("Content-Type", "application/x-www-form-urlencoded"));
        request.setHeader(new HTTPHeader("Content-Length", String.valueOf(postDataBytes.length)));
        request.setPayload(postDataBytes);
        HTTPResponse response = urlFetch.fetch(request);

        return new ByteArrayInputStream(response.getContent());
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
