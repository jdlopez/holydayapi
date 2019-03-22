package es.jdl.holydayapi.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoaderMunicipiosINE {

    public static void main (String[] args) throws Exception {
        //
        Workbook workbook = new XSSFWorkbook(new URL("http://www.ine.es/daco/daco42/codmun/codmun19/19codmun.xlsx").openStream());
        Sheet sheet = workbook.getSheetAt(0);
        List<Map<String, String>> municipios = new ArrayList<>();
        for (int rowNum = 2; rowNum < sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            HashMap<String, String> muni = new HashMap<>();
            muni.put("cpro", row.getCell(1).toString());
            muni.put("id", row.getCell(2).toString());
            muni.put("nm", row.getCell(4).toString());
            municipios.add(muni);
        }
        workbook.close();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(municipios);
        FileWriter fw = new FileWriter("./data/municipios.json");
        fw.write(json);
        fw.close();


    }
}
