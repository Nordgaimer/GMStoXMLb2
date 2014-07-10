package DataOperator;

import ConnectionManager.JDBC_Utils;
import FXMLControllers.ConnectionManager;
import FXMLControllers.MainFrameController;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/*
* Create xml string - fastest, but may have encoding issues
*/
public class XMLBuilder {

    private ArrayList<Integer> docTypesChoosed = MainFrameController.listOfDocIDByTypes;
    private LinkedList<String> selectedDates = MainFrameController.listOfPeriod;
    private ArrayList<Integer> selectedDocsByID = MainFrameController.listOfDocsSelectedByID;
    private int countOfSelectedDocTypes = MainFrameController.listOfDocIDByTypes.size();

    public static void main(String[] args) throws SQLException {

        XMLBuilder q = new XMLBuilder();
        q.docTypesChoosed.add(12001);
        q.docTypesChoosed.add(12002);
        q.docTypesChoosed.add(11012);

    }
    // Gets Document tables for selected DocTypes ID
    public HashMap<String,String> getDocTables (String docCodeConverted) throws SQLException {
        Connection conn = DriverManager.getConnection(ConnectionManager.getConnParams);
        String query = "select TableCode,TableName,TableDesc from z_Tables where DocCode="+docCodeConverted;
        Statement stat = null;
        ResultSet rs = null;
        String tableName;
        String tabbleDesc;
        int tableCode;
        HashMap<String,String> tableList = new HashMap<String,String>();

        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(query);

            while (rs.next()) {
                tableCode = rs.getInt(1);
                tableName = rs.getString(2);
                tabbleDesc = rs.getString(3);
                tableList.put(tableName, "<Table TableCode='" + String.valueOf(tableCode) + "' TableName='" + tableName + "' TableDesc='" + tabbleDesc + "'\n");
            }
            conn.commit();

            return tableList;
        } catch (SQLException e) {
            System.out.println(e);

        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
        return  tableList;
    }

    /**
     * (HAS TEST CONNECTION)
     * Standart JAVA API for XML Building, converts ResultSet to XML String.
     * @return XML String.
     * @throws SQLException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public String stringBuilderToXMLFormat() throws SQLException {
        QueryBuilder queryBuilder = new QueryBuilder();
        StringBuffer xml = new StringBuffer();
        Connection con = DriverManager.getConnection(ConnectionManager.getConnParams);
        xml.append("<GMSData>");
        for (int i=0;i<countOfSelectedDocTypes;i++){
            String docTypeCode = String.valueOf(docTypesChoosed.get(i));
            xml.append("<DocType DocCode='"+docTypeCode+"' DocName='"+queryBuilder.getDocName(docTypeCode)+"'>\n");
           //NEED TO ADD ALL TABLES
            xml.append("<Table TableName=");
            // HERE ADD ALL DATA FOR EACH TABLE
            ResultSet rs = con.createStatement().executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            while (rs.next())
            {
                xml.append("<Row>\n");

                for (int i = 1; i <= colCount; i++)
                {
                    String columnName = rsmd.getColumnName(i);
                    String columnNameWithDesc = rsmd.getColumnName(i)+" desc='"+DocsLocalization.getRusLocalizationName(rsmd.getColumnName(i))+"'";
                    Object value = rs.getObject(i);
                    xml.append("<" + columnNameWithDesc + ">");

                    if (value != null)
                    {
                        xml.append(value.toString().trim());
                    }
                    xml.append("</" + columnName + ">\n");
                }
                xml.append("</Row>\n");
            }

            //xml.append("</Results>\n");


        }
      return xml.toString();
    }


}
