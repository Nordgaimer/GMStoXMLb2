package FXMLControllers;

import ConnectionManager.ErrorMsge;
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


public class ConnectionController extends Pane {


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
    /*
    * Stub for fast connection (TESTING MODE)
    */
//    static String dbInstance_ = "MS2008R2E";
//    static String dbIP_ = "localhost";
//    static String dbName_ = "GMSSample38";
//    static String dbLogin_ = "sa";
//    static String dbPass_ = "QQQqqq123";
//    =JDBC_Utils.jdbcUrlBuilder(dbInstance_,dbIP_,dbName_,dbLogin_,dbPass_);
//

    public static String getConnParams;

    public void connectToDB(ActionEvent event) {
        /*
        * For real work.
        * Connection properties
        * */
        String dbInstance_ = dbInstance.getText();
        String dbIP_ = dbIP.getText();
        String dbName_ = dbName.getText();
        String dbLogin_ = dbLogin.getText();
        String dbPass_ = dbPassword.getText();
        getConnParams = JDBC_Utils.jdbcUrlBuilder(dbInstance_, dbIP_, dbName_, dbLogin_, dbPass_);
        try {
            Connection conn = DriverManager.getConnection(getConnParams);
            ((Node) (event.getSource())).getScene().getWindow().hide();
            Pane root = FXMLLoader.load(getClass().getClassLoader().getResource("mainframe.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Выгрузка в XML");
            stage.setScene(new Scene(root, 281, 198));
            stage.setResizable(false);
            stage.show();
        } catch (SQLException e) {
            new ErrorMsge("Ошибка: " + e.toString());
            //TODO Change status window.
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
