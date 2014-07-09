package ConnectionManager;

import java.sql.*;

/**
 * Created by Елена on 06.07.2014.
 */
public class JDBC_Utils {
    private static final String DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";


    static {
        initDriver();
    }
    public static String jdbcUrlBuilder (String dbInstance, String dbIP, String dbName, String dbUser, String dbPassword  ){
       String JDBC_URL = "jdbc:sqlserver://"+dbIP+";instance="+dbInstance+";databaseName="+dbName+";user="+dbUser+";password="+dbPassword;
       return JDBC_URL;
    }


    public static void rollbackQuietly(Connection connection) {
        if (connection!=null){
            try {
                connection.rollback();
            } catch (SQLException e) {
                //NOP
            }
        }

    }
    public static void closeQuietly(Connection connection){

        try {
            connection.close();
        } catch (SQLException e) {
            //NOP
        }

    }
    public static void closeQuietly(ResultSet resultSet){

        try {
            resultSet.close();
        } catch (SQLException e) {
            //NOP
        }

    }
    public static void closeQuietly(Statement statement){

        try {
            statement.close();
        } catch (SQLException e) {
            //NOP
        }

    }
    private static boolean initialized;
    public static void initDriver() {
        if (!initialized){
            try {
                Class.forName(DRIVER_CLASS_NAME);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException ("Can't initialize driver");
            }
        }
        initialized=true;
    }


}
