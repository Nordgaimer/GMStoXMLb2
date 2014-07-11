package DataOperator;

import ConnectionManager.JDBC_Utils;
import FXMLControllers.ConnectionManager;
import FXMLControllers.MainFrameController;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;

/*
* Create xml string - fastest, but may have encoding issues
*/
public class XMLBuilder {

    private static ArrayList<Integer> docTypesChoosed = MainFrameController.listOfDocIDByTypes;

    public XMLBuilder() {
        QueryBuilder qb = new QueryBuilder();
    }

    /**
     * (HAS TEST CONNECTION)
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
        Connection con = DriverManager.getConnection(ConnectionManager.getConnParams);
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
        Connection con = DriverManager.getConnection(ConnectionManager.getConnParams);
        QueryBuilder qb = new QueryBuilder();
        ResultSet rs = con.createStatement().executeQuery(qb.buildQueryWithParams(docTableName));
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        //Checks if any data available
        if (rs.next()!=true){
            xml.append("NO DATA AVAILABLE");
        }
        while (rs.next()) {
            xml.append("<Row>\n");
            for (int j = 1; j <= colCount; j++) {
                String columnName = rsmd.getColumnName(j);
                String columnNameWithDesc = rsmd.getColumnName(j) + " desc='" + DocsLocalization.getRusLocalizationName(rsmd.getColumnName(j)) + "'";
                Object value = rs.getObject(j);
                xml.append("<" + columnNameWithDesc + ">");
                if (value != null) {
                    xml.append(value.toString().trim());
                    System.out.println("Загружаем файлы...");
                    xml.append("</" + columnName + ">\n");
                }
            }
            xml.append("<Row>\n");
        }
        return xml.toString();
    }
}




