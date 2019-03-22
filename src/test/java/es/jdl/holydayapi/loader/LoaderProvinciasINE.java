package es.jdl.holydayapi.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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

public class LoaderProvinciasINE {

    public static void main (String[] args) throws Exception {
        //String url = "http://www.ine.es/daco/daco42/codmun/cod_ccaa_provincia.htm";
        //
        Workbook workbook = new XSSFWorkbook(
                new URL("https://administracionelectronica.gob.es/ctt/resources/Soluciones/238/Descargas/Catalogo%20de%20Provincias.xlsx?idIniciativa=238&idElemento=427").openStream());
        Sheet sheet = workbook.getSheetAt(0);
        List<Map<String, String>> provincias = new ArrayList<>();
        for (int rowNum = 3; rowNum < sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            HashMap<String, String> prov = new HashMap<>();
            prov.put("id", safeToString(row.getCell(1)));
            prov.put("nm", row.getCell(2).toString().trim());
            prov.put("codCA", safeToString(row.getCell(3))); // quitar decimales
            provincias.add(prov);
        }
        workbook.close();
        System.out.println("Convierte " + provincias.size() + " provincias");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(provincias);
        FileWriter fw = new FileWriter("./data/provincias.json");
        fw.write(json);
        fw.close();

    }

    // numeros sin decimales, cadenas tal cual
    private static String safeToString(Cell cell) {
        if (cell.getCellType().equals(CellType.NUMERIC) )
            return String.valueOf( (int)cell.getNumericCellValue() );
        else
            return cell.toString();
    }
}
