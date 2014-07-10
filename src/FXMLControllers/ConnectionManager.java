package FXMLControllers;

import ConnectionManager.ErrorMsg;
import ConnectionManager.JDBC_Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;


public class ConnectionManager extends Pane {


    @FXML
    TextField dbInstance;
    @FXML
    TextField dbIP;
    @FXML
    TextField dbName;
    @FXML
    TextField dbLogin;
    @FXML
    PasswordField dbPassword;
    @FXML
    Button dbTryConnectionBtn;
    @FXML
    Button dbConnectBtn;
    /*
    * Stub
    * */

    static String dbInstance_ = "GMSNORDGAIMER";
    static String dbIP_ = "localhost";
    static String dbName_ = "GMSSample38";
    static String dbLogin_ = "sa";
    static String dbPass_ = "QQQqqq123";
    public static String getConnParams =JDBC_Utils.jdbcUrlBuilder(dbInstance_, dbIP_, dbName_, dbLogin_, dbPass_);


    public void connectToDB(ActionEvent event) {
        /*
        * For real work.
        * */
        //private String dbInstance_ = dbInstance.getText();
        //private String dbIP_ = dbIP.getText();
        //private String dbName_ = dbName.getText();
        //private String dbLogin_ = dbLogin.getText();
        //private String dbPass_ = dbPassword.getText();
        getConnParams =JDBC_Utils.jdbcUrlBuilder(dbInstance_, dbIP_, dbName_, dbLogin_, dbPass_);
        try {
            Connection conn = DriverManager.getConnection(getConnParams);
            ((Node) (event.getSource())).getScene().getWindow().hide();
            Pane root  = FXMLLoader.load(getClass().getClassLoader().getResource("mainframe.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Выгрузка в XML");
            stage.setScene(new Scene(root, 281 , 198));
            stage.setResizable(false);
            stage.show();
        } catch (SQLException e) {
            new ErrorMsg("Ошибка",e.toString(),false);
            //TODO Change status window.
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
