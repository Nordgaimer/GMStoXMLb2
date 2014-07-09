package DataOperator;

import ConnectionManager.JDBC_Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QueryBuilder {
    /**
     * Loads DocCode and DocName from z_Docs and put it in Collection HashMap for further usage.
     */
    public static ObservableMap<Integer, String> getDocsCatalog(Connection connection) {
        Connection conn = connection;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery("select DocCode,DocName from z_Docs where DocCatCode=1");
            ObservableMap<Integer,String> result = FXCollections.observableHashMap();
            while (rs.next()) {
                int DocCode = rs.getInt(1);
                String DocName = rs.getString(2);
                result.put(DocCode, DocName);
            }
            conn.commit();
            return result;


        } catch (SQLException e) {
            JDBC_Utils.rollbackQuietly(conn);
        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
        return null;
    }


    public List<String> generalQueryBuilder (ArrayList <Integer> docCodes){
        return null;
    }

    /**
     * Returns
     */
    public static ObservableList<String> getDocsPreview(Connection connection,String previewFinalQuery) {
        Connection conn = connection;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(previewFinalQuery);
            ObservableMap<Integer,Integer> resultMap = FXCollections.observableHashMap();
            ArrayList <String> resultList = new ArrayList<String>();
            while (rs.next()) {
                int docID = rs.getInt(1);
                int docDate = rs.getInt(2);
                resultMap.put(docID, docDate);
            }
            //Converting HashMap to ArrayList
            for (Map.Entry<Integer, Integer> entry : resultMap.entrySet()) {
                String firstStr = String.valueOf(entry.getKey());
                resultList.add(firstStr);
                String secondStr = String.valueOf(entry.getValue());
                resultList.add(secondStr);
            }
            ObservableList <String> readyList = FXCollections.observableList(resultList);
            conn.commit();

            return readyList;
        } catch (SQLException e) {
            JDBC_Utils.rollbackQuietly(conn);
        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
        return null;
    }

    /**
     * Building query for preview (Looking for all document tables in database and gets a document header table name)
     */
    public static String getQueryForDocumentPreview (Connection connection,int docCode) throws SQLException {
        String SQLFilterQuery ="select TOP 1 TableName from z_Tables where DocCode ="+docCode+" ORDER BY TableCode ASC";

        Connection conn = connection;
        Statement stat = null;
        ResultSet rs = null;
        String tableName ="";
        //Getting table name from selected document type.
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(SQLFilterQuery);
            while (rs.next()){
                tableName = rs.getString(1);
            }
            conn.commit();
            return "select DocID, DocDate from "+tableName;
        } catch (SQLException e) {
            System.out.println(e);

        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
        return "";
    }

}


