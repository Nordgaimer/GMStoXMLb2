package DataOperator;

import ConnectionManager.JDBC_Utils;
import FXMLControllers.ConnectionController;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DocsLocalization {

    private static HashMap<String,String> mapOfNames = new HashMap<String, String>();

    /**
     * Get from database all russian descriptions for every field and loads them to HashMap.
     * @throws SQLException
     */
    public void getRusLocalizationNames () throws SQLException {
        String query = "select FieldName,FieldDesc from z_FieldsRep";
        Connection conn = DriverManager.getConnection(ConnectionController.getConnParams);
        Statement stat = null;
        ResultSet rs = null;
        String fieldDesc ="";
        String fieldName ="";
        //Getting russian desc for selected fieldname.
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(query);
            while (rs.next()){
                fieldName = rs.getString(1);
                fieldDesc = rs.getString(2);
                mapOfNames.put(fieldName,fieldDesc);
            }
            conn.commit();

        } catch (SQLException e) {
            System.out.println(e);

        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
    }

    //Searching in HashMap field name description in rus.
    public static String getRusName(String fieldName) throws SQLException {
        String desc="";
        for(Map.Entry<String, String> e : mapOfNames.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();

            if (fieldName.equals(key)){
                desc=value;
            }
        }
        return desc;
    }
}
