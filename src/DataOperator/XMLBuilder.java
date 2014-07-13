package DataOperator;

import ConnectionManager.ErrorMsge;
import FXMLControllers.ConnectionController;
import FXMLControllers.MainFrameController;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import java.sql.*;
import java.util.ArrayList;

/*
* Create xml string - fastest, but may have encoding issues
*/
public class XMLBuilder {
    public static boolean isAnyDataAvailable;
    private static ArrayList<Integer> docTypesChoosed = MainFrameController.listOfDocIDByTypes;
    public static ArrayList<String> missingDataTables = new ArrayList<>();

    public XMLBuilder() throws SQLException {
        QueryBuilder qb = new QueryBuilder();
        DocsLocalization localization = new DocsLocalization();
        localization.getRusLocalizationNames();
    }

    /**
     * Standart JAVA API for XML Building, converts ResultSet to XML String.
     *
     * @return XML String.
     * @throws SQLException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public String stringBuilderToXMLFormat() throws SQLException {
        QueryBuilder queryBuilder = new QueryBuilder();
        StringBuffer xml = new StringBuffer();
        Connection con = DriverManager.getConnection(ConnectionController.getConnParams);
        xml.append("<GMSData>\n");
        //Get Document type row 1 by 1
        for (int i = 0; i < docTypesChoosed.size(); i++) {
            String docTypeCode = String.valueOf(docTypesChoosed.get(i));
            ArrayList<String> listOfTables = queryBuilder.getAllTableNames(docTypeCode);
            xml.append("<DocType DocCode='" + docTypeCode + "' DocName='" + queryBuilder.getDocName(docTypeCode) + "'>\n");

            for (int k = 0; k < listOfTables.size(); k++) {
                xml.append("<Table TableName='" + listOfTables.get(k) + "' TableCode='" + queryBuilder.getTableCode(listOfTables.get(k)) +
                        "' TableDesc='" + queryBuilder.getTableDesc(listOfTables.get(k)) + "'>\n");
                xml.append(getRowData(listOfTables.get(k)));
                xml.append("</Table>\n");
            }
            xml.append("</DocType>\n");
        }
    xml.append("</GMSData>\n");
    return xml.toString();
}

    /**
     * Generating XML string, reads all documents data from particular table
     *
     * @param docTableName - Particular document table name
     * @return - String in XML format.
     * @throws SQLException
     */
    public String getRowData(String docTableName) throws SQLException {
        StringBuffer xml = new StringBuffer();
        Connection con = DriverManager.getConnection(ConnectionController.getConnParams);
        QueryBuilder qb = new QueryBuilder();
        ResultSet rs = con.createStatement().executeQuery(qb.buildQueryWithParams(docTableName));
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        //Checks if any data available
        if (rs.next() == false) {
            isAnyDataAvailable = false;
            missingDataTables.add(DocsLocalization.getRusName(docTableName));
        } else {
            isAnyDataAvailable = true;
        }

        while (rs.next()) {
            xml.append("<Row>\n");
            for (int j = 1; j <= colCount; j++) {
                String columnName = rsmd.getColumnName(j);
                String columnNameWithDesc = rsmd.getColumnName(j) + " desc='" + DocsLocalization.getRusName(rsmd.getColumnName(j)) + "'";
                Object value = rs.getObject(j);
                xml.append("<" + columnNameWithDesc + ">");
                if (value != null) {
                    xml.append(value.toString().trim());
                    xml.append("</" + columnName + ">\n");
                }
            }
            xml.append("<Row>\n");
        }
        System.out.println(xml.toString());
        return xml.toString();
    }
}




