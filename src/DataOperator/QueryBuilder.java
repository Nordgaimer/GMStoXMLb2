package DataOperator;

import ConnectionManager.JDBC_Utils;
import FXMLControllers.ConnectionController;
import FXMLControllers.MainFrameController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.sql.*;
import java.util.*;


public class QueryBuilder {


    private static LinkedList<String> selectedDates = MainFrameController.listOfPeriod;
    private static ArrayList<String> selectedDocsByID = MainFrameController.listOfDocsSelectedByID;
    private static  ArrayList<String> foundedTableWithDocDate = new ArrayList<>();
    private static int foundedTablesCount;

    /**
     * Returns query with params. like date and documents number (if exists)
     *
     * @return sql query with params
     */

    public String buildQueryWithParams(String docTableName) throws SQLException {
        String readyQuery="";
        //Checking if user select any particular document number
        if (selectedDocsByID.size()==0){
            if (isDocDateExistsInTable(docTableName)) {
                readyQuery = "select * from " + docTableName + " where DocDate BETWEEN '" + selectedDates.get(0) + "' AND '" + selectedDates.get(1) + "'";
                foundedTableWithDocDate.add(docTableName);
                foundedTablesCount++;
            }else{
                readyQuery = "select * from " + docTableName + " where ChID IN (select CHiD from " + foundedTableWithDocDate.get(foundedTablesCount-1) + " where DocDate BETWEEN '" + selectedDates.get(0) + "' AND '" + selectedDates.get(1) + "')";
            }
        }
        else
        {
            String ChID = "";
            for (int i=0;i<selectedDocsByID.size();i++){
                if (selectedDocsByID.size()==1){
                    ChID += selectedDocsByID.get(i);
                }
                if (selectedDocsByID.size()>1 && selectedDocsByID.size()!=1){
                    ChID += selectedDocsByID.get(i)+",";
                }
                if (i==selectedDocsByID.size()-1 && selectedDocsByID.size()!=1){
                    ChID += selectedDocsByID.get(i);
                }
            }
            readyQuery = "select * from " + docTableName + " where ChID IN("+ChID+")";
        }
        return readyQuery;
    }

    /**
     * Сheking if particular table in database has column with name "DocDate"
     *
     * @param docTableName - name of table
     * @return Result true/false
     * @throws SQLException
     */
    public boolean isDocDateExistsInTable(String docTableName) throws SQLException {
        ArrayList<String> columnNames = new ArrayList<String>();
        Connection conn = DriverManager.getConnection(ConnectionController.getConnParams);
        String query = "select COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME ='" + docTableName + "'";
        Statement stat = null;
        ResultSet rs = null;
        String searchedValue = "DocDate";
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            while (rs.next()) {
                int i = 1;
                while (i <= colCount) {
                    columnNames.add(rs.getString(i++));
                }
            }
            conn.commit();
            for (String value : columnNames) {
                if (value.contentEquals(searchedValue))
                    return true;
            }

        } catch (SQLException e) {
            System.out.println(e);

        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
        return false;
    }

    /**
     * Returns document name by document code
     *
     * @param docCode
     * @return String - document code
     * @throws SQLException
     */
    public String getDocName(String docCode) throws SQLException {
        Connection conn = DriverManager.getConnection(ConnectionController.getConnParams);
        String query = "select DocName from z_Docs where DocCode=" + docCode;
        Statement stat = null;
        ResultSet rs = null;
        String docName = "";
        HashMap<String, String> tableList = new HashMap<String, String>();
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
        return docName;
    }

    /**
     * Returns table description by particular table name of document
     *
     * @param tableName
     * @return String - document code
     * @throws SQLException
     */
    public String getTableCode(String tableName) throws SQLException {
        Connection conn = DriverManager.getConnection(ConnectionController.getConnParams);
        String query = "select TableCode from z_Tables where TableName ='" + tableName + "'";
        Statement stat = null;
        ResultSet rs = null;
        String getTableName = "";
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(query);

            while (rs.next()) {
                getTableName = String.valueOf(rs.getInt(1));
            }
            conn.commit();

            return getTableName;
        } catch (SQLException e) {
            System.out.println(e);

        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
        return getTableName;
    }

    /**
     * Returns table description by particular table name of document
     *
     * @param tableName
     * @return String - document code
     * @throws SQLException
     */
    public String getTableDesc(String tableName) throws SQLException {
        Connection conn = DriverManager.getConnection(ConnectionController.getConnParams);
        String query = "select TableDesc from z_Tables where TableName ='" + tableName + "'";
        Statement stat = null;
        ResultSet rs = null;
        String getTableDesc = "";
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(query);

            while (rs.next()) {
                getTableDesc = rs.getString(1);
            }
            conn.commit();

            return getTableDesc;
        } catch (SQLException e) {
            System.out.println(e);

        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
        return getTableDesc;
    }

    /**
     * Generating list of table names of particular document type
     *
     * @param docCode - Document code number
     * @return ArrayList of table names
     * @throws SQLException
     */
    public ArrayList<String> getAllTableNames(String docCode) throws SQLException {
        ArrayList<String> tableNames = new ArrayList<String>();
        Connection conn = DriverManager.getConnection(ConnectionController.getConnParams);
        String query = "select TableName from z_Tables where DocCode=" + docCode;
        Statement stat = null;
        ResultSet rs = null;
        String tableName;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(query);
            while (rs.next()) {
                tableName = rs.getString(1);
                tableNames.add(tableName);
            }
            conn.commit();

            return tableNames;
        } catch (SQLException e) {
            System.out.println(e);

        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
        return tableNames;
    }

    public ArrayList<String> getAllDocCodes() throws SQLException {
        ArrayList<String> tableNames = new ArrayList<String>();
        Connection conn = DriverManager.getConnection(ConnectionController.getConnParams);
        String query = "select DocCode from z_Docs where DocCatCode=1";
        Statement stat = null;
        ResultSet rs = null;
        String docCodes;
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            rs = stat.executeQuery(query);
            while (rs.next()) {
                docCodes = rs.getString(1);
                tableNames.add(docCodes);
            }
            conn.commit();

            return tableNames;
        } catch (SQLException e) {
            System.out.println(e);

        } finally {
            JDBC_Utils.closeQuietly(rs);
            JDBC_Utils.closeQuietly(conn);
            JDBC_Utils.closeQuietly(stat);
        }
        return tableNames;
    }


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


}


