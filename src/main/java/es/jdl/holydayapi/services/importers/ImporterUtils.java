package es.jdl.holydayapi.services.importers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Component
public class ImporterUtils {

    private ObjectMapper om = new ObjectMapper();

    public String getCsvURLFromRFD(String urlJson) throws IOException {
        URL url = new URL(urlJson);
        JsonNode tree = om.readTree(url.openStream());
        List<JsonNode> urls = tree.findValues("accessURL");
        String csvURL = null;
        for (JsonNode n: urls) {
            if (n.textValue().endsWith(".csv"))
                csvURL = n.textValue();
        }
        return csvURL;
    }

}
