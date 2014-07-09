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

    public static String getConnParams;

    public void connectToDB(ActionEvent event) {
        /*
        * Stub
        * */
        String dbInstanceConverted = "GMSNORDGAIMER";
        String dbIPConverted = "localhost";
        String dbNameConverted = "GMSSample38";
        String dbLoginConverted = "sa";
        String dbPassConverted = "QQQqqq123";
        /*
        * For real work.
        * */
        //String dbInstanceConverted = dbInstance.getText();
        //String dbIPConverted = dbIP.getText();
        //String dbNameConverted = dbName.getText();
        //String dbLoginConverted = dbLogin.getText();
        // String dbPassConverted = dbPassword.getText();
        getConnParams =JDBC_Utils.jdbcUrlBuilder(dbInstanceConverted,dbIPConverted,dbNameConverted,dbLoginConverted,dbPassConverted);
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
