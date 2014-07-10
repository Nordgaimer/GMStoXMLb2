package DataOperator;

import ConnectionManager.JDBC_Utils;
import FXMLControllers.ConnectionManager;

import java.sql.*;

/**
 * Created by Елена on 06.07.2014.
 */
public class DocsLocalization {


    public static String getRusLocalizationName (String fieldName) throws SQLException {
        String query = "select FieldDesc from z_FieldsRep where FieldName='"+fieldName+"'";
        Connection conn = DriverManager.getConnection(ConnectionManager.getConnParams);
        Statement stat = null;
        ResultSet rs = null;
        String fieldDesc ="";
        //Getting russian desc for selected fieldname.
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(query);
            while (rs.next()){
                fieldDesc = rs.getString(1);
            }
            conn.commit();
            return fieldDesc;
        } catch (SQLException e) {
            System.out.println(e);

        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
        return "No desc";

    }
}
