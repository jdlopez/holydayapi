package es.jdl.holydayapi.services.importers;

/**
 * Importer configuration
 */
public class ConfImport {

    private String dateFormat = "dd/MM/yyyy";
    private String charSet = "iso-8859-1";
    private String delimiter = ";";
    private int minColumns = 5;
    private int dayIndex = 0;
    private boolean checkHolydayRow;
    private int holydayRowIndex;
    private String holydayRowTest;
    private int typeColIndex;
    private String typeLocalTest;
    private String typeRegionalTest;
    private String typeCountryTest;
    private boolean checkGetLocal;
    private int cityIndex;
    private int skipRows = 1;
    private int nameIndex;
    private String defaultType;

    public HolydayType calcType(String[] row) {
        String s = row[typeColIndex] != null?row[typeColIndex].toLowerCase():"";
        if (getTypeLocalTest() != null && s.contains(getTypeLocalTest().toLowerCase()))
            return HolydayType.LOCAL;
        else if (getTypeRegionalTest() != null && s.contains(getTypeRegionalTest().toLowerCase()))
            return HolydayType.REGION;
        else if (getTypeCountryTest() != null && s.contains(getTypeCountryTest().toLowerCase()))
            return HolydayType.COUNTRY;
        else
            return null;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public int getMinColumns() {
        return minColumns;
    }

    public void setMinColumns(int minColumns) {
        this.minColumns = minColumns;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public boolean isCheckHolydayRow() {
        return checkHolydayRow;
    }

    public void setCheckHolydayRow(boolean checkHolydayRow) {
        this.checkHolydayRow = checkHolydayRow;
    }

    public int getHolydayRowIndex() {
        return holydayRowIndex;
    }

    public void setHolydayRowIndex(int holydayRowIndex) {
        this.holydayRowIndex = holydayRowIndex;
    }

    public String getHolydayRowTest() {
        return holydayRowTest;
    }

    public void setHolydayRowTest(String holydayRowTest) {
        this.holydayRowTest = holydayRowTest;
    }

    public int getTypeColIndex() {
        return typeColIndex;
    }

    public void setTypeColIndex(int typeColIndex) {
        this.typeColIndex = typeColIndex;
    }

    public String getTypeLocalTest() {
        return typeLocalTest;
    }

    public void setTypeLocalTest(String typeLocalTest) {
        this.typeLocalTest = typeLocalTest;
    }

    public String getTypeRegionalTest() {
        return typeRegionalTest;
    }

    public void setTypeRegionalTest(String typeRegionalTest) {
        this.typeRegionalTest = typeRegionalTest;
    }

    public String getTypeCountryTest() {
        return typeCountryTest;
    }

    public void setTypeCountryTest(String typeCountryTest) {
        this.typeCountryTest = typeCountryTest;
    }

    public boolean isCheckGetLocal() {
        return checkGetLocal;
    }

    public void setCheckGetLocal(boolean checkGetLocal) {
        this.checkGetLocal = checkGetLocal;
    }

    public int getCityIndex() {
        return cityIndex;
    }

    public void setCityIndex(int cityIndex) {
        this.cityIndex = cityIndex;
    }

    public int getSkipRows() {
        return skipRows;
    }

    public void setSkipRows(int skipRows) {
        this.skipRows = skipRows;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }
}
