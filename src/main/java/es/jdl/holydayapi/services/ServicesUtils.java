package es.jdl.holydayapi.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
}
