package DataOperator;

import ConnectionManager.JDBC_Utils;
import FXMLControllers.ConnectionManager;
import FXMLControllers.MainFrameController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.sql.*;
import java.util.*;


public class QueryBuilder {


    private ArrayList<Integer> docTypesChoosed = MainFrameController.listOfDocIDByTypes;
    private LinkedList<String> selectedDates = MainFrameController.listOfPeriod;
    private ArrayList<Integer> selectedDocsByID = MainFrameController.listOfDocsSelectedByID;
    private int countOfSelectedDocTypes = MainFrameController.listOfDocIDByTypes.size();




    public String getDocName (String docCode) throws SQLException {
        Connection conn = DriverManager.getConnection(ConnectionManager.getConnParams);
        String query = "select DocName from z_Docs where DocCode="+docCode;
        Statement stat = null;
        ResultSet rs = null;
        String docName ="";

        HashMap<String,String> tableList = new HashMap<String,String>();

        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(query);

            while (rs.next()) {
                docName = rs.getString(1);
            }
            conn.commit();

            return docName;
        } catch (SQLException e) {
            System.out.println(e);

        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
        return  docName;
    }

    public ArrayList<String> getAllTableNames (String docCode) throws SQLException {
        ArrayList<String> tableNames = new ArrayList<String>();
        Connection conn = DriverManager.getConnection(ConnectionManager.getConnParams);
        String query = "select TableName from z_Tables where DocCode="+docCode;
        Statement stat = null;
        ResultSet rs = null;
        String tableName;
        HashMap<String,String> tableList = new HashMap<String,String>();

        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(query);

            while (rs.next()) {
                tableName = rs.getString(1);
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



//    public String generalSQLBuilder (){
//        String mainQuery="select * from where docid=";
//        String searchByDocID = "";
//
//        for ()
//
//        if (selectedDocsByID.isEmpty()==false)
//        {
//            searchByDocID = "DocID =";
//            for (Integer value: selectedDocsByID){
//            searchByDocID+=String.valueOf(value);
//            }
//
//        }
//
//
//        String query = ""
//
//            return null;
//    }

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
            ObservableMap<Integer, String> result = FXCollections.observableHashMap();
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

    /**
     * Returns
     */
    public static ObservableList<String> getDocsPreview(Connection connection, String previewFinalQuery) {
        Connection conn = connection;
        Statement stat = null;
        ResultSet rs = null;

        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(previewFinalQuery);
            ObservableMap<Integer, Integer> resultMap = FXCollections.observableHashMap();
            ArrayList<String> resultList = new ArrayList<String>();
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
            ObservableList<String> readyList = FXCollections.observableList(resultList);
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


}


